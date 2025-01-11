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

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;

public class ThrowerWork extends Command {

    int iters, speed;
    int throwingIters=0;
    int reachedOne=0, reachedTwo=0;

    /**********************************************************************************
     **********************************************************************************/

    public ThrowerWork(int speed, int iters) {
        //addRequirements(Robot.lidar);
        this.iters = iters;
        this.speed = speed;
        throwingIters=0;
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
		reachedOne = Robot.thrower.throwerRPM(0,speed);
        reachedTwo = Robot.thrower.throwerRPM(1,speed);

        if (throwingIters > 0) {
            Robot.thrower.setAutoTriggerRun(true);
            Robot.thrower.throwerTriggerOn();
            throwingIters--;
        } else {
            SmartDashboard.putNumber("reachedOne", reachedOne);
            SmartDashboard.putNumber("reachedTwo", reachedTwo);

            // If we have reached the target rpm on the thrower, run the trigger and shoot the note
            if (reachedOne > 2 && reachedTwo > 2) {
                Robot.thrower.throwerTriggerOn();
                Robot.thrower.setAutoTriggerRun(true);
                Robot.thrower.setThrowTriggered(true);
                throwingIters=150;
            } else {
                Robot.thrower.throwerTriggerOff();
                Robot.thrower.setAutoTriggerRun(false);
                Robot.thrower.setThrowTriggered(false);
            }
        }    
    }

    /**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
     **********************************************************************************/

    @Override
    public boolean isFinished() {
        iters--;

        if ((iters == 0 && throwingIters == 0) || throwingIters == 1 || !Robot.checkAutoCommand()) {
            //Robot.thrower.cancel();
            Robot.thrower.throwerTriggerOff();

            Robot.thrower.setThrowTriggered(false);
            Robot.thrower.setAutoTriggerRun(false);
            return true;
        }
        return false;
    }

    /**********************************************************************************
     * Called once after isFinished returns true
     **********************************************************************************/

    @Override
    public void end(boolean isInteruppted) {
        //Robot.thrower.cancel();
        Robot.thrower.throwerTriggerOff();
        Robot.thrower.setThrowTriggered(false);
        Robot.thrower.setAutoTriggerRun(false);
    }
}
