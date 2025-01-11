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
import edu.wpi.first.wpilibj2.command.Command;

public class PickupControl extends Command {
	JoystickWrapper operatorJoystick;
	Pickup pickup;
	static int keepRunning=0;
	static int pickupSeenCount=0;
	static int triggerSeenCount=0;
	static boolean triggerRunning=false;

	/**********************************************************************************
	 **********************************************************************************/

	public PickupControl(Pickup subsystem) {
		addRequirements(subsystem);
		operatorJoystick = new JoystickWrapper(Robot.oi.operatorController, 0.15);
		this.pickup = subsystem;
	}

	/**********************************************************************************
	 **********************************************************************************/

	@Override
	public void initialize() {
		Robot.stopAutoCommand();
	}

	public void clearTriggerTripped(){
		Robot.pickup.setTriggerTripped(false);
	}

	/**********************************************************************************
	 * Called every tick (20ms)
	 **********************************************************************************/

	static void checkRun() {
		Robot.pickup.setUserRunPickup(true);
		Robot.Leds.setMode(LEDSubsystem.LEDModes.RunPickup);

		if (Robot.pickup.getPhotoSensor()) { pickupSeenCount++; }
		if (Robot.thrower.getPhotoSensor()) { triggerSeenCount++; }
		if (triggerSeenCount >= 1) { 
			keepRunning=0;
			Robot.pickup.cancel();
		    Robot.thrower.setAutoTriggerRun(false);		
			Robot.thrower.throwerTriggerRun(0);
			triggerRunning=false;
			triggerSeenCount=0;
			pickupSeenCount=0;
			Robot.pickup.setTriggerTripped(true);
		} else {
			if (!Robot.pickup.getTriggerTripped()) {
			  Robot.pickup.pickupMotorOn();
		      Robot.thrower.setAutoTriggerRun(true);		
			  Robot.thrower.throwerTriggerRun(-1);
			  triggerRunning=true;
			}  
		}
	} 

	@Override
	public void execute() {

		if (Robot.internalData.isAuto() || Robot.isAutoCommand) {
			// Ignore user controls during Autonomous
			Robot.pickup.setUserRunPickup(false);
			return;
		}		

       	if (Robot.pickup.getPhotoSensor() || Robot.thrower.getPhotoSensor()) {
			Robot.Leds.setMode(LEDSubsystem.LEDModes.RunPickup);
		}

		if (operatorJoystick.leftTriggerPressed() || operatorJoystick.isYButton()) {
			keepRunning=1;
			checkRun();
		} else if ( keepRunning > 0 ) {
			keepRunning--;
			checkRun();
		} else if (operatorJoystick.isLShoulderButton()) {
			Robot.pickup.setUserRunPickup(true);
			this.pickup.pickupMotorReverse();
			pickupSeenCount=0;
			triggerSeenCount=0;
			Robot.pickup.setTriggerTripped(false);
		} else {
			Robot.pickup.setUserRunPickup(false);
			if (!Robot.pickup.getAutoRunPickup()) {
  				this.pickup.cancel();
			}	
			pickupSeenCount=0;
			triggerSeenCount=0;
			if (triggerRunning) {
				Robot.thrower.setAutoTriggerRun(false);		
				Robot.thrower.throwerTriggerRun(0);
				triggerRunning=false;
			}
		}
	}
}
