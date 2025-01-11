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

public class ThrowerAngle extends Command {

    private final double angle;
    private boolean reachedAngle = false;
    private int iters;

    /**********************************************************************************
     **********************************************************************************/

    public ThrowerAngle(double angle, int iters) {
        super();
        //addRequirements(Robot.lidar, Robot.thrower);
        addRequirements(Robot.thrower);
        this.angle = angle;
        this.iters = iters;
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
        iters--;
        Robot.thrower.setAutoMoveThrower(true);
        reachedAngle = Robot.thrower.setThrowerPosition(this.angle);
    }

    /**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
     **********************************************************************************/

    @Override
    public boolean isFinished() {
        return reachedAngle || iters==0;
    }

    /**********************************************************************************
     * Called once after isFinished returns true
     **********************************************************************************/

    @Override
    public void end(boolean isInteruppted) {
        Robot.thrower.setAutoMoveThrower(false);
        Robot.thrower.moveThrower(0);
    }
}
