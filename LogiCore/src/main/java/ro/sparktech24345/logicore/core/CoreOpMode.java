package ro.sparktech24345.logicore.core;

import static dev.anygeneric.blazeftc.BlazeDummyPlug.closeBlazeFTC;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.photon.PhotonCore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.anygeneric.blazeftc.DummyPlugOpMode;
import ro.sparktech24345.logicore.commands.BaseCommand;
import ro.sparktech24345.logicore.config.Hubs;
import ro.sparktech24345.logicore.config.IsHardware;
import ro.sparktech24345.logicore.hardware.CoreVoltageSensor;
import ro.sparktech24345.logicore.pedro.CoreFollower;
import ro.sparktech24345.logicore.pedro.FollowerConstants;
import ro.sparktech24345.logicore.utils.Benchmark;
import ro.sparktech24345.logicore.utils.DriveTrain;

/**
 * Base class for all LogiCore OpModes. Provides unified lifecycle management,
 * module system, command queuing, and hardware access.
 */
public abstract class CoreOpMode extends DummyPlugOpMode implements CommandQueuer {
    public CoreOpMode(OpModeConfig config) {
        this.config = config;
        this.coreFollower = new CoreFollower<>(config.followerConstants.get(), config.type.get() == OpModeType.TELEOP ? null : config.startPose.get());
    }

    private OpModeConfig config;

    private final ModuleHandler cHubModules = new ModuleHandler();
    private final ModuleHandler eHubModules = new ModuleHandler();
    private final ModuleHandler internalModules = new ModuleHandler();
    private final ModuleHandler independentModules = new ModuleHandler();
    private final CoreQueuer queuer = new CoreQueuer();


        /** Global instance accessor for hardware components that need OpMode context */
        private static CoreOpMode instance = null;
        public static CoreOpMode getInstance() { return instance; }

    /** Current stage of the OpMode lifecycle */
    private GameStage stage = GameStage.INIT;
    public GameStage getStage() { return this.stage; }

    /** Telemetry system with update throttling and multi-output support */
    private final CoreTelemetry coreTelemetry = new CoreTelemetry();
    public CoreTelemetry getTelemetry() { return this.coreTelemetry; }

    private CoreFollower<FollowerConstants> coreFollower;

    private ElapsedTime elt = new ElapsedTime();
    DriveTrain driveTrain;

    /** Gamepad input processing with button state tracking */
    CoreGamepad gamepad;

    /** Voltage monitoring for battery health tracking */
    CoreVoltageSensor voltageSensor = new CoreVoltageSensor();

    /** Control Hub and Expansion Hub handlers for bulk reads */
    CoreHubs hubs = new CoreHubs();

    /** Install a module into the system with priority-based execution order */
    public <T extends CoreModule> T install(Hubs hub, T module, double priority) {
        if (module instanceof IsHardware) ((IsHardware)module).getConfig().setId(hub.getId());
        switch (hub) {
            case CONTROL: cHubModules.install(module, priority);
            case EXPANSION: eHubModules.install(module, priority);
            case INDEPENDENT: independentModules.install(module, priority);
        }
        return module;
    }
    /** Execute a command immediately (bypasses queue) */
    final public void execute(BaseCommand... commands) { for (BaseCommand cmd : commands) queuer.execute(cmd); }

    /** Queue a command for sequential execution */
    final public void queue(BaseCommand... commands) { for (BaseCommand cmd : commands) queuer.queue(cmd); }

    /** Clear all pending and executing commands */
    final public void clear() { queuer.clear(); }

    private static ExecutorService executor = null;
    public static ExecutorService executor() { return executor; }

    /**
     * Central update function that coordinates all system updates in the correct order:
     * 1. Clear hardware bulk caches
     * 2. Update input systems (gamepad, sensors)
     * 3. Execute user code for current stage
     * 4. Update user modules
     * 5. Process command queue
     * 6. Update telemetry (last, to capture all changes)
     */
    private void update(Runnable fn) {
        // read control hub motors, exp motors, senzori, processing, write controlhub, motors exp, motors all servos
        Benchmark.of("inputs", () -> {
            if (stage != GameStage.INIT) {
                internalModules.readCore();
                cHubModules.readCore();
                eHubModules.readCore();
                independentModules.readCore();
            }
            // end of input stuff
        });

        Benchmark.of("processing", () -> {
            fn.run();
            internalModules.doUpdate(stage); // kinda also read // actually not really
            cHubModules.doUpdate(stage);
            eHubModules.doUpdate(stage);
            independentModules.doUpdate(stage);
            // end of processing
        });

        Benchmark.of("outputs", () -> {
            if (stage != GameStage.INIT) {
                internalModules.writeCore();
                cHubModules.writeCore();
                eHubModules.writeCore();
                independentModules.writeCore();
            }
        });
    }

    final public void initCore() {
        instance = this;
        executor = Executors.newFixedThreadPool(4);
        
        // ============ PERFORMANCE ENGINE SETUP ============
        switch (config.performanceEngine.get()) {
            case PHOTON: {
                PhotonCore.experimental.setMaximumParallelCommands(6);
                PhotonCore.PARALLELIZE_SERVOS = true;
                PhotonCore.enable();
            }
            case BLAZE: {
                config.configSetup.get().run();
                initializeBlazeFTC();
                engageMotorAcceleration();
//                engageBulkReadAcceleration(Hub.CtrlHub,1,stuffToGetEncoderData)
                //ima just do the stuff in the blaze Op mode ig
            }
            case NONE: {
                // Default OpMode behavior
            }
        }

        // ====== GAMEPAD + TELEMETRY SETUP =======
        gamepad = internalModules.install(new CoreGamepad(gamepad1, gamepad2), 1);
        if (config.useDriveTrain.get())
            driveTrain = internalModules.install(new DriveTrain(gamepad1), 1);
        coreTelemetry.addTelemetry(super.telemetry);
        internalModules.install(coreTelemetry, 1);
        internalModules.install(voltageSensor, 1);
        internalModules.install(hubs, Float.POSITIVE_INFINITY);
        internalModules.install(queuer, 1);
        if (config.useFollower.get()) internalModules.install(coreFollower, 1);

        // ============================ EXECUTING THE USER WRITTEN CODE ============================
        update(this::onInit);
        stage = GameStage.INIT_LOOP;
    }

    final public void init_loopCore() {
        update(this::init_loopCore);
    }

    final public void startCore() {
        stage = GameStage.START;
        update(this::onStart);
        stage = GameStage.LOOP;
    }

    final public void loopCore() {
        update(this::onLoop);
    }

    final public void stopCore() {
        stage = GameStage.STOP;
        update(this::onStop);
        if (config.performanceEngine.get() == PerformanceEngine.BLAZE) {
            closeBlazeFTC();
        }
        instance = null;
        executor.shutdownNow();
    }

    /** User-defined initialization logic - called once during init stage */
    abstract public void onInit();

    /** Optional user logic for init_loop stage - called repeatedly before start */
    public void onInitLoop() {}

    /** Optional user logic for start stage - called when transitioning to running */
    public void onStart() {}

    /** User-defined main loop logic - called every cycle during running */
    abstract public void onLoop();

    /** Optional user cleanup logic - called when OpMode stops */
    public void onStop() {}

    public void runOpMode() {
        if (config.performanceEngine.get() == PerformanceEngine.BLAZE) {
            runOpModeInBlaze();
        } else { // normal op mode
            try {
                initCore();
                while (opModeInInit()) {
                    init_loopCore();
                }
                waitForStart();
                if (opModeIsActive()) {
                    startCore();
                    while (opModeIsActive()) {
                        loopCore();
                    }
                }
                stopCore();
            } catch (Throwable e) {
                System.out.println("Error in OpMode!!!");
                System.out.println(e.getMessage());
            } finally {
                stopCore();
            }
        }
    }

    public void runOpModeInBlaze() {
        long targetMs = 5;

        try {
            initCore();
            while (opModeInInit()) {
                maintainLoopRate(targetMs, this::init_loopCore);
            }

            waitForStart();

            if (opModeIsActive()) {
                runBlazeFTC(0);
                startCore();

                while (opModeIsActive()) {
                    maintainLoopRate(targetMs, this::loopCore);
                }
            }
            stopCore();
        } catch (Throwable e) {
            System.out.println("Error in OpMode!!!");
            System.out.println(e.getMessage());
        } finally {
            stopCore();
        }
    }

    /**
     * Runs the body block and sleeps for any remaining time in the target cycle.
     */
    private void maintainLoopRate(long targetMs, Runnable block) {
        long startTime = System.currentTimeMillis();
        block.run();
        long elapsedTime = System.currentTimeMillis() - startTime;
        long sleepTime = targetMs - elapsedTime;

        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
