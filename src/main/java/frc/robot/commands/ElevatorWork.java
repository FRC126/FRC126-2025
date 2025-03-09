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
import frc.robot.Robot.shootAction;

public class ElevatorWork extends Command {

    int iters;
    Robot.heightTargets target;
    shootAction shootAfter;
    int runCount;
    boolean reached;

    /**********************************************************************************
     **********************************************************************************/

    public ElevatorWork(Robot.heightTargets target, shootAction shootAfter, int iters) {
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

        if ((reached || runCount > 0) && shootAfter == Robot.shootAction.Shoot) {
            Robot.coralShooter.setAutoMove(true);
            Robot.coralShooter.runCoralShooter(.25);
            runCount++;
        } else {
            runCount=0;
        }
    }


    /**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
     **********************************************************************************/

    @Override
    public boolean isFinished() {
        iters--;
        if (iters == 0 || 
           (reached && shootAfter == Robot.shootAction.NoShoot) ||
           (shootAfter == Robot.shootAction.Shoot && runCount > 5)) {
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
        Robot.coralShooter.setAutoMove(true);      
        Robot.elevator.cancel();
        runCount=0;
    }
}