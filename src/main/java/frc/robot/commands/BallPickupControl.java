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

public class BallPickupControl extends Command {
	JoystickWrapper operatorJoystick;
	BallPickup subsystem;

	/**********************************************************************************
	 **********************************************************************************/

	public BallPickupControl(BallPickup subsystemin) {
		addRequirements(subsystemin);
		subsystem=subsystemin;
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
    	// Ball Pickup Movement Control

        double y=0;

		if (operatorJoystick.isLShoulderButton()) {
			subsystem.moveTarget(0);
			subsystem.runWheel(0);
		} else if (operatorJoystick.isRShoulderButton()) {
			subsystem.moveTarget(1);
			subsystem.runWheel(.25);
			Robot.Leds.setMode(LEDs.LEDModes.BallPickup);
		} else {
			if (operatorJoystick.getPovUp()) { y=-.20; }
			if (operatorJoystick.getPovDown()) { y=.2; }

			if (y != 0) {
				subsystem.raiseLower(y);
			} else {
				subsystem.raiseLower(0);
			}

			if (operatorJoystick.getPovLeft()) {
				subsystem.runWheel(.25);
				Robot.Leds.setMode(LEDs.LEDModes.BallPickup);
			} else if (operatorJoystick.getPovRight()) {
				subsystem.runWheel(-.25);
				Robot.Leds.setMode(LEDs.LEDModes.BallPickup);
			} else {
				subsystem.runWheel(0);
			}

   		    SmartDashboard.putNumber("Ball Pickup Movement", y);
		}	
	}
}
