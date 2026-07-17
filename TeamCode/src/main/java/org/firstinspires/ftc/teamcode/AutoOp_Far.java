package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoOp (Far)", group = "FTC")
public class AutoOp_Far extends AutoOp {

    @Override
    protected void onSequenceStart() {
        totalSteps = 3;
        resetIMU();
        nextStep("Wait");
    }

    @Override
    protected void runSequence() {
        switch (stepNumber) {
            case 0:
                sleepWhileActive(20000);
                nextStep("Drive forward");
                break;

            case 1:
                hardwareUtils.driveStraight(0.5, 6);
                nextStep("Complete");
                break;

            case 2:
                finishSequence();
                break;
        }
    }
}