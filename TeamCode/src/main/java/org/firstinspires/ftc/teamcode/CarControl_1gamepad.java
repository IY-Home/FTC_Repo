package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.GamepadConfig.Button;
import org.firstinspires.ftc.teamcode.GamepadConfig.Axis;

@TeleOp(name = "Car Control - 1 Gamepad", group = "FTC_Old")
public class CarControl_1gamepad extends FtcOpMode {

    @Override
    public void run() {
        if (gamepadConfig.isEmpty()) {
            // Driving controls (Gamepad 1)
            gamepadConfig.setAxes(gamepad1)
                    .track(Axis.LEFT_STICK_Y, this::setDriveY)
                    .track(Axis.LEFT_STICK_X, this::setDriveX)
                    .track(Axis.RIGHT_STICK_X, this::setDriveRx);

            // Speed modes & IMU reset
            gamepadConfig.setButtons(gamepad1)
                    .onClick(Button.A, () -> setSpeedMode("Precision"))
                    .onClick(Button.B, () -> setSpeedMode("Slow"))
                    .onClick(Button.X, () -> setSpeedMode("Turbo"))
                    .onClick(Button.Y, () -> setSpeedMode("Normal"))
                    .onClick(Button.OPTIONS, hardwareUtils::resetIMU);

            // Indexer
            gamepadConfig.setButtons(gamepad1)
                    .whenPressed(Button.RIGHT_BUMPER, hardwareUtils::indexForward)
                    .whenReleased(Button.RIGHT_BUMPER, hardwareUtils::stopIndexer)
                    .whenPressed(Button.LEFT_BUMPER, hardwareUtils::indexReverse)
                    .whenReleased(Button.LEFT_BUMPER, hardwareUtils::stopIndexer);

            // Shooter (triggers as buttons)
            gamepadConfig.setAxes(gamepad1)
                    .whenPressed(Axis.LEFT_TRIGGER, hardwareUtils::reverseShooter)
                    .whenReleased(Axis.LEFT_TRIGGER, hardwareUtils::stopShooter)
                    .whenPressed(Axis.RIGHT_TRIGGER, hardwareUtils::startShooter)
                    .whenReleased(Axis.RIGHT_TRIGGER, hardwareUtils::stopShooter);
        }
    }
}