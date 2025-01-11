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
			return;
		}	

        boolean noAngle=false;
        boolean noAim=false;

        if (driveJoystick.getPovUp() || driveJoystick.getPovDown()) {
            //Robot.limeLight.setActiveSeek(true);
            if (Robot.targetType == Robot.targetTypes.TargetRed) {
                Robot.Leds.setMode(LEDSubsystem.LEDModes.AimingRed);
            } else if (Robot.targetType == Robot.targetTypes.TargetBlue) {
               Robot.Leds.setMode(LEDSubsystem.LEDModes.AimingBlue);
            }
            if (driveJoystick.getPovDown()) { 
                noAngle=true;
            } else {
                noAim=true;
            }
        } else if (driveJoystick.getPovLeft()) {
            //Robot.targetType = Robot.targetTypes.TargetRed;
        } else if (driveJoystick.getPovRight()) {
            //Robot.targetType = Robot.targetTypes.TargetBlue;
         } else {
            //Robot.limeLight.setActiveSeek(false);
            Robot.thrower.setAutoMoveThrower(false);
        }     
        
        //Robot.limeLight.trackTarget();

        //Robot.limeLight.seekTarget(noAngle, noAim);
    }

	/************************************************************************
     * Make this return true when this Command no longer needs to run execute()
	 ************************************************************************/

    @Override
    public boolean isFinished() {
        return false;
    }
}
