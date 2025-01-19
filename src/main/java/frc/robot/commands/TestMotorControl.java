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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class TestMotorControl extends Command {
	JoystickWrapper operatorJoystick;

	/**********************************************************************************
	 **********************************************************************************/

	public TestMotorControl(TestMotor subsystem) {
		addRequirements(subsystem);
		operatorJoystick = new JoystickWrapper(Robot.oi.operatorController, 0.15);
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
    	// Climber Movement Control
		double y = operatorJoystick.getRightStickY();

			SmartDashboard.putNumber("Climb Speed", y);

		if (y > 0) {
			Robot.climber.runMotor((y - .15) * 1.15);
			SmartDashboard.putString("Climbing", "Climbing");

		} else if (	y < 0 ) {
			Robot.climber.runMotor((y + .15) * 1.15);
			SmartDashboard.putString("Climbing", "lowering");
		} else {
			Robot.climber.runMotor(0);
			SmartDashboard.putString("Climbing", "no action");
		}
	}
}
