package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoOp (Blue, no shoot)", group = "FTC")
public class AutoOp_Blue_Noshoot extends AutoOp {

    @Override
    protected void onSequenceStart() {
        totalSteps = 5;
        resetIMU();
        nextStep("Drive forward");
    }

    @Override
    protected void runSequence() {
        switch (stepNumber) {
            case 0:
                hardwareUtils.driveStraight(0.7, 5);
                nextStep("Aligning");
                break;

            case 1:
                hardwareUtils.strafe(0.5, 2);
                nextStep("Turning to park");
                break;

            case 2:
                hardwareUtils.turnTo(41);
                nextStep("Strafing to park");
                break;

            case 3:
                hardwareUtils.strafe(1.0, 10);
                nextStep("Complete");
                break;

            case 4:
                finishSequence();
                break;
        }
    }
}