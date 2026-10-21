package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.youfuns.webserver.WebServer;

import org.firstinspires.ftc.teamcode.framework.FtcOpMode;
import org.firstinspires.ftc.teamcode.framework.FtcWebServer;

@TeleOp(name="Shooter Test")
public class ShooterTest extends FtcOpMode {
    @Override
    public void customInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        WebServer<?, ?, ?> server = FtcWebServer.initServer(9371, telemetry);
        sleep(1000);
        server.on("/tunePIDF", e -> {
            double p = e.getQueryParameterAsDouble("p", 5);
            double i = e.getQueryParameterAsDouble("i", 0.2);
            double d = e.getQueryParameterAsDouble("d", 0.8);
            double f = e.getQueryParameterAsDouble("f", 30);
            hardwareUtils.leftShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, f));
            hardwareUtils.rightShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, i, d, f));
            e.send("Set!");
        }).on("/test", e -> {
            //hardwareUtils.shootBalls(3);
            e.send("Shoot!");
        }).on("/servo", e -> {
            hardwareUtils.setServoPosition(1);
            sleep(1000);
            hardwareUtils.setServoPosition(0);
            e.send("Tested: " + hardwareUtils.getLeftServoPosition() + " & " + hardwareUtils.getRightServoPosition());
        }).on("/setServo", e -> {
            hardwareUtils.setServoPosition(e.getQueryParameterAsDouble("pos", 0.0));
            e.send("Tested: " + hardwareUtils.getLeftServoPosition() + " & " + hardwareUtils.getRightServoPosition());
        })
        .onException((e, ex) -> {
                e.send(ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }).start();
    }
}
