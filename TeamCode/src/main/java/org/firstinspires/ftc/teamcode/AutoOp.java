package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class AutoOp extends FtcOpMode {
    protected ElapsedTime stepTimer = new ElapsedTime();
    protected boolean sequenceStarted = false;
    protected boolean sequenceComplete = false;
    protected String currentStep = "INIT";
    protected int stepNumber = 0;

    protected int totalSteps = 1;

    {
        // Autonomous OpModes don't use gamepads by default
        useGamepad = false;
        // Telemetry is useful for debugging autonomous
        showTelemetry = true;
    }

    @Override
    public void customInit() {
        // Start the autonomous sequence
        sequenceStarted = true;
        stepTimer.reset();
        onSequenceStart();
    }

    @Override
    public void run() {
        // Execute the current step
        runSequence();

        // Update telemetry
        if (showTelemetry) {
            updateAutoTelemetry();
        }
    }

    /**
     * Called once when the autonomous sequence starts.
     * Override this to initialize your sequence.
     */
    protected abstract void onSequenceStart();

    /**
     * Called repeatedly in the loop.
     * Override this to implement your step-by-step autonomous sequence.
     */
    protected abstract void runSequence();

    /**
     * Advance to the next step in the autonomous sequence.
     */
    protected void nextStep(String stepName) {
        stepNumber++;
        currentStep = stepName;
        stepTimer.reset();
    }

    /**
     * Mark the sequence as complete.
     */
    protected void finishSequence() {
        sequenceComplete = true;
        telemetry.addData("Status", "Auto Complete!");
        telemetry.update();
    }

    /**
     * Update telemetry with autonomous-specific data.
     */
    protected void updateAutoTelemetry() {
        telemetry.addLine("=== AUTO INFO ===");
        telemetry.addData("Step", stepNumber + "/" + totalSteps + ": " + currentStep);
        telemetry.addData("Step Time", "%.2f seconds", stepTimer.seconds());
        telemetry.addData("Runtime", "%.2f seconds", getRuntime());
        telemetry.addData("Sequence Complete", sequenceComplete);
        telemetry.addLine("--- Hardware ---");
        telemetry.addData("Heading", "%.2f°", hardwareUtils.getHeading());
        telemetry.addData("Move Speed", "%.0f%%", hardwareUtils.getMoveSpeed() * 100);
        telemetry.addData("Shooter Speed", "%.0f%%", hardwareUtils.getShooterSpeed() * 100);
        telemetry.addLine("--- Motor Powers ---");
        telemetry.addData("FL", "%.2f", hardwareUtils.getFrontLeftSpeed());
        telemetry.addData("BL", "%.2f", hardwareUtils.getBackLeftSpeed());
        telemetry.addData("FR", "%.2f", hardwareUtils.getFrontRightSpeed());
        telemetry.addData("BR", "%.2f", hardwareUtils.getBackRightSpeed());
        telemetry.addData("Indexer", "%.2f", hardwareUtils.getIndexerSpeed());
        telemetry.addData("Left Shooter", "%.2f", hardwareUtils.getLeftShooterSpeed());
        telemetry.addData("Right Shooter", "%.2f", hardwareUtils.getRightShooterSpeed());

        telemetry.update();
    }

    /**
     * Sleep helper that keeps the OpMode active.
     */
    protected void sleepWhileActive(long milliseconds) {
        long startTime = System.currentTimeMillis();
        while (opModeIsActive() && (System.currentTimeMillis() - startTime) < milliseconds) {
            // Small sleep to prevent CPU hogging
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            // Keep telemetry updated during sleep
            if (showTelemetry) {
                updateAutoTelemetry();
            }
        }
    }

    /**
     * Get the current runtime of the OpMode.
     */
    @Override
    public double getRuntime() {
        return stepTimer.seconds();
    }
}