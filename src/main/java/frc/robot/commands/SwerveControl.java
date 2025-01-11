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

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.JoystickWrapper;
import frc.robot.Robot;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.subsystems.LEDSubsystem;

public class SwerveControl extends Command {
	JoystickWrapper driveJoystick;
	public static boolean driveStraight = false;
	public static double straightDegrees = 0;
	int delay=0;

	/**********************************************************************************
	 **********************************************************************************/

	public SwerveControl(SwerveDrive subsystem) {
		addRequirements(subsystem);
		driveJoystick = new JoystickWrapper(Robot.oi.driveController, 0.10);
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
		if (--delay < 0) { delay=0; }

		// X buttom aborts any running auto commands
		if (driveJoystick.isXButton()) {
			Robot.stopAutoCommand();
		}

		if (Robot.internalData.isAuto() || Robot.swerveDrive.getAutoMove() || Robot.isAutoCommand) {
			// Ignore user controls during Autonomous
    		driveStraight = false;
			return;
		}

		// Get the driver inputs from the driver xbox controller
		double forwardBack = driveJoystick.getLeftStickY();
		if (forwardBack > 0) {
			forwardBack = (forwardBack - .1) * 1.111;
		} else if (forwardBack < 0) {
			forwardBack = (forwardBack + .1) * 1.111;
		}
		double leftRight = driveJoystick.getLeftStickX();
		if (leftRight > 0) {
			leftRight = (leftRight - .1) * 1.111;
		} else if (leftRight < 0) {
			leftRight = (leftRight + .1) * 1.111;
		}

		double rotate = driveJoystick.getRightStickX();
		if (rotate > 0) {
			rotate = (rotate - .1) * 1.111;
		} else if (rotate < 0) {
			rotate = (rotate + .1) * 1.111;
		}

		if (forwardBack == 0 && leftRight == 0 && rotate == 0) {
	        Robot.Leds.setMode(LEDSubsystem.LEDModes.GaelForce);
		} else {	
	        Robot.Leds.setMode(LEDSubsystem.LEDModes.DriveMode);
		}

		// left Trigger enables slow mode
		if (driveJoystick.getLeftTrigger() > .25) {
			Robot.swerveDrive.driveSlow(true);
		    Robot.Leds.setMode(LEDSubsystem.LEDModes.SlowMode);
		} else {
			Robot.swerveDrive.driveSlow(false);
		}

		// Apply motor braking when the right trigger is pressed
		if (driveJoystick.getRightTrigger() > .25) {
			Robot.swerveDrive.brakesOn();
			Robot.Leds.setMode(LEDSubsystem.LEDModes.BrakeMode);
		} else {
			Robot.swerveDrive.brakesOff();
		}			

		// reset the gyro to zero fix any drift
		if (driveJoystick.isBButton()) {
			Robot.swerveDrive.resetYaw();
			straightDegrees = Robot.swerveDrive.getYaw();      
		}

    	if (driveJoystick.isStartButton()) {
			if (delay == 0) {
			    Robot.swerveDrive.toggleFullSpeed();
				delay=100;
			}	
		}	

		if (rotate == 0 && (forwardBack != 0 || leftRight != 0)) {
			// if no rotate input specified, we are going to drive straight
			if (driveStraight != true) {
				// If driveStraight isn't set, save the current angle
				straightDegrees = Robot.swerveDrive.getYaw();      
				driveStraight = true;
			}	
		} else if (rotate != 0) {
				// clear drive straight since we are rotating
				driveStraight = false;
		}

		SmartDashboard.putBoolean("driveStraight", driveStraight);
		SmartDashboard.putNumber("straightDegrees", straightDegrees);

		Robot.swerveDrive.Drive(forwardBack, leftRight, rotate, driveStraight, straightDegrees);
	}
}
