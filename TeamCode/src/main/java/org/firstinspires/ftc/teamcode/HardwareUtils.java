package org.firstinspires.ftc.teamcode;


import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Map;
import java.util.function.Supplier;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class HardwareUtils {
    private final DcMotor frontLeft, frontRight, backLeft, backRight, indexer, intake;
    private final DcMotorEx leftShooter, rightShooter;
    private final IMU imu;
    private final IMU.Parameters imuParameters;
    private final org.firstinspires.ftc.robotcore.external.Telemetry debug;
    private final Config config;
    private final HashMap<String, String> deviceMap;

    private final Supplier<Boolean> isOpModeActive;

    private final double INITIAL_MOVE_SPEED;
    private final double INITIAL_SHOOTER_SPEED;

    private double MIN_MOVE_SPEED;
    private double MAX_MOVE_SPEED;
    private double MIN_SHOOTER_SPEED;
    private double MAX_SHOOTER_SPEED;
    private double MOVE_ADJUST_AMOUNT;
    private double SHOOTER_ADJUST_AMOUNT;
    private double INITIAL_INTAKE_SPEED;

    private final boolean USE_IMU;

    private final boolean USE_INTAKE;

    {
        USE_IMU = false;
        USE_INTAKE = false;
        deviceMap = new HashMap<>();
        deviceMap.put("frontLeft", "fl");
        deviceMap.put("frontRight", "fr");
        deviceMap.put("backLeft", "bl");
        deviceMap.put("backRight", "br");
        deviceMap.put("indexer", "id");
        deviceMap.put("leftShooter", "ls");
        deviceMap.put("rightShooter", "rs");
        if (USE_INTAKE) deviceMap.put("intake", "it");
        if (USE_IMU) deviceMap.put("imu", "imu");
        imuParameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));
        INITIAL_MOVE_SPEED = 0.6;
        INITIAL_SHOOTER_SPEED = 1200;
        MIN_MOVE_SPEED = 0;
        MAX_MOVE_SPEED = 1;
        MIN_SHOOTER_SPEED = 500;
        MAX_SHOOTER_SPEED = 2100;
        MOVE_ADJUST_AMOUNT = 0.05;
        SHOOTER_ADJUST_AMOUNT = 100;
        INITIAL_INTAKE_SPEED = 0.6;
    }

    private static class Config {
        private Map<String, String> deviceMap;
        private double moveSpeed;
        private double shooterSpeed;
        private double intakeSpeed;
        private com.qualcomm.robotcore.hardware.HardwareMap hardwareMap;
        private org.firstinspires.ftc.robotcore.external.Telemetry debug;



        public Config(com.qualcomm.robotcore.hardware.HardwareMap hardwareMap, org.firstinspires.ftc.robotcore.external.Telemetry debug) {
            this.hardwareMap = hardwareMap;
            this.debug = debug;
            debug("Created HardwareUtils.Config");
        }

        public DcMotor createMotor(String motorName) {
            debug("Creating motor: " + motorName);
            try {
                String motorPath = deviceMap.get(motorName);
                DcMotor motor = hardwareMap.dcMotor.get(motorPath);
                return motor;
            } catch (Exception e) {
                debug("Encountered " + e.getClass().getSimpleName() + ": " + e.getMessage());
                return null;
            }
        }

        public <T extends com.qualcomm.robotcore.hardware.HardwareDevice> T createDevice(Class<T> clazz, String deviceName) {
            debug("Creating device: " + deviceName);
            try {
                 String devicePath = deviceMap.get(deviceName);
                return hardwareMap.get(clazz, devicePath);
            } catch (Exception e) {
                debug("Encountered " + e.getClass().getSimpleName() + ": " + e.getMessage());
                return null;
            }
        }

        public double getMoveSpeed() {
            return moveSpeed;
        }

        public Config setMoveSpeed(double moveSpeed) {
            this.moveSpeed = moveSpeed;
            return this;
        }

        public double getShooterSpeed() {
            return shooterSpeed;
        }

        public Config setShooterSpeed(double shooterSpeed) {
            this.shooterSpeed = shooterSpeed;
            return this;
        }

        public double getIntakeSpeed() {
            return intakeSpeed;
        }

        public Map<String, String> getDeviceMap() {
            return deviceMap;
        }

        public Config setDeviceMap(HashMap<String, String> deviceMap) {
            this.deviceMap = deviceMap;
            return this;
        }

        private void debug(String message) {
            this.debug.addLine(message);
            debug.update();
        }
    }

    public HardwareUtils(com.qualcomm.robotcore.hardware.HardwareMap hardwareMap, org.firstinspires.ftc.robotcore.external.Telemetry debug, Supplier<Boolean> isOpModeActive) {
        this.debug = debug;
        this.isOpModeActive = isOpModeActive;

        this.config = new Config(hardwareMap, debug)
                .setDeviceMap(deviceMap)
                .setMoveSpeed(INITIAL_MOVE_SPEED)
                .setShooterSpeed(INITIAL_SHOOTER_SPEED);

        this.frontLeft = config.createMotor("frontLeft");
        this.frontRight = config.createMotor("frontRight");
        this.backLeft = config.createMotor("backLeft");
        this.backRight = config.createMotor("backRight");
        this.indexer = config.createMotor("indexer");
        this.leftShooter = config.createDevice(DcMotorEx.class, "leftShooter");
        this.rightShooter = config.createDevice( DcMotorEx.class, "rightShooter");
        this.intake = USE_INTAKE ? config.createMotor("intake") : null;
        this.imu = USE_IMU ? config.createDevice(IMU.class, "imu") : null;
        if (anyNull(frontLeft, frontRight, backLeft, backRight, indexer, leftShooter, rightShooter)) {
            debug("One or more motors are null!");
            sleep(2000);
        } else {
            initDevices();
            debug("Created HardwareUtils");
        }
    }

    public HardwareUtils increaseMoveSpeed(double by) {
        config.setMoveSpeed(Math.min(MAX_MOVE_SPEED, config.getMoveSpeed() + by));
        debug("Increasing move speed by " + by + ", new speed " + config.getMoveSpeed());
        return this;
    }

    public HardwareUtils decreaseMoveSpeed(double by) {
        config.setMoveSpeed(Math.max(MIN_MOVE_SPEED, config.getMoveSpeed() - by));
        debug("Decreasing move speed by " + by + ", new speed " + config.getMoveSpeed());
        return this;
    }

    public HardwareUtils increaseMoveSpeed() {
        return this.increaseMoveSpeed(MOVE_ADJUST_AMOUNT);
    }

    public HardwareUtils decreaseMoveSpeed() {
        return this.decreaseMoveSpeed(MOVE_ADJUST_AMOUNT);
    }

    public HardwareUtils setMoveSpeed(double speed) {
        config.setMoveSpeed(Math.min(MAX_MOVE_SPEED, Math.max(MIN_MOVE_SPEED, speed)));
        debug("Setting move speed to " + speed + ", new speed " + config.getMoveSpeed());
        return this;
    }

    public HardwareUtils increaseShooterSpeed(double by) {
        config.setShooterSpeed(Math.min(MAX_SHOOTER_SPEED, config.getShooterSpeed() + by));
        debug("Increasing shooter speed by " + by + ", new speed " + config.getShooterSpeed());
        return this;
    }

    public HardwareUtils decreaseShooterSpeed(double by) {
        config.setShooterSpeed(Math.max(MIN_SHOOTER_SPEED, config.getShooterSpeed() - by));
        debug("Decreasing shooter speed by " + by + ", new speed " + config.getShooterSpeed());
        return this;
    }

    public HardwareUtils increaseShooterSpeed() {
        return this.increaseShooterSpeed(SHOOTER_ADJUST_AMOUNT);
    }

    public HardwareUtils decreaseShooterSpeed() {
        return this.decreaseShooterSpeed(SHOOTER_ADJUST_AMOUNT);
    }

    public HardwareUtils setShooterSpeed(double speed) {
        config.setShooterSpeed(Math.min(MAX_SHOOTER_SPEED, Math.max(MIN_SHOOTER_SPEED, speed)));
        debug("Setting shooter speed to " + speed + ", new speed " + config.getShooterSpeed());
        return this;
    }

    public double getMoveSpeed() {
        return config.getMoveSpeed();
    }

    public double getShooterSpeed() {
        return config.getShooterSpeed();
    }

    public double getFrontLeftSpeed() {
        return frontLeft.getPower();
    }

    public double getFrontRightSpeed() {
        return frontRight.getPower();
    }

    public double getBackLeftSpeed() {
        return backLeft.getPower();
    }

    public double getBackRightSpeed() {
        return backRight.getPower();
    }

    public double getLeftShooterSpeed() {
        return leftShooter.getVelocity();
    }

    public double getRightShooterSpeed() {
        return rightShooter.getVelocity();
    }

    public double getIndexerSpeed() {
        return indexer.getPower();
    }

    public double getIntakeSpeed() {
        return intake != null ? intake.getPower() : 0.0;
    }

    public double getHeading() {
        return imu == null ? 0 : imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public double getHeadingRadians() {
        return imu == null ? 0 : imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public double getPitch() {
        return imu == null ? 0 : imu.getRobotYawPitchRollAngles().getPitch(AngleUnit.DEGREES);
    }

    public double getRoll() {
        return imu == null ? 0 : imu.getRobotYawPitchRollAngles().getRoll(AngleUnit.DEGREES);
    }


    private boolean anyNull(Object... objects) {
        // 1. Safety check for the array itself
        if (objects == null || objects.length == 0) return true;

        // 2. Stream directly from the array and check for any nulls
        return Arrays.stream(objects).anyMatch(Objects::isNull);
    }

    private void initDevices() {
        // Set motor directions
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        // Set brake mode for better control
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Set Intake and Shooter motors
        leftShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftShooter.setDirection(DcMotor.Direction.FORWARD);
        rightShooter.setDirection(DcMotor.Direction.REVERSE);
        indexer.setDirection(DcMotor.Direction.REVERSE);
        indexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        if (imu != null) imu.initialize(imuParameters);

        leftShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(5, 0.2, 0.8, 30));
        rightShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(5, 0.2, 0.8, 30));
    }

    // ==================== DRIVETRAIN CORE FUNCTIONS ====================

    /**
     * Drive the robot using field-centric (robot-relative) controls.
     *
     * @param y Forward/Backward (-1 to 1)
     * @param x Strafing Left/Right (-1 to 1)
     * @param rx Rotation (-1 to 1)
     */
    public void drive(double y, double x, double rx) {
        // Apply deadzone to prevent stick drift
        if (Math.abs(y) < 0.05) y = 0;
        if (Math.abs(x) < 0.05) x = 0;
        if (Math.abs(rx) < 0.05) rx = 0;

        double rotX, rotY;
        if (imu == null) {
            // Robot-centric: no rotation transformation
            rotX = x;
            rotY = y;
        } else {
            // Field-centric: transform based on robot heading
            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
        }

        // Normalize to prevent motor clipping
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator * config.getMoveSpeed();
        double backLeftPower = (rotY - rotX + rx) / denominator * config.getMoveSpeed();
        double frontRightPower = (rotY - rotX - rx) / denominator * config.getMoveSpeed();
        double backRightPower = (rotY + rotX - rx) / denominator * config.getMoveSpeed();

        // Set motor powers
        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);
    }

    /**
     * Stop all drivetrain motors immediately.
     */
    public void stopDrivetrain() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Drive straight forward/backward for a specific distance (in inches).
     * Uses time-based approximation.
     */
    public void driveStraight(double inches, double speed) {
        double timeSeconds = Math.abs(inches / (24.0 * speed));
        drive(speed * Math.signum(inches), 0, 0);
        sleep((long)(timeSeconds * 1000));
        stopDrivetrain();
    }

    /**
     * Strafe left/right for a specific distance (in inches).
     */
    public void strafe(double inches, double speed) {
        double timeSeconds = Math.abs(inches / (24.0 * speed));
        drive(0, speed * Math.signum(inches), 0);
        sleep((long)(timeSeconds * 1000));
        stopDrivetrain();
    }

    /**
     * Turn to a specific absolute angle using the IMU.
     */
    public void turnTo(double targetAngleDegrees) {
        if (imu == null) return;
        double currentAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        double error = targetAngleDegrees - currentAngle;

        while (error > 180) error -= 360;
        while (error < -180) error += 360;

        double turnPower = (error > 0) ? 0.3 : -0.3;

        // Turn in place
        double y = 0, x = 0, rx = turnPower;
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator * 0.5;
        double backLeftPower = (y - x + rx) / denominator * 0.5;
        double frontRightPower = (y - x - rx) / denominator * 0.5;
        double backRightPower = (y + x - rx) / denominator * 0.5;

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);

        ElapsedTime timer = new ElapsedTime();
        while (isOpModeActive.get() && timer.seconds() < 3) {
            currentAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            error = targetAngleDegrees - currentAngle;
            while (error > 180) error -= 360;
            while (error < -180) error += 360;
            if (Math.abs(error) < 2) break;
        }
        stopDrivetrain();
    }

    // ==================== SHOOTER CORE FUNCTIONS ====================

    /**
     * Spin up the shooter at the configured speed.
     */
    public void startShooter() {
        double speed = config.getShooterSpeed();
        leftShooter.setVelocity(speed);
        rightShooter.setVelocity(speed);
    }

    /**
     * Stop the shooter.
     */
    public void stopShooter() {
        leftShooter.setVelocity(0);
        rightShooter.setVelocity(0);
    }

    /**
     * Spin the shooter backward (for unjamming).
     */
    public void reverseShooter() {
        double speed = config.getShooterSpeed();
        leftShooter.setVelocity(-speed);
        rightShooter.setVelocity(-speed);
    }

    /**
     * Run the indexer forward (feed a ball into the shooter).
     */
    public void indexForward() {
        indexer.setPower(-1);
    }

    /**
     * Run the indexer backward (reverse a ball out).
     */
    public void indexReverse() {
        indexer.setPower(1);
    }

    /**
     * Stop the indexer.
     */
    public void stopIndexer() {
        indexer.setPower(0);
    }

    /**
     * Shoot a single ball by spinning the indexer for a fixed time.
     */
    public void shootOneBall() {
        indexForward();
        sleep(500);
        stopIndexer();
        sleep(200);
    }

    /**
     * Shoot multiple balls in sequence.
     */
    public void shootBalls(int count) {
        startShooter();
        sleep(1500); // Wait for shooter to spin up
        for (int i = 0; i < count; i++) {
            shootOneBall();
        }
        stopShooter();
    }

    // ==================== INTAKE CORE FUNCTIONS ====================

    /**
     * Spin up the intake at the configured speed.
     */
    public void startIntake() {
        if (intake == null) return;
        double speed = config.getIntakeSpeed();
        intake.setPower(speed);
    }

    /**
     * Stop the intake.
     */
    public void stopIntake() {
        if (intake == null) return;
        intake.setPower(0);
    }

    /**
     * Spin the intake backward (for unjamming).
     */
    public void reverseIntake() {
        if (intake == null) return;
        double speed = config.getShooterSpeed();
        leftShooter.setPower(-speed);
        rightShooter.setPower(-speed);
    }

    // ==================== UTILITY FUNCTIONS ====================

    /**
     * Reset the IMU yaw to zero.
     */
    public void resetIMU() {
        if (imu != null) imu.resetYaw();
    }


    /**
     * Sleep helper (blocks the thread).
     * USE ONLY IN AUTONOMOUS or background threads!
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void debug(String message) {
        this.debug.addLine(message);
        this.debug.update();
    }
}