package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.GamepadConfig.Button;
import org.firstinspires.ftc.teamcode.GamepadConfig.Axis;

@TeleOp(name = "Car Control - 2 Gamepad", group = "FTC")
public class CarControl_2gamepad extends FtcOpMode {

    @Override
    public void run() {
        if (gamepadConfig.isEmpty()) {
            // Gamepad 1: Driving
            gamepadConfig.setAxes(gamepad1)
                    .track(Axis.LEFT_STICK_Y, this::setDriveY)
                    .track(Axis.LEFT_STICK_X, this::setDriveX)
                    .track(Axis.RIGHT_STICK_X, this::setDriveRx);

            gamepadConfig.setButtons(gamepad1)
                    .onClick(Button.A, () -> setSpeedMode("Precision"))
                    .onClick(Button.B, () -> setSpeedMode("Slow"))
                    .onClick(Button.X, () -> setSpeedMode("Turbo"))
                    .onClick(Button.Y, () -> setSpeedMode("Turbo"))
                    .onClick(Button.OPTIONS, hardwareUtils::resetIMU);

            // Gamepad 2: Shooter & Indexer
            gamepadConfig.setButtons(gamepad2)
                    .whenPressed(Button.RIGHT_BUMPER, hardwareUtils::indexForward)
                    .whenReleased(Button.RIGHT_BUMPER, hardwareUtils::stopIndexer)
                    .whenPressed(Button.LEFT_BUMPER, hardwareUtils::indexReverse)
                    .whenReleased(Button.LEFT_BUMPER, hardwareUtils::stopIndexer);

            gamepadConfig.setAxes(gamepad2)
                    .whenPressed(Axis.LEFT_TRIGGER, hardwareUtils::startShooter)
                    .whenReleased(Axis.LEFT_TRIGGER, hardwareUtils::stopShooter)
                    .whenPressed(Axis.RIGHT_TRIGGER, hardwareUtils::reverseShooter)
                    .whenReleased(Axis.RIGHT_TRIGGER, hardwareUtils::stopShooter);
        }
    }
}