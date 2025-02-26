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

	boolean triggered=false;
	int trigCount=0;

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

    	// Elevator Movement Control
		double y=0;
		if (operatorJoystick.getLeftTrigger() != 0) {
			y=operatorJoystick.getLeftTrigger() *-1;
		} else if (operatorJoystick.getRightTrigger() != 0) {
			y=operatorJoystick.getRightTrigger() *.35;
		}

		y*=.65;

		double yorig=y;

		if (y < 0) {
				if (!subsystem.getPhotoSensor()) {
				    trigCount++;
					if (trigCount > 5) {
						y=-.25;
					} else {
						y=-.4;
					}
				} else {
					if (trigCount > 5) {
						y=0;
						Robot.Leds.setMode(LEDs.LEDModes.Rainbow);

					}	
				}
				Robot.Leds.setMode(LEDs.LEDModes.ShootingCoral);
			
		} else if ( y > 0 ) {
			Robot.Leds.setMode(LEDs.LEDModes.ShootingCoral);
			trigCount=0;
		} else {
			trigCount=0;
		}
		
    	subsystem.runCoralShooter(y);
		if (yorig == 0) {
			subsystem.cancel();
		}
	}
}
