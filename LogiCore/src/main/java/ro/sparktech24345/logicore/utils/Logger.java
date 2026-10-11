package ro.sparktech24345.logicore.utils;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.PrintStream;

import ro.sparktech24345.logicore.core.CoreOpMode;
import ro.sparktech24345.logicore.core.CoreTelemetry;

public class Logger {
    private final CoreTelemetry coreTelemetry;
    private final Telemetry tel;
    private final PrintStream fd;

    public PrintStream fd() {
        return fd;
    }

    public Logger() {
        this(CoreOpMode.instance() != null ? CoreOpMode.instance().telemetry() : null, System.out);
    }

    public Logger(CoreTelemetry coreTelemetry) {
        this(coreTelemetry, System.out);
    }

    public Logger(CoreTelemetry coreTelemetry, PrintStream output) {
        this.coreTelemetry = coreTelemetry;
        this.tel = coreTelemetry != null ? coreTelemetry.telemetry() : null;
        this.fd = output;
    }

    public Logger(Telemetry tele) {
        this(tele, System.out);
    }

    public Logger(Telemetry tele, PrintStream output) {
        this.coreTelemetry = null;
        this.tel = tele;
        this.fd = output;
    }

    public Logger(PrintStream output) {
        this(CoreOpMode.instance() != null ? CoreOpMode.instance().telemetry() : null, output);
    }

    public void write(String caption, String fmt, Object... data) {
        if (coreTelemetry != null) {
            coreTelemetry.addData(caption, fmt, data);
        } else if (tel != null) {
            synchronized (tel) {
                tel.addData(caption, fmt, data);
            }
        }
        if (fd != null) {
            fd.printf(caption + fmt + "\n", data);
        }
    }

    public void write(String caption, Object data) {
        if (coreTelemetry != null) {
            coreTelemetry.addData(caption, data);
        } else if (tel != null) {
            synchronized (tel) {
                tel.addData(caption, data);
            }
        }
        if (fd != null) {
            fd.println(caption + ": " + data);
        }
    }

    public void write(String str) {
        if (coreTelemetry != null) {
            coreTelemetry.addLine(str);
        } else if (tel != null) {
            synchronized (tel) {
                tel.addLine(str);
            }
        }
        if (fd != null) {
            fd.println(str);
        }
    }
}
