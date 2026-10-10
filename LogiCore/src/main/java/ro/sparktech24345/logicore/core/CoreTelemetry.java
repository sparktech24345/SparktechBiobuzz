package ro.sparktech24345.logicore.core;

import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ro.sparktech24345.logicore.utils.TickInterval;

/**
 * Thread-safe async telemetry implementation using a ScheduledExecutorService.
 * Runs telemetry updates on a background thread every 33ms, completely removing
 * telemetry overhead from the main loop thread.
 */
public class CoreTelemetry implements CoreModule {
    private final MultipleTelemetry tel;

    public Telemetry telemetry() {
        return tel;
    }

    private final TickInterval ticker;

    // Buffer thread-safe pentru date, medii și linii
    private final ConcurrentHashMap<String, Object> dataBuffer = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AvgAccumulator> avgBuffer = new ConcurrentHashMap<>();
    private final List<String> lineBuffer = new CopyOnWriteArrayList<>();

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;

    public CoreTelemetry(double interval, double delay, Telemetry... telemetryList) {
        this.tel = new MultipleTelemetry(telemetryList);
        this.ticker = new TickInterval(interval < 1 ? 1 : interval, delay < 0 ? 0 : delay);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public CoreTelemetry(Telemetry... telemetryList) {
        this(5, 0, telemetryList);
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
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        startScheduler();
    }

    private synchronized void startScheduler() {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            scheduledTask = scheduler.scheduleWithFixedDelay(this::flushTelemetry, 0, 33, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void loopCore() {
        // Opțional: logică per frame dacă e necesar
    }

    /**
     * No-op on main thread: background ScheduledExecutorService handles flushing every 33ms.
     */
    @Override
    public void writeCore() {
    }

    private void flushTelemetry() {
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
        } catch (Exception e) {
            e.printStackTrace(System.out);
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
    }

    /**
     * Închide executor-ul la oprirea OpMode-ului pentru a preveni memory leaks.
     */
    public void stopCore() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
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