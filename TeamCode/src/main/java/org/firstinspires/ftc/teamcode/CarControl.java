package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.framework.FtcOpMode;
import org.firstinspires.ftc.teamcode.framework.GamepadConfig.*;

@TeleOp(name="Car Control", group="Current")
public class CarControl extends FtcOpMode {
    // --- Shooter servo state (spec: D-pad Up/Down = servo direction) ---
    private static final double SERVO_STEP = 0.05;
    private static final double SERVO_MIN  = 0.0;
    private static final double SERVO_MAX  = 1.0;
    private double shooterServoPosition = 0.5; // start centered

    @Override
    public void customInit() {
        if (!gamepadConfig.isEmpty()) return;

        // ================= GAMEPAD 1 — DRIVER =================
        gamepadConfig.on(gamepad1)
                // 4. Joysticks — mecanum drive (movement + rotation)
                .track(Axis.LEFT_STICK_Y,  this::setDriveY)
                .track(Axis.LEFT_STICK_X,  this::setDriveX)
                .track(Axis.RIGHT_STICK_X, this::setDriveRx)

                // 8. Action buttons — drive speed modes (reuse existing modes)
                .onClick(Button.A, () -> setSpeedMode("Precision")) // 25%  -> closest existing (0.15)
                .onClick(Button.X, () -> setSpeedMode("Slow"))      // 50%  -> closest existing (0.35)
                .onClick(Button.B, () -> setSpeedMode("Fast"))      // 75%  -> closest existing (0.8)
                .onClick(Button.Y, () -> setSpeedMode("Turbo"))     // 100% -> existing (1.0)

                // 9. START — IMU reset (spec: use START or OPTIONS, just one)
                .onClick(Button.START, hardwareUtils::resetIMU)

                // ===== Section B fixes wired to the gamepad =====

                // 1. L1 — unjam: intake + indexer + shooter reversed
                .whenPressed(Button.LEFT_BUMPER,  hardwareUtils::unjam)
                .whenReleased(Button.LEFT_BUMPER, hardwareUtils::stopShooting)

                // 1. L2 — intake on (held)
                .whenPressed(Axis.LEFT_TRIGGER,  hardwareUtils::startIntake)
                .whenReleased(Axis.LEFT_TRIGGER, hardwareUtils::stopIntake)

                // 2. R1 — indexer on (held)
                .whenPressed(Button.RIGHT_BUMPER,  hardwareUtils::startIndexer)
                .whenReleased(Button.RIGHT_BUMPER, hardwareUtils::stopIndexer)

                // 2. R2 — shooter on (held)
                .whenPressed(Axis.RIGHT_TRIGGER,  hardwareUtils::startShooter)
                .whenReleased(Axis.RIGHT_TRIGGER, hardwareUtils::stopShooter)

                // 3. D-pad L/R — shooter speed decrease / increase
                .onClick(Button.DPAD_LEFT,  hardwareUtils::decreaseShooterSpeed)
                .onClick(Button.DPAD_RIGHT, hardwareUtils::increaseShooterSpeed)

                // 3. D-pad U/D — shooter servo direction
                .onClick(Button.DPAD_UP,   this::servoUp)
                .onClick(Button.DPAD_DOWN, this::servoDown);
    }

    private void servoUp() {
        shooterServoPosition = clamp(shooterServoPosition + SERVO_STEP);
        hardwareUtils.setServoPosition(shooterServoPosition);
    }

    private void servoDown() {
        shooterServoPosition = clamp(shooterServoPosition - SERVO_STEP);
        hardwareUtils.setServoPosition(shooterServoPosition);
    }

    private double clamp(double v) {
        return Math.max(SERVO_MIN, Math.min(SERVO_MAX, v));
    }

}
