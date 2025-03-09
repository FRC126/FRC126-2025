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
	
	public static enum intakeState{None, Run, RunSlow, Backup, Done, Eject};

	boolean triggered=false;
	int trigCount=0;
	boolean centered=false;
	int heldCount=0;
	intakeState State=intakeState.None;	
	int ejectCount=0;

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
		int triggerThreshold=3;

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
		} else if (operatorJoystick.isLShoulderButton()) {
			y = -.5;;
			State=intakeState.Eject;
		} else {
			heldCount=0;
			trigCount=0;
			State=intakeState.None;
		}

		y*=.75;

		double yorig=y;

		if (y < 0) {
			switch (State) {
				case None:
					ejectCount=0;
					State=intakeState.Run;
					/*
					if (subsystem.getPhotoSensor()) {
						State=intakeState.Eject;
						y=-.25;
					} else {	
				 		State=intakeState.Run;
					}	
					*/
					break;
				case Run:	 
 				    if (subsystem.getPhotoSensor()) {
						State=intakeState.RunSlow;
						y=-.25;
					}
					break;	
                case RunSlow:
					if (!subsystem.getPhotoSensor()) {
						State=intakeState.Backup;
						y=0;
					} else {
						y=-.25;
					}
					break;
				case Backup:
					if (subsystem.getPhotoSensor()) {
						State=intakeState.Done;
						y=0;
					} else {
						y=.15;
					}
					break;	
				case Done:
   					Robot.Leds.setMode(LEDs.LEDModes.Rainbow);
					y=0;
					break;	
				case Eject:
				    /*
					ejectCount++;
					y=y*.75;
  				    if (subsystem.getPhotoSensor()) {
					    y=-.25;
						if (ejectCount > 7) {
							State=intakeState.RunSlow;
							ejectCount=0;
						}
					}
					*/
					break;	
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
