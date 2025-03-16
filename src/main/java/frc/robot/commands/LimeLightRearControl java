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

public class LimeLightRearControl extends Command {
    public static int iter=0;
    JoystickWrapper driveJoystick;

	/************************************************************************
	 ************************************************************************/

    public LimeLightRearControl(LimeLight subsystem) {
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
            Robot.limeLightRear.trackTarget();
			return;
		}	

        Robot.limeLightRear.trackTarget();

        if (driveJoystick.getPovDown()) {
            Robot.limeLightRear.setActive(true);
            Robot.Leds.setMode(LEDs.LEDModes.Aiming);
            Robot.limeLightRear.seekTargetNoDistance();
        } else {    
            if (!Robot.limeLight.getActive()) {
                Robot.swerveDrive.setAutoMove(false);
            }    
            Robot.limeLightRear.setActive(false);
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