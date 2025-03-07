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

public class LimeLightWork extends Command {

    int iters;
    Robot.heightTargets target;
    boolean shootAfter;
    int runCount;
    boolean reached;

    /**********************************************************************************
     **********************************************************************************/

    public LimeLightWork(Robot.heightTargets target, boolean shootAfter, int iters) {
        this.iters = iters;
        this.target = target;
        this.shootAfter = shootAfter;
        runCount=0;
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
		reached=Robot.elevator.moveTarget(target);

        if ((reached || runCount > 0) && shootAfter) {
            Robot.coralShooter.runCoralShooter(.5);
            runCount++;
        } 
    }


    /**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
     **********************************************************************************/

    @Override
    public boolean isFinished() {
        iters--;
        if (iters == 0 || 
           (reached && !shootAfter) ||
           (shootAfter && runCount > 5)) {
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
        Robot.elevator.cancel();
        
    }
}