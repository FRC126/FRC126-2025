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
import frc.robot.RobotMap;
import frc.robot.subsystems.*;
import frc.robot.JoystickWrapper;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
//import edu.wpi.first.math.MathUtil;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ThrowerControl extends Command {
	JoystickWrapper operatorJoystick;
	boolean idleThrower=false;
	boolean throwerDebug=true;
	int delay=0;
	final int IDLE_RPM=RobotMap.idleSpeed;
	final int MAX_RPM=4200;
	public static int throwerRun=0;
	boolean resetThrower=false;

	/**********************************************************************************
	 **********************************************************************************/
	
    public ThrowerControl(Thrower subsystem) {
		addRequirements(subsystem);
		operatorJoystick = new JoystickWrapper(Robot.oi.operatorController	, 0.15);
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
		double speed;

		if (Robot.internalData.isAuto() || Robot.isAutoCommand) {
			// Ignore user controls during Autonomous
			return;
		}	
		
		if ( delay > 0) { delay--; }

		// Thrower Angle Control
		double y = operatorJoystick.getLeftStickY();

		if (operatorJoystick.isStartButton()) {
			Robot.thrower.resetEncoders();
		};

  	    if (operatorJoystick.getPovUp()) {
   		    Robot.thrower.setThrowerPosition(115);
		} else if (operatorJoystick.getPovRight()) {
   		    Robot.thrower.setThrowerPosition(60);
		} else if (operatorJoystick.getPovDown()) {
   		    Robot.thrower.setThrowerPosition(38);
		} else if (operatorJoystick.getPovLeft()) {
   		    Robot.thrower.setThrowerPosition(45);
		} else {
			if ( y!=0 ) {
				Robot.thrower.moveThrower(y);
			} else {
				if ( !Robot.thrower.getAutoMoveThrower() && !operatorJoystick.rightTriggerPressed() && !resetThrower) {
				    Robot.thrower.moveThrower(0);
				}
 			}			
		}

		if (operatorJoystick.isBButton()) {
            // Toggle the thrower idle on and off
			if (delay <= 0) {
				if (idleThrower) { idleThrower = false; } else { idleThrower = true; }
				delay=30;
			}
		}	

		if (operatorJoystick.isStartButton()) {
			if (delay <= 0) {
                                /* Robot.thrower.setRPM(Robot.thrower.getRPM()-100);
				 delay=5; */
			}	
		} 	
		
		
		if (Robot.thrower.getRPM() < 0) {
			Robot.thrower.setRPM(0);
		}
		if (Robot.thrower.getRPM() > MAX_RPM) {
			Robot.thrower.setRPM(MAX_RPM);
		}

		if (operatorJoystick.isAButton() || operatorJoystick.rightTriggerPressed() || throwerRun > 0) {
			// double distance=Robot.distance.getDistanceAvg();

			// TODO set the thrower angle and speed based on the distance
			// TODO TODO TODO

    		// Run the motors at specified rpm
			speed=Robot.thrower.getRPM();
			Robot.Leds.setMode(LEDSubsystem.LEDModes.ShootingSpeaker);

			if (operatorJoystick.rightTriggerPressed()) {
				Robot.thrower.setThrowerPosition(60);
				resetThrower=true;
			}
		} else if (idleThrower) {
			// Idle the throwers
			speed=IDLE_RPM; 
			Robot.thrower.setThrowTriggered(false);
			if  (resetThrower) {
				boolean foo=Robot.thrower.setThrowerPosition(30);
				if (foo) { resetThrower=false; }

			}
		} else {
			speed=0;
			Robot.thrower.setThrowTriggered(false);
			if  (resetThrower) {
				boolean foo=Robot.thrower.setThrowerPosition(30);
				if (foo) { resetThrower=false; }

			}
		}
		int reachedOne=0, reachedTwo=0;
		if (!operatorJoystick.isRShoulderButton()){
			reachedOne = Robot.thrower.throwerRPM(1,speed);
			reachedTwo = Robot.thrower.throwerRPM(2,speed);
		}

		if (throwerDebug) {
			SmartDashboard.putNumber("thrower myRPM", Robot.thrower.getRPM());
			SmartDashboard.putBoolean("idleThrower", idleThrower);
			SmartDashboard.putNumber("reachedOne", reachedOne);
    	 	SmartDashboard.putNumber("reachedTwo", reachedTwo);
	     	SmartDashboard.putNumber("speed", speed);
			SmartDashboard.putNumber("thrower tilt input", y);
		}

		if (reachedOne > 2 && reachedTwo > 2 && 
		    (operatorJoystick.isAButton())) {
	     	// If we have reached the target rpm on the thrower, run the trigger and shoot the note
            Robot.thrower.throwerTriggerOn();
			Robot.thrower.setThrowTriggered(true);
			Robot.pickup.setTriggerTripped(false);
		} else if ((operatorJoystick.isAButton()) &&
   	        Robot.thrower.getThrowTriggered()) {
			// If the throw was already triggered, keep running while the button is pressed
            Robot.thrower.throwerTriggerOn();
			Robot.pickup.setTriggerTripped(false);
		} else if (operatorJoystick.isLShoulderButton()) {
			Robot.thrower.throwerTriggerReverse();
			Robot.thrower.setThrowerSpeed(-1.0);
			Robot.thrower.setThrowTriggered(false);
		} else if (!Robot.thrower.getAutoTriggerRun()) {
			Robot.thrower.throwerTriggerOff();
		}
	}
}

