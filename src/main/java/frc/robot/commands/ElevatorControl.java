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

public class ElevatorControl extends Command {
	JoystickWrapper operatorJoystick;
	Elevator subsystem;

	/**********************************************************************************
	 **********************************************************************************/

	public ElevatorControl(Elevator subsystemIn) {
		addRequirements(subsystemIn);
		subsystem = subsystemIn;
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

		if (Robot.internalData.isAuto() || subsystem.getAutoMove() || Robot.isAutoCommand) {
			// Ignore user controls during Autonomous
			return;
		}

    	// Elevator Movement Control
		double y = operatorJoystick.getRightStickY() * .7;
		double y2 =operatorJoystick.getLeftStickY() * -.75;

        boolean aButton = operatorJoystick.isAButton();
		boolean bButton = operatorJoystick.isBButton();
		boolean xButton = operatorJoystick.isXButton();
		boolean yButton = operatorJoystick.isYButton();

		if (aButton) {
			subsystem.moveTarget(Robot.heightTargets.LOne);
		} else if (bButton) {
			subsystem.moveTarget(Robot.heightTargets.LTwo);
		} else if (yButton) {
			subsystem.moveTarget(Robot.heightTargets.LThree);
		} else if (xButton) {
			subsystem.moveTarget(Robot.heightTargets.LFour);
		} else {
			if (y != 0) {
				subsystem.moveElevator(y);
			} else {
				subsystem.moveElevator(0);
			}
			SmartDashboard.putNumber("Elevator Movement", y);

			if (y2!=0) {
				subsystem.moveExtension(y2);
			} else {
				subsystem.moveExtension(0);
			}
		}	
	}
}
