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

public class ClimberControl extends Command {
	JoystickWrapper operatorJoystick;

	/**********************************************************************************
	 **********************************************************************************/

	public ClimberControl(Climber subsystem) {
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

		if (y > 0) {
	        Robot.climber.extendClimber(y);
			Robot.Leds.setMode(LEDSubsystem.LEDModes.Climbing);
			SmartDashboard.putString("Climbing", "Climbing");

		} else if (	y < 0 ) {
	        Robot.climber.retractClimber(y);
			Robot.Leds.setMode(LEDSubsystem.LEDModes.Climbing);
			SmartDashboard.putString("Climbing", "lowering");
		} else {
			Robot.climber.cancel();
			SmartDashboard.putString("Climbing", "no action");
		}
	}
}
