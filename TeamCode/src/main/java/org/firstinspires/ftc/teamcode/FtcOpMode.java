package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class FtcOpMode extends LinearOpMode {
    protected HardwareUtils hardwareUtils;
    protected GamepadConfig gamepadConfig;
    protected HashMap<String, Double> speedModes;

    protected boolean useGamepad = true;
    protected boolean showTelemetry = true;

    // Drive state for TeleOp (stored so we can call drive() once per loop)
    protected double driveY = 0;
    protected double driveX = 0;
    protected double driveRx = 0;

    {
        speedModes = new HashMap<>();
        speedModes.put("Precision", 0.15);
        speedModes.put("Slow", 0.35);
        speedModes.put("Normal", 0.6);
        speedModes.put("Fast", 0.8);
        speedModes.put("Turbo", 1.0);
    }

    protected void initHardware() {
        hardwareUtils = new HardwareUtils(hardwareMap, telemetry, this::opModeIsActive);
        gamepadConfig = new GamepadConfig();
    }

    protected void setSpeedMode(String speedMode) {
        Double speed = speedModes.get(speedMode);
        if (speed != null) hardwareUtils.setMoveSpeed(speed);
    }

    protected String getSpeedMode() {
        for (Map.Entry<String, Double> mode : speedModes.entrySet()) {
            if (Objects.equals(hardwareUtils.getMoveSpeed(), mode.getValue())) {
                return mode.getKey();
            }
        }
        return "[" + hardwareUtils.getMoveSpeed() + "]";
    }

    // ==================== DRIVE HELPERS ====================

    /**
     * Set the drive Y (forward/backward) value and update the drivetrain.
     */
    protected void setDriveY(double y) {
        this.driveY = -y;
        updateDrive();
    }

    /**
     * Set the drive X (strafing left/right) value and update the drivetrain.
     */
    protected void setDriveX(double x) {
        this.driveX = x;
        updateDrive();
    }

    /**
     * Set the drive RX (rotation) value and update the drivetrain.
     */
    protected void setDriveRx(double rx) {
        this.driveRx = rx;
        updateDrive();
    }

    /**
     * Call hardwareUtils.drive() with all three stored axis values.
     */
    protected void updateDrive() {
        if (hardwareUtils != null) {
            hardwareUtils.drive(driveY, driveX, driveRx);
        }
    }

    /**
     * Reset all drive values to 0 and stop the drivetrain.
     */
    protected void resetDrive() {
        driveY = 0;
        driveX = 0;
        driveRx = 0;
        updateDrive();
    }

    public void customInit() {};
    public abstract void run();

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Initializing hardware...");
        telemetry.update();
        sleep(50);
        initHardware();
        telemetry.addLine("Performing custom initialization sequence...");
        telemetry.update();
        sleep(50);
        customInit();
        telemetry.addLine("Indicating start...");
        telemetry.update();
        sleep(50);
        signalStart();
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {
            run();
            if (useGamepad) gamepadConfig.executeActions();
            if (showTelemetry) updateTelemetry();
            if (handleStopRequest()) break;
        }

    }

    protected void signalStart() {
        gamepad1.rumble(1, 0, 500);
        gamepad1.setLedColor(1, 255, 1, 1000);

        telemetry.addData("Status", "Ready, press START.");
        telemetry.update();
    }

    protected void updateTelemetry() {
        // --- TELEMETRY FOR DRIVER FEEDBACK ---
        telemetry.addLine("=== DRIVER INFO ===");
        telemetry.addData("Drive Speed", "%.0f%%", hardwareUtils.getMoveSpeed() * 100);
        telemetry.addData("Drive Speed Mode", getSpeedMode());
        telemetry.addData("Shooter Speed", "%.0f%%", hardwareUtils.getShooterSpeed() * 100);
        telemetry.addLine("--- Motor Powers ---");
        telemetry.addData("FL", "%.2f", hardwareUtils.getFrontLeftSpeed());
        telemetry.addData("BL", "%.2f", hardwareUtils.getBackLeftSpeed());
        telemetry.addData("FR", "%.2f", hardwareUtils.getFrontRightSpeed());
        telemetry.addData("BR", "%.2f", hardwareUtils.getBackRightSpeed());
        telemetry.addData("Indexer", "%.2f", hardwareUtils.getIndexerSpeed());
        telemetry.addData("Left Shooter", "%.2f", hardwareUtils.getLeftShooterSpeed());
        telemetry.addData("Right Shooter", "%.2f", hardwareUtils.getRightShooterSpeed());
        telemetry.addData("Intake", "%.2f", hardwareUtils.getIntakeSpeed());
        telemetry.addLine("--- IMU ---");
        telemetry.addData("IMU Heading", hardwareUtils.getHeading());
        telemetry.addData("IMU Pitch", hardwareUtils.getPitch());
        telemetry.addData("IMU Roll", hardwareUtils.getRoll());
        telemetry.update();
    }

    protected void shutdown() {
        if (hardwareUtils != null) {
            hardwareUtils.stopDrivetrain();
            hardwareUtils.stopShooter();
            hardwareUtils.stopIndexer();
        }
        telemetry.addData("Status", "OpMode Complete");
        telemetry.update();
    }

    protected void resetIMU() {
        if (hardwareUtils != null) {
            hardwareUtils.resetIMU();
            telemetry.addData("IMU", "Reset");
            telemetry.update();
        }
    }

    protected boolean handleStopRequest() {
        if (isStopRequested()) {
            shutdown();
            return true;
        }
        return false;
    }
}