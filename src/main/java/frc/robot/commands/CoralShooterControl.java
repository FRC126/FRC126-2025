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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
    	// Elevator Movement Control
		double y = operatorJoystick.getLeftStickY() * .1;

		if (y != 0) {
			subsystem.runCoralShooter(y);
		} else {
			subsystem.cancel();
		}
		SmartDashboard.putNumber("Coral Shooter Movement", y);
	}
}
