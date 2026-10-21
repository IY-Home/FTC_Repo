package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.framework.FtcOpMode;
import org.firstinspires.ftc.teamcode.framework.GamepadConfig.Button;
import org.firstinspires.ftc.teamcode.framework.GamepadConfig.Axis;

@TeleOp(name = "Car Control - 2 Gamepad", group = "FTC")
public class CarControl_2gamepad extends FtcOpMode {

    @Override
    public void customInit() {
        if (gamepadConfig.isEmpty()) {
            // Gamepad 1: Driving
            gamepadConfig.on(gamepad1)
                    .track(Axis.LEFT_STICK_Y, this::setDriveY)
                    .track(Axis.LEFT_STICK_X, this::setDriveX)
                    .track(Axis.RIGHT_STICK_X, this::setDriveRx);

            gamepadConfig.on(gamepad1)
                    .onClick(Button.A, () -> setSpeedMode("Precision"))
                    .onClick(Button.B, () -> setSpeedMode("Slow"))
                    .onClick(Button.X, () -> setSpeedMode("Turbo"))
                    .onClick(Button.Y, () -> setSpeedMode("Turbo"))
                    .onClick(Button.OPTIONS, hardwareUtils::resetIMU);

            // Gamepad 2: Shooter & Indexer
            gamepadConfig.on(gamepad2)
                    .onClick(Button.RIGHT_BUMPER, hardwareUtils::startIndexer)
                    .onRelease(Button.RIGHT_BUMPER, hardwareUtils::stopIndexer)
                    .onClick(Button.LEFT_BUMPER, hardwareUtils::reverseIndexer)
                    .onRelease(Button.LEFT_BUMPER, hardwareUtils::stopIndexer);

            gamepadConfig.on(gamepad2)
                    .onClick(Axis.LEFT_TRIGGER, hardwareUtils::startShooter)
                    .onRelease(Axis.LEFT_TRIGGER, hardwareUtils::stopShooter)
                    .onClick(Axis.RIGHT_TRIGGER, hardwareUtils::reverseShooter)
                    .onRelease(Axis.RIGHT_TRIGGER, hardwareUtils::stopShooter);
        }
    }
}