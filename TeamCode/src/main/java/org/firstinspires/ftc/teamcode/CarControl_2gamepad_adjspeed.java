package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.framework.FtcOpMode;
import org.firstinspires.ftc.teamcode.framework.GamepadConfig.*;
@TeleOp(name = "Car Control - 2 Gamepad (Shooter Speed Adjustable)", group = "FTC_Old")
public class CarControl_2gamepad_adjspeed extends FtcOpMode {
    @Override
    public void customInit() {
        gamepadConfig.on(gamepad1)
                .track(Axis.LEFT_STICK_Y, this::setDriveY)
                .track(Axis.LEFT_STICK_X, this::setDriveX)
                .track(Axis.RIGHT_STICK_X, this::setDriveRx)
                .onClick(Button.A, () -> setSpeedMode("Precision"))
                .onClick(Button.B, () -> setSpeedMode("Slow"))
                .onClick(Button.X, () -> setSpeedMode("Fast"))
                .onClick(Button.Y, () -> setSpeedMode("Turbo"))
                .onClick(Button.OPTIONS, hardwareUtils::resetIMU)
                .onClick(Button.LEFT_BUMPER, hardwareUtils::unjam)
                .onRelease(Button.LEFT_BUMPER, hardwareUtils::stopShooting)
                .onClick(Button.RIGHT_BUMPER, hardwareUtils::startIndexer)
                .onRelease(Button.RIGHT_BUMPER, hardwareUtils::stopIndexer);

        gamepadConfig.on(gamepad2)
                .onClick(Axis.LEFT_TRIGGER, hardwareUtils::startShooter)
                .onRelease(Axis.LEFT_TRIGGER, hardwareUtils::stopShooter)
                .onClick(Axis.RIGHT_TRIGGER, hardwareUtils::reverseShooter)
                .onRelease(Axis.RIGHT_TRIGGER, hardwareUtils::stopShooter)
                .onClick(Button.RIGHT_BUMPER, hardwareUtils::increaseShooterSpeed)
                .onClick(Button.LEFT_BUMPER, hardwareUtils::decreaseShooterSpeed);
    }
}