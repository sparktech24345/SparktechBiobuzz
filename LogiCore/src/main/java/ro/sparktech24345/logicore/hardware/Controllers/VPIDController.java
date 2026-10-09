package ro.sparktech24345.logicore.hardware.Controllers;

public class VPIDController extends PIDController {
    public double Kf;
    /// f as a linear additive as f(target) = x
    public double Ks;

    /// an additive constant
    public void setConstants(double p, double i, double d, double f, double s) {
        super.setConstants(p, i, d);
        this.Ks = s;
    }

    public void setConstants(VPIDCoefficients coef) {
        if (coef == null) return;
        this.setConstants(coef.Kp, coef.Ki, coef.Kd, coef.Kf, coef.Ks);
    }

    public VPIDCoefficients getConstants() {
        return new VPIDCoefficients(this.getKf(), this.getKi(), this.getKd(), getKf(), getKs());
    }

    public VPIDController(double p, double i, double d, double f, double s) {
        setConstants(p, i, d, f, s);
    }

    @Override
    public double calculate(double target, double currentVelocity) {
        return super.calculate(target, currentVelocity) + Ks * Math.signum(target);
    }

    public double getKf() {
        return Kf;
    }

    public double getKs() {
        return Ks;
    }


    public static class VPIDCoefficients {
        public VPIDCoefficients() {
            this(0, 0, 0, 0, 0);
        }

        public VPIDCoefficients(double p, double i, double d, double f, double s) {
            this.Kp = p;
            this.Ki = i;
            this.Kd = d;
            this.Kf = f;
            this.Ks = s;
        }

        public double Kp;
        public double Ki;
        public double Kd;
        public double Kf;
        public double Ks;
    }


}
