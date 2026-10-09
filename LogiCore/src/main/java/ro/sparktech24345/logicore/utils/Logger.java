package ro.sparktech24345.logicore.utils;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.PrintStream;

import ro.sparktech24345.logicore.core.CoreOpMode;

public class Logger {
    Telemetry tel;
    PrintStream fd;

    public PrintStream fd() {
        return fd;
    }

    public Logger() {
        this(CoreOpMode.instance().telemetry().telemetry(), System.out);
    }

    public Logger(Telemetry tele) {
        this(tele, System.out);
    }

    public Logger(PrintStream output) {
        this(CoreOpMode.instance().telemetry().telemetry(), output);
    }

    public Logger(Telemetry tele, PrintStream output) {
        this.tel = tele;
        this.fd = output;
    }

    public void write(String caption, String fmt, Object... data) {
        tel.addData(caption, fmt, data);
        fd.printf(caption + fmt + "\n", data);
    }

    public void write(String caption, Object data) {
        tel.addData(caption, data);
        fd.println(caption + ": " + data);
    }

    public void write(String str) {
        tel.addLine(str);
        fd.println(str);
    }
}
