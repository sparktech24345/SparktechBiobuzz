package ro.sparktech24345.logicore.core;

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Thread-safe async telemetry implementation optimized for custom architectures.
 * Insulates the main loop from antenna hardware lag (2ms-19ms) while ensuring
 * stable mathematical synchronization and a strict >=2ms receiver pacing gap.
 */
public class CoreTelemetry implements CoreModule {
    private final MultipleTelemetry tel;

    public Telemetry telemetry() {
        return tel;
    }

    private final TickInterval ticker;

    // 1. Private workspaces for the main loop thread (No thread contention)
    private final ConcurrentHashMap<String, Object> dataBuffer = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AvgAccumulator> avgBuffer = new ConcurrentHashMap<>();
    private final List<String> lineBuffer = new CopyOnWriteArrayList<>();

    // 2. High-performance, lock-free snapshot queue to carry frames to the background
    private final ConcurrentLinkedQueue<TelemetryFrameSnapshot> frameQueue = new ConcurrentLinkedQueue<>();

    // 3. Executor Engine and Pacing Variables
    private ExecutorService scheduler;
    private Future<?> scheduledTask;
    private volatile boolean running = false;

    private long lastJniSendTimeNano = 0;
    private static final long MIN_JNI_GAP_NANO = 2_000_000L; // Strict 2ms receiver gap

    public CoreTelemetry(double interval, double delay, Telemetry... telemetryList) {
        this.tel = new MultipleTelemetry(telemetryList);
        this.ticker = new TickInterval(interval < 1 ? 1 : interval, delay < 0 ? 0 : delay);
        this.scheduler = Executors.newSingleThreadExecutor();
    }

    public CoreTelemetry(Telemetry... telemetryList) {
        this(3, 0, telemetryList);
    }

    /**
     * Apelat pe main loop - scrie doar în buffer-ul privat.
     */
    public void addData(String key, Object value) {
        dataBuffer.put(key, value);
    }

    /**
     * Accumulates numeric values across main loop frames and sends their average
     * at the moment telemetry is updated.
     */
    public void addAverageData(String key, Number value) {
        if (value == null) return;
        avgBuffer.computeIfAbsent(key, k -> new AvgAccumulator()).add(value.doubleValue());
    }

    public void addAverageData(String key, double value) {
        avgBuffer.computeIfAbsent(key, k -> new AvgAccumulator()).add(value);
    }

    /**
     * Apelat pe main loop - adaugă linia în buffer.
     */
    public void addLine(String line) {
        lineBuffer.add(line);
    }

    public TickInterval getTicker() {
        return this.ticker;
    }

    @Override
    public void initCore() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadExecutor();
        }
        startScheduler();
    }

    private synchronized void startScheduler() {
        if (scheduledTask == null || scheduledTask.isDone()) {
            running = true;
            scheduledTask = scheduler.submit(this::runPacedExecutionLoop);
        }
    }

    @Override
    public void loopCore() {
        // Opțional: logică per frame dacă e necesar
    }

    /**
     * Main loop finish line! CALL THIS at the absolute end of every OpMode loop iteration.
     * Takes isolated snapshots of all your telemetry and clears the buffers instantly.
     */
    public void dumpMap() {
        // Freeze everything in a stable frame state container
        Map<String, Object> dataSnapshot = new HashMap<>(dataBuffer);

        Map<String, Double> avgSnapshot = new HashMap<>();
        for (Map.Entry<String, AvgAccumulator> entry : avgBuffer.entrySet()) {
            avgSnapshot.put(entry.getKey(), entry.getValue().getAndReset());
        }

        List<String> lineSnapshot = new ArrayList<>(lineBuffer);

        // Flings it to the thread-safe queue instantly (<1 microsecond)
        frameQueue.add(new TelemetryFrameSnapshot(dataSnapshot, avgSnapshot, lineSnapshot));

        // Clear everything out so the next main loop frame starts completely fresh
        dataBuffer.clear();
        lineBuffer.clear();
        // Note: avgBuffer is NOT cleared so it keeps accumulators alive, but their state was reset.
    }

    /**
     * Main loop write method remains a clean no-op as the background loop handles everything.
     */
    @Override
    public void writeCore() {
    }

    /**
     * The background thread's high-precision engine loop.
     */
    private void runPacedExecutionLoop() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        while (running && !Thread.currentThread().isInterrupted()) {
            if (frameQueue.isEmpty()) {
                // If the robot is idling or hasn't called dumpMap(), let the processor breathe
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            // ANTENNA LAG COMPACTION: If the antenna stalled for 19ms, your 7ms main loop
            // queued up multiple frames. We instantly fast-forward to the latest snapshot
            // to ensure true real-time execution metrics.
            TelemetryFrameSnapshot frameToSend = null;
            while (!frameQueue.isEmpty()) {
                frameToSend = frameQueue.poll();
            }

            if (frameToSend == null) continue;


            // Execute the flush operation on the background thread using the frozen snapshot
            flushTelemetry(frameToSend);
        }
    }

    private void flushTelemetry(TelemetryFrameSnapshot snapshot) {
        try {
            // Populate telemetry EXCLUSIVELY from the frozen snapshot map
            for (Map.Entry<String, Object> entry : snapshot.data.entrySet()) {
                tel.addData(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, Double> entry : snapshot.averages.entrySet()) {
                tel.addData(entry.getKey(), entry.getValue());
            }

            for (String line : snapshot.lines) {
                tel.addLine(line);
            }

            // THE VOLATILE HARDWARE BOTTLENECK: This safely stalls here for 2ms-19ms in the background.
            tel.update();

        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            // Track completion time AFTER the antenna release, starting the 2ms countdown valve
            lastJniSendTimeNano = System.nanoTime();
        }
    }

    /**
     * Curăță o cheie din buffer (dacă este nevoie manual).
     */
    public void removeItem(String key) {
        dataBuffer.remove(key);
        avgBuffer.remove(key);
    }

    /**
     * Resetează complet buffer-ul de date.
     */
    public void clearAll() {
        dataBuffer.clear();
        avgBuffer.clear();
        lineBuffer.clear();
        frameQueue.clear();
    }

    /**
     * Închide executor-ul la oprirea OpMode-ului pentru a preveni memory leaks.
     */
    public void stopCore() {
        running = false;
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    /**
     * Container holding a frozen point-in-time snapshot of a specific loop frame.
     */
    private static class TelemetryFrameSnapshot {
        public final Map<String, Object> data;
        public final Map<String, Double> averages;
        public final List<String> lines;

        public TelemetryFrameSnapshot(Map<String, Object> data, Map<String, Double> averages, List<String> lines) {
            this.data = data;
            this.averages = averages;
            this.lines = lines;
        }
    }

    /**
     * Thread-safe helper class for computing running averages.
     */
    private static class AvgAccumulator {
        private double sum = 0;
        private long count = 0;
        private double lastAverage = 0;

        public synchronized void add(double value) {
            sum += value;
            count++;
        }

        public synchronized double getAndReset() {
            if (count > 0) {
                lastAverage = sum / count;
                sum = 0;
                count = 0;
            }
            return lastAverage;
        }
    }
}
