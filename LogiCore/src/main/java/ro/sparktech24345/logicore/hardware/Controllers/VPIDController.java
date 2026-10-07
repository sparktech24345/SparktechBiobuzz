package ro.sparktech24345.logicore.hardware.Controllers;

public class VPIDController extends PIDController {
    public double Kf; /// f as a linear additive as f(target) = x
    public double Ks; /// an additive constant
    public void setConstants(double p, double i, double d, double f, double s) {
        super.setConstants(p, i, d);
        this.Kf = f;
        this.Ks = s;
    }
    public void setConstants(VPIDCoeficients coef){
        if(coef == null) return;
        this.setConstants(coef.Kp , coef.Ki , coef.Kd , coef.Kf, coef.Ks);
    }
    public VPIDCoeficients getConstants(){
        return new VPIDCoeficients(this.getKf(),this.getKi(),this.getKd(), getKf(), getKs());
    }
    public VPIDController(double p, double i, double d, double f, double s){
        setConstants(p,i,d,f,s);
    }

    @Override
    public double calculate(double target, double currentVelocity) {
        return super.calculate(target, currentVelocity) + target * Kf + Ks * Math.signum(target);
    }

    public double getKf() {
        return Kf;
    }

    public double getKs() {
        return Ks;
    }


    public class VPIDCoeficients{
        public VPIDCoeficients(double p, double i, double d, double f, double s) {
            this.Kp = p;
            this.Ki = i;
            this.Kd = d;
            this.Kf = f;
            this.Ks = s;
        }
        public double Kp;
        public double Kd;
        public double Ki;
        public double Kf;
        public double Ks;
    }


}
