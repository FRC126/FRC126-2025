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
import frc.robot.RobotMap;
import frc.robot.subsystems.*;
import frc.robot.JoystickWrapper;
import edu.wpi.first.wpilibj2.command.Command;

public class CoralShooterControl extends Command {
	JoystickWrapper operatorJoystick;
	CoralShooter subsystem;
	
	/**********************************************************************************
	 **********************************************************************************/

	public CoralShooterControl(CoralShooter subsystemIn) {
		addRequirements(subsystemIn);
		subsystem = subsystemIn;
		operatorJoystick = new JoystickWrapper(Robot.oi.operatorController, RobotMap.joystickDrift);
	}

	/**********************************************************************************
	 **********************************************************************************/

	@Override
	public void initialize() {
		Robot.stopAutoCommand();
	}

	/**********************************************************************************
	 * Called every tick (20ms)
	 **********************************************************************************/

	@Override
	public void execute() {
		boolean doEject=false;

		if (Robot.internalData.isAuto() || subsystem.getAutoMove() || Robot.isAutoCommand) {
			// Ignore user controls during Autonomous
			return;
		}
				
    	// Elevator Movement Control
		double y=0;
		if (operatorJoystick.getLeftTrigger() != 0) {
			y=operatorJoystick.getLeftTrigger() * -.75;
		} else if (operatorJoystick.getRightTrigger() != 0) {
			y=operatorJoystick.getRightTrigger() * .25;
		} else if (operatorJoystick.isLShoulderButton()) {
			doEject=true;
		} else {
			subsystem.resetState();
		}

		if (y < 0 || doEject) {
			y = subsystem.doIntake(y,doEject);
		} else if ( y > 0 ) {
			// Shooter is running in reverse
			if (subsystem.getPhotoSensor()) {
				y = 0;
				Robot.Leds.setMode(LEDs.LEDModes.Rainbow);
			} else {
				Robot.Leds.setMode(LEDs.LEDModes.ShootingCoral);
			}	
		}
		
  	    subsystem.runCoralShooter(y);
	}
}
