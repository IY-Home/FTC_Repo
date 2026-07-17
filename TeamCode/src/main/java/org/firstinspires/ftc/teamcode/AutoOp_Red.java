package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoOp (Red)", group = "FTC")
public class AutoOp_Red extends AutoOp {

    @Override
    protected void onSequenceStart() {
        totalSteps = 6;
        resetIMU();
        nextStep("Drive backward");
    }

    @Override
    protected void runSequence() {
        switch (stepNumber) {
            case 0:
                hardwareUtils.driveStraight(0.5, -14);
                nextStep("Aligning");
                break;

            case 1:
                hardwareUtils.turnTo(-4);
                hardwareUtils.strafe(0.5, 4);
                nextStep("Shooting");
                break;

            case 2:
                hardwareUtils.shootBalls(3);
                nextStep("Strafing to park");
                break;

            case 3:
                hardwareUtils.strafe(-0.5, 21);
                nextStep("Turning to park");
                break;

            case 4:
                hardwareUtils.turnTo(-42);
                nextStep("Complete");
                break;

            case 5:
                finishSequence();
                break;
        }
    }
}