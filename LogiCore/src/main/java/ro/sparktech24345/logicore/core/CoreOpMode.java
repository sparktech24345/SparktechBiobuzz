package ro.sparktech24345.logicore.core;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.photon.PhotonCore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.anygeneric.blazeftc.BlazeDummyPlug;
import dev.anygeneric.blazeftc.BlazeFTC;
import dev.anygeneric.blazeftc.DummyPlugOpMode;
import ro.sparktech24345.logicore.commands.BaseCommand;
import ro.sparktech24345.logicore.config.IsHardware;
import ro.sparktech24345.logicore.config.Keys;
import ro.sparktech24345.logicore.config.MotorMap;
import ro.sparktech24345.logicore.events.EventBus;
import ro.sparktech24345.logicore.hardware.CoreVoltageSensor;
import ro.sparktech24345.logicore.pedro.CoreFollower;
import ro.sparktech24345.logicore.pedro.FollowerConstants;
import ro.sparktech24345.logicore.utils.Benchmark;
import ro.sparktech24345.logicore.utils.DriveTrain;
import ro.sparktech24345.logicore.utils.Logger;
import ro.sparktech24345.logicore.utils.MathUtils;

/**
 * Base class for all LogiCore OpModes. Provides unified lifecycle management,
 * module system, command queuing, and hardware access.
 */
public abstract class CoreOpMode extends DummyPlugOpMode {
    public CoreOpMode(OpModeConfig config) {
        this.config = config;
        this.coreFollower = new CoreFollower<>(config.followerConstants.get(), config.type.get() == OpModeType.TELEOP ? null : config.startPose.get());
    }

    private OpModeConfig config;

    public OpModeConfig config() {
        return this.config;
    }

    public void config(OpModeConfig config) {
        this.config = config;
    }

    private final ModuleHandler cHubModules = new ModuleHandler();
    private final ModuleHandler eHubModules = new ModuleHandler();
    private final ModuleHandler internalModules = new ModuleHandler();
    private final ModuleHandler independentModules = new ModuleHandler();
    private final CoreQueuer queuer = new CoreQueuer();


    /**
     * Global instance accessor for hardware components that need OpMode context
     */
    private static CoreOpMode instance = null;

    public static CoreOpMode instance() {
        return instance;
    }

    /**
     * Current stage of the OpMode lifecycle
     */
    protected GameStage stage = GameStage.INIT;

    public GameStage stage() {
        return this.stage;
    }

    /**
     * Telemetry system with update throttling and multi-output support
     */
    protected CoreTelemetry coreTelemetry;

    public CoreTelemetry telemetry() {
        return this.coreTelemetry;
    }

    protected final CoreFollower<FollowerConstants> coreFollower;

    public CoreFollower<FollowerConstants> follower() {
        return coreFollower;
    }

    protected DriveTrain driveTrain;

    public DriveTrain driveTrain() {
        return driveTrain;
    }

    /**
     * Gamepad input processing with button state tracking
     */
    protected CoreGamepad gamepad = new CoreGamepad();

    public CoreGamepad gamepad() {
        return gamepad;
    }

    protected Logger logger;

    public Logger logger() {
        return logger;
    }

    /**
     * Voltage monitoring for battery health tracking
     */
    protected CoreVoltageSensor voltageSensor = new CoreVoltageSensor();

    public CoreVoltageSensor voltageSensor() {
        return voltageSensor;
    }

    /**
     * Control Hub and Expansion Hub handlers for bulk reads
     */
    protected CoreHubs hubs = new CoreHubs();

    public CoreHubs hubs() {
        return hubs;
    }

    /**
     * Install a module into the system with priority-based execution order
     */
    public <T extends CoreModule> T install(T module, double priority) {
        if (module instanceof IsHardware) {
            int k = ((IsHardware) module).key();
            switch (Keys.keyId(k)) {
                case 2:
                    eHubModules.install(module, priority);
                    break;
                case 173:
                    cHubModules.install(module, priority);
                    break;
                default:
                    independentModules.install(module, priority);
                    break;
            }
        } else independentModules.install(module, priority);
        return module;
    }

    /**
     * Execute a command immediately (bypasses queue)
     */
    final public void execute(BaseCommand... commands) {
        for (BaseCommand cmd : commands) queuer.execute(cmd);
    }

    /**
     * Queue a command for sequential execution
     */
    final public void queue(BaseCommand... commands) {
        for (BaseCommand cmd : commands) queuer.queue(cmd);
    }

    /**
     * Clear all pending and executing commands
     */
    final public void clear() {
        queuer.clear();
    }

    public static ExecutorService executor;

    public static void schedule(Runnable run) {
        if (run == null) return;
        executor.execute(run);
//        run.run();
    }

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
        if (stage != GameStage.INIT) Benchmark.of("inputs", () -> {
            internalModules.readCore();
//                if (config.performanceEngine.get() != PerformanceEngine.BLAZE) {
            cHubModules.readCore();
            eHubModules.readCore();
//                }
            independentModules.readCore();
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

        if (stage != GameStage.INIT) Benchmark.of("outputs", () -> {
            Benchmark.of("internal modules output", internalModules::writeCore);
            Benchmark.of("motors and servos output", () -> {
                cHubModules.writeCore();
                eHubModules.writeCore();
                if (config.performanceEngine.get() == PerformanceEngine.BLAZE)
                    MotorMap.write();
            });
            Benchmark.of("independent modules output", independentModules::writeCore);
        });
    }

    public void setMotorPower(DcMotorEx motor, double power) {
        setMotorPower(
                Keys.key(motor.getController().getConnectionInfo(), motor.getPortNumber()),
                motor, power);
    }

    public void setMotorPower(int key, DcMotorEx motor, double power) {
        if (config.performanceEngine.get() == PerformanceEngine.BLAZE && !config.accelerateMotors.get()) {
            double targetPower = MathUtils.clip(power, -1, 1);
            MotorMap.set(key, targetPower);
            MotorMap.clear();
        } else
            motor.setPower(MathUtils.clip(power, -1, 1)); // reverse should by itself make it negative
    }

    final public void initCore() {
        instance = this;
        // ====== GAMEPAD + TELEMETRY SETUP =======
        // executor = Executors.newSingleThreadExecutor();

        // ============ PERFORMANCE ENGINE SETUP ============
        switch (config.performanceEngine.get()) {
            case PHOTON: {
                PhotonCore.experimental.setMaximumParallelCommands(6);
                PhotonCore.PARALLELIZE_SERVOS = true;
                PhotonCore.enable();
                break;
            }
            case BLAZE: {
                BlazeDummyPlug.initializeBlazeFTC(hardwareMap);
                if (config.accelerateMotors.get())
                    BlazeDummyPlug.engageMotorAccel(hardwareMap); // maybe not needed but use getMotor wrapper also
//                BlazeDummyPlug.engageBulkReadAcceleration(hardwareMap, Hub.CtrlHub, 1, () -> {
//                    if (stage != GameStage.INIT) cHubModules.readCore();
//                    System.out.println("Read on control hub" + Hub.CtrlHub);
//                    return Unit.INSTANCE;
//                });
//                BlazeDummyPlug.engageBulkReadAcceleration(hardwareMap, Hub.ExHub, 1, () -> {
//                    if (stage != GameStage.INIT) eHubModules.readCore();
//                    System.out.println("Read on expansion hub" + Hub.ExHub);
//                    return Unit.INSTANCE;
//                });
                break;
            }
            case NONE: {
                // Default OpMode behavior
                break;
            }
        }

        // init stuff
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor();
        }

        internalModules.install(gamepad, 1);
        if (config.useFollower.get()) internalModules.install(coreFollower, 1);
        if (config.useDriveTrain.get() && config.type.get() == OpModeType.TELEOP)
            driveTrain = internalModules.install(new DriveTrain(gamepad1), 1);
        internalModules.install(coreTelemetry, 1);
        if (config.useVoltageSensor.get())
            internalModules.install(voltageSensor, 1);
        internalModules.install(hubs, Float.POSITIVE_INFINITY);
        internalModules.install(queuer, 1);

        // ============================ EXECUTING THE USER WRITTEN CODE ============================
        update(this::onInit);
        stage = GameStage.INIT_LOOP;
    }

    final public void init_loopCore() {
        logger.write("stage", stage);
        update(this::onInitLoop);
    }

    final public void startCore() {
        stage = GameStage.START;
        update(this::onStart);
        stage = GameStage.LOOP;
    }

    final public void loopCore() {
        logger.write("stage", stage);
        update(this::onLoop);
    }

    final public void stopCore() {
        stage = GameStage.STOP;
        update(this::onStop);
        EventBus.cleanup();
        // instance = null;
        executor.shutdownNow();
        executor = null;
    }

    /**
     * User-defined initialization logic - called once during init stage
     */
    abstract public void onInit();

    /**
     * Optional user logic for init_loop stage - called repeatedly before start
     */
    public void onInitLoop() {
    }

    /**
     * Optional user logic for start stage - called when transitioning to running
     */
    public void onStart() {
    }

    /**
     * User-defined main loop logic - called every cycle during running
     */
    abstract public void onLoop();

    /**
     * Optional user cleanup logic - called when OpMode stops
     */
    public void onStop() {
    }


    @Override
    public void runOpMode() {
        try {
            coreTelemetry = new CoreTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            logger = new Logger(coreTelemetry.telemetry(), config.fd.get());
            logger.write("STARTED USING", config.performanceEngine.get());


            if (config.performanceEngine.get() == PerformanceEngine.BLAZE) super.runOpMode();
            /// RUNNING CODE WITHOUT BLAZE
            else {
                logger.write("Entering OpMode");

                initCore();
                while (opModeInInit()) this.init_loopCore();
                waitForStart();

                if (opModeIsActive()) {
                    startCore();
                    while (opModeIsActive()) this.loopCore();
                }
                stopCore();
                logger.write("Exiting OpMode");
            }
        } catch (Throwable e) {
            logger.write("Error in OpMode!!!");
            logger.write(e.getMessage());
            e.printStackTrace(logger.fd());
            throw e;
        } finally {
            executor.shutdownNow();
            instance = null;
        }
    }

    public void runOpModeInBlaze() {
        try {
            long targetMs = 0;
            logger.write("Entering OpMode");
            initCore();
            while (opModeInInit()) maintainLoopRate(targetMs, this::init_loopCore);
            waitForStart();

            if (opModeIsActive()) {
                if (config.performanceEngine.get() == PerformanceEngine.BLAZE)
                    BlazeFTC.run(0);
                startCore();
                while (opModeIsActive()) maintainLoopRate(targetMs, this::loopCore);
            }
            stopCore();
            logger.write("Exiting OpMode");
        } catch (Throwable e) {
            logger.write("Error in OpMode!!!");
            logger.write(e.getMessage());
            e.printStackTrace(logger.fd());
            throw e;
        } finally {
            executor.shutdownNow();
            instance = null;
        }
    }

    /**
     * Runs the body block and sleeps for any remaining time in the target cycle.
     */
    private void maintainLoopRate(long targetMs, Runnable block) {
        long startTime = System.currentTimeMillis();
        block.run();
        if (targetMs <= 0) return;
        long elapsedTime = System.currentTimeMillis() - startTime;
        long sleepTime = targetMs - elapsedTime;

        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.write(e.getMessage());
                e.printStackTrace(logger.fd());
            }
        }
    }
}
