package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.youfuns.webserver.WebServer;

import org.firstinspires.ftc.teamcode.framework.FtcOpMode;

@TeleOp(name="Web Dashboard", group="Test")
public class WebControlTeleop extends FtcOpMode {
    @Override
    public void customInit() {
        WebServer<?, ?, ?> server = null;
        int port = 9371;
        while (server == null) {
            try {
                server = WebServer.create(port);
            } catch (Exception e) {
                port++;
            }
        }
        telemetry.addLine("Port: " + port);
        telemetry.update();
        sleep(1000);

        server
                .on("/api", exchange -> {
                    exchange.enableCors();
                    String motor = exchange.getQueryParameter("motor", "fl");
                    double power = exchange.getQueryParameterAsDouble("power", 0.0);
                    try {
                        switch (motor) {
                            case "fl":
                                hardwareUtils.frontLeft.setPower(power);
                                break;
                            case "fr":
                                hardwareUtils.frontRight.setPower(power);
                                break;
                            case "bl":
                                hardwareUtils.backLeft.setPower(power);
                                break;
                            case "br":
                                hardwareUtils.backRight.setPower(power);
                                break;
                            case "id":
                                hardwareUtils.indexer.setPower(power);
                                break;
                            case "sl":
                                hardwareUtils.leftShooter.setPower(power);
                                break;
                            case "sr":
                                hardwareUtils.rightShooter.setPower(power);
                                break;
                            case "it":
                                hardwareUtils.intake.setPower(power);
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid motor: " + motor);
                        }
                        exchange.send("Power set.");
                    } catch (Exception e) {
                        exchange.send(e.getMessage());
                    }
                })
                .start();
    }
}
