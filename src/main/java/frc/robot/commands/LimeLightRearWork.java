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

public class LimeLightRearWork extends Command {

    int iters;
    Robot.heightTargets target;
    boolean reached;
    Robot.leftRight direction;

    /**********************************************************************************
     **********************************************************************************/

    public LimeLightRearWork(int iters) {
        this.iters = iters;
        reached=false;
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
		reached=Robot.limeLightRear.seekTargetNoDistance();
    }

    /**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
     **********************************************************************************/

    @Override
    public boolean isFinished() {
        iters--;
        if (iters == 0 || reached) {
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