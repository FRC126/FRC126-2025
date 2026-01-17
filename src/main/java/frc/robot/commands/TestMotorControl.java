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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class TestMotorControl extends Command {
	JoystickWrapper operatorJoystick;

	/**********************************************************************************
	 **********************************************************************************/

	public TestMotorControl(TestMotor subsystem) {
		addRequirements(subsystem);
		operatorJoystick = new JoystickWrapper(Robot.oi.operatorController, 0.15);

		// Add a slider to the SmartDashboard for motor speed
		SmartDashboard.putNumber("Motor Speed Slider", 0.0); // Default value is 0.0
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
		// Read the slider value from the SmartDashboard
		double sliderSpeed = SmartDashboard.getNumber("Motor Speed Slider", 0.0);

    	// Climber Movement Control
		double y = operatorJoystick.getRightStickY();
		double speed=0;

		if (y > 0) {
			speed = (y - .15) * 1.15;
			SmartDashboard.putString("Test Motor", "Forward");
		} else if (	y < 0 ) {
			speed = (y + .15) * 1.15;
			SmartDashboard.putString("Test Motor", "Backward");
		} else {
			SmartDashboard.putString("Test Motor", "Stop");
			}

	
	 	SmartDashboard.putNumber("Test Motor Speed", speed);
        Robot.testMotor.runMotor(speed)	;
	}
}
