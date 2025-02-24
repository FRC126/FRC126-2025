/**********************************
	   _      ___      ____
	 /' \   /'___`\   /'___\
    /\_, \ /\_\ /\ \ /\ \__/
	\/_/\ \\/_/// /__\ \  _``\
	   \ \ \  // /_\ \\ \ \L\ \
	    \ \_\/\______/ \ \____/
		 \/_/\/_____/   \/___/

    Team 126 2025 Code       
	Go get em gaels!

***********************************/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;

public class BallPickupWork extends Command {

    int iters;
    int target;

    /**********************************************************************************
     **********************************************************************************/

    public BallPickupWork(int target, int iters) {
        this.iters = iters;
        this.target = target;
    }

    /**********************************************************************************
     * Called just before this Command runs the first time
     **********************************************************************************/

    @Override
    public void initialize() {
    }

    /**********************************************************************************
     * Called repeatedly when this Command is scheduled to run
     **********************************************************************************/

    @Override
    public void execute() {
		Robot.ballPickup.moveTarget(target);
    }

    /**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
     **********************************************************************************/

    @Override
    public boolean isFinished() {
        iters--;

        if (iters == 0) {
     		Robot.coralShooter.runCoralShooter(0);
            return true;
        }
        return false;
    }

    /**********************************************************************************
     * Called once after isFinished returns true
     **********************************************************************************/

    @Override
    public void end(boolean isInteruppted) {
        Robot.coralShooter.runCoralShooter(0);
    }
}