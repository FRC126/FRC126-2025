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

import frc.robot.Robot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**********************************************************************************
 **********************************************************************************/

 public class TurnDegreesAbsolute extends Command {
    boolean turnDebug=false;
    double targetDegrees;
    int iters;
    static public double driftAllowance=1;
    int targetReached=0;

	/**********************************************************************************
	 **********************************************************************************/
	
    public TurnDegreesAbsolute(double target, int iters_in ) {
        targetDegrees = target;
        iters = iters_in;
        targetReached=0;
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
        double driveRotate=Robot.swerveDrive.rotateToAbsolute(targetDegrees);

        if (driveRotate==0) {
            targetReached++;
        } else {
            targetReached=0;
        }

        if (turnDebug) {
            SmartDashboard.putNumber("Turn Target Reached",targetReached);
        }
    }

	/**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
	 **********************************************************************************/
	
    @Override
    public boolean isFinished() {
        iters--;

        if (targetReached >= 1 || iters <= 0 || !Robot.checkAutoCommand()) {
            // We have reached our target angle or run out of time to do so.
            Robot.swerveDrive.cancel();
            return true;
        }

        return false;
    }

	/**********************************************************************************
     * Called once after isFinished returns true
	 **********************************************************************************/
	
    @Override
    public void end(boolean isInteruppted) {
        Robot.swerveDrive.cancel();
    }
}