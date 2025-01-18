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

package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.math.controller.ImplicitModelFollower;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.math.MathUtil;

import com.ctre.phoenix6.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkRelativeEncoder;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

/**********************************************************************************
 **********************************************************************************/

public class Thrower extends SubsystemBase {	
	static double throwerSpeed[] = { 0,0,0 };
	static int targetReached[] = { 0,0,0 } ;
    static int delay;
	static double Pfast = 0.000075;
    static double Pslow = 0.000015;
    static double I = -0.0003;
	boolean throwerDebug=true;
	public static double myRPM=3000;
    static boolean throwTriggered=false;
	static boolean autoTriggerRun=false;
	static boolean autoMoveThrower=false;
	DigitalInput photoSensor = new DigitalInput(4);

	// Thrower Angle Control
	boolean reachedAngleTarget=true;
	int reachedAngleCount=0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Thrower Motors

    TalonFX throwerMotorTalonOne = new TalonFX(RobotMap.throwerTalonMotorOneCanID);
    TalonFX throwerMotorTalonTwo = new TalonFX(RobotMap.throwerTalonMotorTwoCanID);

    SparkMax throwerTriggerMotor = new SparkMax(RobotMap.throwerTriggerMotorCanID, SparkMax.MotorType.kBrushless);
    SparkMax throwerClimberMotorLeft = new SparkMax(RobotMap.throwerClimberMotorLeftCanID, SparkMax.MotorType.kBrushless);
    SparkMax throwerClimberMotorRight = new SparkMax(RobotMap.throwerClimberMotorRightCanID, SparkMax.MotorType.kBrushless);

    RelativeEncoder throwerTriggerMotorRelativeEncoder = throwerTriggerMotor.getEncoder();
    RelativeEncoder throwerClimberMotorLeftRelativeEncoder = throwerClimberMotorLeft.getEncoder();
    RelativeEncoder throwerClimberMotorRightRelativeEncoder = throwerClimberMotorRight.getEncoder();	

    SparkMaxConfig ThrowerConfig = new SparkMaxConfig();

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Thrower Limit Switches

    DigitalInput throwerBottomLimit = new DigitalInput(9);
    DigitalInput throwerTopLimit = new DigitalInput(8);

	/************************************************************************
	 ************************************************************************/

	public Thrower() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new ThrowerControl(this));

		ThrowerConfig.encoder.countsPerRevolution(42);
		throwerTriggerMotor.configure(ThrowerConfig, null, null);
		throwerClimberMotorLeft.configure(ThrowerConfig, null, null);
		throwerClimberMotorRight.configure(ThrowerConfig, null, null);

		ThrowerConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);

	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
     * Run Main Thower Wheels by target RPM
	 ************************************************************************/

    public int throwerRPM(int index, double targetRPM) {
		double ix, error=0.0, rpm;
		StatusSignal<AngularVelocity> RPM;
		double P=Pslow;

		if (index == 1) {
		    RPM = throwerMotorTalonOne.getVelocity();
 			rpm = RPM.getValueAsDouble() * 60;
		} else {
		    RPM = throwerMotorTalonTwo.getVelocity();
			rpm = RPM.getValueAsDouble() * 60;
		}	

        //String bar="Thrower " + index + " RPM Current foo";
		//SmartDashboard.putNumber(bar,rpm);

		/**********************************************************************
		 * PID Loop for controlling motor RPM
		 **********************************************************************/

		if (targetRPM == 0) { /** Spindown **/
			throwerSpeed[index]=0;
		} else { /** Normal operation **/
			error = targetRPM - rpm;
			if (Math.abs(error)>500) {
				P=Pfast;
			} 
			ix = error * 0.02; /** Loop frequency **/
			throwerSpeed[index] += P * error + I * ix;
		}

		if(throwerSpeed[index] < 0) {
			throwerSpeed[index] = 0;
		} else if(throwerSpeed[index] > 1) {
			throwerSpeed[index] = 1;
		}

		if (targetRPM < rpm + 100 && targetRPM > rpm - 100) {
			targetReached[index]++;
		} else {
			targetReached[index]=0;
		}

        // Set the speed on the Thrower Motors
		if (index == 1) {
			throwerMotorTalonOne.set(throwerSpeed[index]);
		} else {
  			throwerMotorTalonTwo.set(throwerSpeed[index]);
		}	

		if (throwerDebug) {
			// Log info to the smart dashboard
			String foo="Thrower " + index + " RPM Current";
			SmartDashboard.putNumber(foo,rpm);
			foo="Thrower " + index + " RPM Target";
			SmartDashboard.putNumber(foo,targetRPM);
			foo="Thrower " + index + " RPM Reached";
			SmartDashboard.putNumber(foo,targetReached[index]);
			foo="Thrower " + index + " speed";
			SmartDashboard.putNumber(foo,throwerSpeed[index]);
		}

        return(targetReached[index]);
    }

	/************************************************************************
     *push note out for amp
	 ************************************************************************/

    public void setThrowerSpeed(double speed) {
		throwerMotorTalonOne.set(speed);
		throwerMotorTalonTwo.set(speed);
	}

    /************************************************************************
	 ************************************************************************/
	public void resetEncoders() {
        resetEncoders(RobotMap.throwerSixtyTicks);
	}	

	public void resetEncoders(double stop) {
		throwerClimberMotorLeftRelativeEncoder.setPosition(stop*-1);
		throwerClimberMotorRightRelativeEncoder.setPosition(stop);
	}

    /************************************************************************
	 ************************************************************************/

	public double getPosition() {
        double position=0;

		double left = throwerClimberMotorLeftRelativeEncoder.getPosition()*-1;
		double right = throwerClimberMotorRightRelativeEncoder.getPosition();

		SmartDashboard.putNumber("left thrower pos", left);
		SmartDashboard.putNumber("right thrower pos", right);

		position=(left+right)/2.0;

		SmartDashboard.putNumber("thrower position calc", position);

		return(position);
	}


    /************************************************************************
	 ************************************************************************/

    public double moveThrower(double speed) {
		double currAngle=getThrowerAngle();
		boolean useLimitSwiches=true;
        
		throwerClimberMotorLeft.configure(ThrowerConfig, null, null);
		throwerClimberMotorRight.configure(ThrowerConfig, null, null);
     	
		if (currAngle > 135 && speed > 0) { speed *= .3; }
		if (currAngle < 30 && speed < 0) { speed *= .3; }
		if (currAngle > 138 && speed > 0) { speed *= .3; }
		if (currAngle < 27 && speed < 0) { speed *= .3; }

		if (((currAngle > 140 && speed > 0) || 
		     (currAngle < 23 && speed < 0)) && 
			 !Robot.overrideEncoders) {
		    throwerClimberMotorLeft.set(0);
		    throwerClimberMotorRight.set(0);
			return(currAngle);
		}

		if (Robot.overrideEncoders) {
			if (speed < 0) { speed = -.2;} else if (speed > 0) { speed=.2; }
		} else {
			if (currAngle > 135 && speed > 0) { speed *= .3; }
			if (currAngle < 30 && speed < 0) { speed *= .3; }
			if (currAngle > 138 && speed > 0) { speed *= .3; }
			if (currAngle < 27 && speed < 0) { speed *= .3; }			
		}

		SmartDashboard.putNumber("thrower speed", speed);

		if ( speed > 0 && throwerTopLimit.get() == true && useLimitSwiches) {
			speed=0;
			resetEncoders(RobotMap.throwerTop);
		}		
		if ( speed < 0 && throwerBottomLimit.get() == true && useLimitSwiches ) {
		    speed=0;
			resetEncoders(RobotMap.throwerBottom);
		}		

		throwerClimberMotorLeft.set(speed*-1);
		throwerClimberMotorRight.set(speed);

        return(currAngle);
	}

    /************************************************************************
	 ************************************************************************/

	 public double getThrowerAngle() {
		double position=getPosition();

		double currAngle = (position / 2.13) + 23;		
		SmartDashboard.putNumber("thrower angle", currAngle);
		SmartDashboard.putNumber("thrower position", position);

		return(currAngle);
	}

    /************************************************************************
	 ************************************************************************/

	public boolean setThrowerPosition(double angle) {

		double currAngle=getThrowerAngle();

		double diff = Math.abs(currAngle-angle);
		double speed = Robot.boundSpeed(diff/12,1,0.06);

		if (diff > 0.15) {
			moveThrower(speed*  ((currAngle > angle + .10) ? -1 : 1));
			reachedAngleCount=0;
		} else {
			reachedAngleCount++;
			moveThrower(0);
		}		

		if (reachedAngleCount>2) {
			reachedAngleTarget=true;
			return true;			
		} else {
			return false;
		}			

	}

    /************************************************************************
	 ************************************************************************/

	public void throwerTriggerRun(double speed) {
		throwerTriggerMotor.set(speed);
	}

	/************************************************************************
	 ************************************************************************/

	 public void throwerTriggerReverse() {
		throwerTriggerRun(1);
		Robot.pickup.pickupMotorReverse();
	}

    /************************************************************************
	 ************************************************************************/

    public void throwerTriggerOn() {
		throwerTriggerRun(-1);
		Robot.pickup.pickupMotorOn();
		throwTriggered=true;
		getPhotoSensor();
	}

    /************************************************************************
	 ************************************************************************/

    public void throwerTriggerOff() {
		throwTriggered=false;
		throwerTriggerRun(0);
		if (!Robot.pickup.getUserRunPickup()) {
  			Robot.pickup.pickupMotorOff();
		}		
	}
	
    /************************************************************************
	 ************************************************************************/

	public void cancel() {
        throwerRPM(1,0); 
        throwerRPM(2,0); 
		throwerTriggerOff();
		moveThrower(0);
		setAutoTriggerRun(false);
		setAutoMoveThrower(false);
	}

    /************************************************************************
	 ************************************************************************/

	public double getRPM() {
		return myRPM;
	}

    /************************************************************************
	 ************************************************************************/

	public void setRPM(double rpmIn) {
		myRPM = rpmIn;
	}

    /************************************************************************
	 ************************************************************************/

	public boolean getThrowTriggered() {
		return throwTriggered;
	}

    /************************************************************************
	 ************************************************************************/

	public void setThrowTriggered(boolean value) {
		throwTriggered = value;
	}

    /************************************************************************
	 ************************************************************************/

	 public void setAutoTriggerRun(boolean value) {
		autoTriggerRun = value;
	}

    /************************************************************************
	 ************************************************************************/

	public boolean getAutoTriggerRun() {
		return autoTriggerRun;
	}

    /************************************************************************
	 ************************************************************************/

	 public void setAutoMoveThrower	(boolean value) {
		autoMoveThrower	 = value;
	}

    /************************************************************************
	 ************************************************************************/

	public boolean getAutoMoveThrower	() {
		return autoMoveThrower	;
	}

    /************************************************************************
	 ************************************************************************/

	 public boolean getPhotoSensor() {
        boolean here=photoSensor.get()?false:true;
		SmartDashboard.putBoolean("photoSensor",here);
        if (here || Robot.pickup.getTriggerTripped()) {
			Robot.Leds.setMode(LEDSubsystem.LEDModes.HaveNoteHigh);
		}
		return(here);
	}		

}


