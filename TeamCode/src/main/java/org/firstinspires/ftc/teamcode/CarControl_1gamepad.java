package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.framework.FtcOpMode;
import org.firstinspires.ftc.teamcode.framework.GamepadConfig.Button;
import org.firstinspires.ftc.teamcode.framework.GamepadConfig.Axis;

@TeleOp(name = "Car Control - 1 Gamepad", group = "FTC_Old")
public class CarControl_1gamepad extends FtcOpMode {

    @Override
    public void customInit() {
        if (gamepadConfig.isEmpty()) {
            // Driving controls (Gamepad 1)
            gamepadConfig.on(gamepad1)
                    .track(Axis.LEFT_STICK_Y, this::setDriveY)
                    .track(Axis.LEFT_STICK_X, this::setDriveX)
                    .track(Axis.RIGHT_STICK_X, this::setDriveRx);

            // Speed modes & IMU reset
            gamepadConfig.on(gamepad1)
                    .onClick(Button.A, () -> setSpeedMode("Precision"))
                    .onClick(Button.B, () -> setSpeedMode("Slow"))
                    .onClick(Button.X, () -> setSpeedMode("Fast"))
                    .onClick(Button.Y, () -> setSpeedMode("Turbo"))
                    .onClick(Button.OPTIONS, hardwareUtils::resetIMU);

            // Indexer
            gamepadConfig.on(gamepad1)
                    .whenPressed(Button.RIGHT_BUMPER, hardwareUtils::startIndexer)
                    .whenReleased(Button.RIGHT_BUMPER, hardwareUtils::stopIndexer)
                    .whenPressed(Button.LEFT_BUMPER, hardwareUtils::reverseIndexer)
                    .whenReleased(Button.LEFT_BUMPER, hardwareUtils::stopIndexer);

            // Shooter (triggers as buttons)
            gamepadConfig.on(gamepad1)
                    .whenPressed(Axis.LEFT_TRIGGER, hardwareUtils::reverseShooter)
                    .whenReleased(Axis.LEFT_TRIGGER, hardwareUtils::stopShooter)
                    .whenPressed(Axis.RIGHT_TRIGGER, hardwareUtils::startShooter)
                    .whenReleased(Axis.RIGHT_TRIGGER, hardwareUtils::stopShooter);
        }
    }
}