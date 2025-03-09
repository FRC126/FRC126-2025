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
import frc.robot.subsystems.*;
import frc.robot.JoystickWrapper;
import edu.wpi.first.wpilibj2.command.Command;

/**********************************************************************************
 **********************************************************************************/

public class LimeLightControl extends Command {
    public static int iter=0;
    JoystickWrapper driveJoystick;

	/************************************************************************
	 ************************************************************************/

    public LimeLightControl(LimeLight subsystem) {
		addRequirements(subsystem);
        driveJoystick = new JoystickWrapper(Robot.oi.driveController, 0.05);
    }

	/************************************************************************
     * Called just before this Command runs the first time
	 ************************************************************************/

    @Override
    public void initialize() {
    }

	/************************************************************************
     * Called repeatedly when this Command is scheduled to run
	 ************************************************************************/

    @Override
    public void execute() {
		if (Robot.internalData.isAuto() || Robot.isAutoCommand) {
			// Ignore user controls during Autonomous
            Robot.limeLight.trackTarget();
			return;
		}	

        Robot.limeLight.trackTarget();
        if (driveJoystick.isYButton()) {
            Robot.Leds.setMode(LEDs.LEDModes.Aiming);
            Robot.limeLight.seekTarget(Robot.leftRight.Left);
        } else if (driveJoystick.isAButton()) {
            Robot.Leds.setMode(LEDs.LEDModes.Aiming);
            Robot.limeLight.seekTarget(Robot.leftRight.Right);
        } else {
            Robot.swerveDrive.setAutoMove(false);
            Robot.elevator.setAutoMove(false);
            Robot.coralShooter.setAutoMove(false);            
        }   
    }

	/************************************************************************
     * Make this return true when this Command no longer needs to run execute()
	 ************************************************************************/

    @Override
    public boolean isFinished() {
        return false;
    }
}