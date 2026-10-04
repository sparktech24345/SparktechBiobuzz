package ro.sparktech24345.logicore.core;

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import ro.sparktech24345.logicore.utils.Benchmark;
import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Thread-safe async telemetry implementation.
 * Uses ConcurrentHashMap as a staging buffer so main thread addData() calls
 * never collide with background thread tel.update() execution.
 */
public class CoreTelemetry implements CoreModule {
    private final MultipleTelemetry tel;
    private final TickInterval ticker;

    // Buffer thread-safe pentru date, medii și linii
    private final ConcurrentHashMap<String, Object> dataBuffer = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AvgAccumulator> avgBuffer = new ConcurrentHashMap<>();
    private final List<String> lineBuffer = new CopyOnWriteArrayList<>();

    private ExecutorService executor;
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    public CoreTelemetry(double interval, double delay, Telemetry... telemetryList) {
        this.tel = new MultipleTelemetry(telemetryList);
        this.ticker = new TickInterval(interval < 1 ? 1 : interval, delay < 0 ? 0 : delay);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public CoreTelemetry(Telemetry... telemetryList) {
        this(4, 0, telemetryList);
    }

    /**
     * Apelat pe main loop - scrie doar în buffer-ul thread-safe.
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
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    public void loopCore() {
        // Opțional: logică per frame dacă e necesar
    }

    /**
     * Submite actualizarea pe thread-ul secundar.
     * Muta datele din buffer în MultipleTelemetry doar pe thread-ul secundar.
     */
    @Override
    public void writeCore() {
        Benchmark.of("telemetry", () -> {
            if (ticker.shouldTick()) {
                if (isUpdating.compareAndSet(false, true)) {
                    executor.submit(() -> {
                        try {
                            // Populează MultipleTelemetry EXCLUSIV pe thread-ul secundar
                            for (Map.Entry<String, Object> entry : dataBuffer.entrySet()) {
                                tel.addData(entry.getKey(), entry.getValue());
                            }

                            // Calculează și trimite mediile acumulate
                            for (Map.Entry<String, AvgAccumulator> entry : avgBuffer.entrySet()) {
                                tel.addData(entry.getKey(), entry.getValue().getAndReset());
                            }

                            for (String line : lineBuffer) {
                                tel.addLine(line);
                            }

                            // Trimite telemetria către FTC Dashboard și Driver Station
                            tel.update();

                            // Curăță liniile după update (comportament similar FTC Telemetry)
                            lineBuffer.clear();
                        } finally {
                            isUpdating.set(false);
                        }
                    });
                }
            }
        });
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
    }

    /**
     * Închide executor-ul la oprirea OpMode-ului pentru a preveni memory leaks.
     */
    public void stopCore() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
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