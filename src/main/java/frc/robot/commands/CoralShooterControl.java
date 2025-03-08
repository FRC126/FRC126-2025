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
	boolean centered=false;
	int heldCount=0;

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

		if (Robot.internalData.isAuto() || subsystem.getAutoMove() || Robot.isAutoCommand) {
			// Ignore user controls during Autonomous
			return;
		}
				
    	// Elevator Movement Control
		double y=0;
		if (operatorJoystick.getLeftTrigger() != 0) {
			y=operatorJoystick.getLeftTrigger() *-1;
			heldCount++;
		} else if (operatorJoystick.getRightTrigger() != 0) {
			y=operatorJoystick.getRightTrigger() *.35;
			heldCount++;
		} else {
			heldCount=0;
		}

		y*=.75;

		double yorig=y;

		if (y < 0) {
			// Shooter is running forward
			if (subsystem.getPhotoSensor() && heldCount > 5 && !centered) {
				// if the photo sensor is on and we are not centered yet, 
				// slow the shooter down after 5 iterations
				trigCount++;
				if (trigCount > 5) {
					y=-.20;
				} 
			} else{
				if (trigCount > 5) {
					// Stop the shooter after we have been triggered
					y=0;
					Robot.Leds.setMode(LEDs.LEDModes.Rainbow);
					if (!subsystem.getPhotoSensor()) {
						// Pull the coral back in a little bit
						y=.1;
						centered=false;
					} else {
						// Sensor is back on, so coral is centered.
						centered=true;	
					}
				}	
			}
			Robot.Leds.setMode(LEDs.LEDModes.ShootingCoral);		
		} else if ( y > 0 ) {
			// Shooter is running in reverse
			if (subsystem.getPhotoSensor()) {
				y=0;
				Robot.Leds.setMode(LEDs.LEDModes.Rainbow);
			} else {
				Robot.Leds.setMode(LEDs.LEDModes.ShootingCoral);
			    trigCount=0;
			}	
		} else {
			trigCount=0;
			centered=false;
		}
		
		if (yorig == 0) {
			subsystem.cancel();
		} else {
    	    subsystem.runCoralShooter(y);
		}	
	}
}
