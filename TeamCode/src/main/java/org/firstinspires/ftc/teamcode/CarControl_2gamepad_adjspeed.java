package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.GamepadConfig.Button;
import org.firstinspires.ftc.teamcode.GamepadConfig.Axis;

@TeleOp(name = "Car Control - 2 Gamepad (Shooter Speed Adjustable)", group = "FTC_Old")
public class CarControl_2gamepad_adjspeed extends FtcOpMode {
    @Override
    public void customInit() {
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
                    .onClick(Button.OPTIONS, hardwareUtils::resetIMU)
                    .whenPressed(Button.RIGHT_BUMPER, hardwareUtils::indexForward)
                    .whenReleased(Button.RIGHT_BUMPER, hardwareUtils::stopIndexer);

            // Gamepad 2: Shooter with speed adjustment
            gamepadConfig.setAxes(gamepad2)
                    .whenPressed(Axis.LEFT_TRIGGER, hardwareUtils::startShooter)
                    .whenReleased(Axis.LEFT_TRIGGER, hardwareUtils::stopShooter)
                    .whenPressed(Axis.RIGHT_TRIGGER, hardwareUtils::reverseShooter)
                    .whenReleased(Axis.RIGHT_TRIGGER, hardwareUtils::stopShooter);

            gamepadConfig.setButtons(gamepad2)
                    .onClick(Button.RIGHT_BUMPER, hardwareUtils::increaseShooterSpeed)
                    .onClick(Button.LEFT_BUMPER, hardwareUtils::decreaseShooterSpeed);
        }
    }
    @Override
    public void run() {
    }
}