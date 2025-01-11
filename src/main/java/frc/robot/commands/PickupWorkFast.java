/**********************************
	   _      ___      ____
	 /' \   /'___`\   /'___\
	/\_, \ /\_\ /\ \ /\ \__/
	\/_/\ \\/_/// /__\ \  _``\
	   \ \ \  // /_\ \\ \ \L\ \
	    \ \_\/\______/ \ \____/
		 \/_/\/_____/   \/___/

    Team 126 2024 Code       
	Go get em gaels!

***********************************/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;

public class PickupWorkFast extends Command {
    int iters;
    boolean runThrowerTrigger;
    int seenCount=0;

    /**********************************************************************************
     **********************************************************************************/

    public PickupWorkFast(int itersIn) {
        iters = itersIn;
        seenCount=0;
    }

    /**********************************************************************************
     * Called just before this Command runs the first time
     **********************************************************************************/

    @Override
    public void initialize() {
        seenCount=0;
    }

    /**********************************************************************************
     * Called repeatedly when this Command is scheduled to run
     **********************************************************************************/

    @Override
    public void execute() {
        Robot.pickup.setAutoRunPickup(true);
        Robot.pickup.pickupMotorOn();
        Robot.thrower.setAutoTriggerRun(true);		
        Robot.thrower.throwerTriggerRun(-1);
    }

    /**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
     **********************************************************************************/

    @Override
    public boolean isFinished() {
        iters--;
        boolean pickedUp=false;

        if (Robot.pickup.getPhotoSensor()) {
            seenCount++;
        }
        if (Robot.thrower.getPhotoSensor()) {
             pickedUp=true;
        }

        if (pickedUp || iters == 0 || !Robot.checkAutoCommand()) {
            Robot.pickup.cancel();
            Robot.thrower.setAutoTriggerRun(false);
            Robot.thrower.throwerTriggerOff();
            return true;
        }
        return false;
    }

    /**********************************************************************************
     * Called once after isFinished returns true
     **********************************************************************************/

    @Override
    public void end(boolean isInteruppted) {
        Robot.pickup.cancel();
        Robot.thrower.setAutoTriggerRun(false);
        Robot.thrower.throwerTriggerOff();
    }
}
