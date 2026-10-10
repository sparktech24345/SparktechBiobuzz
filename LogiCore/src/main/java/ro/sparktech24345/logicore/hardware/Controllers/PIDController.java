package ro.sparktech24345.logicore.hardware.Controllers;

public class PIDController {
    private double kp = 0;
    private double ki = 0;
    private double kd = 0;
    private double kf = 0;

    private double integral = 0;
    private double lastError = 0;
    private double lastVelocity = 0;
    private double lastPos = 0;
    private double error;
    private long lastTime = System.currentTimeMillis();

    public PIDController(double p, double i, double d) {
        setConstants(p, i, d);
    }

    public PIDController(double p, double i, double d, double f) {
        setPIDFConstants(p, i, d, f);
    }

    public PIDController() {
    }

    public void setConstants(double p, double i, double d) {
        kp = p;
        ki = i;
        kd = d;
    }

    public void setPIDFConstants(double p, double i, double d, double f) {
        kp = p;
        ki = i;
        kd = d;
        kf = f;
    }

    public double getIntegralSum() {
        return integral;
    }

    public void setIntegralSum(double sum) {
        integral = sum;
    }

    public double getKi() {
        return ki;
    }

    public double getKf() {
        return kf;
    }

    public double getKp() {
        return kp;
    }

    public double getKd() {
        return kd;
    }

    public double calculate(double target, double current) {
        long now = System.currentTimeMillis();
        double deltaTime = (now - lastTime) / 1000.0;  // in seconds
        lastTime = now;

        error = target - current;

        // Integral term (accumulated error)
        integral += error * deltaTime;

        // Derivative term (rate of change of error)
        double derivative = (deltaTime > 0) ? (error - lastError) / deltaTime : 0;
        lastError = error;

        // PID output
        double output = (kp * error) + (ki * integral) + (kd * derivative) + (kf * Math.signum(error) * (Math.abs(error) > 0.3 ? 1 : 0));

        return output;
    }

}