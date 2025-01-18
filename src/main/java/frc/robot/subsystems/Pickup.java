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

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkRelativeEncoder;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**********************************************************************************
 **********************************************************************************/

public class Pickup extends SubsystemBase {
	boolean pickupDebug = false;
	double pickupRPM;
	int called = 0;
	boolean userRunPickup=false;
	boolean autoRunPickup=false;
	boolean triggerTripped=false;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Pickup CAN Motor
    SparkMax PickupMotor = new SparkMax(RobotMap.PickupCanID, SparkMax.MotorType.kBrushless);
	SparkMaxConfig PickupMotorConfig = new SparkMaxConfig();
    RelativeEncoder PickupMotorEncoder = PickupMotor.getEncoder();
	DigitalInput photoSensor = new DigitalInput(6);

	/************************************************************************
	 ************************************************************************/
    public boolean getTriggerTripped() {
		return triggerTripped;
	}

	public void setTriggerTripped(boolean foo) {
		triggerTripped=foo;
	}
	
	public Pickup() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new PickupControl(this));
		PickupMotorConfig.encoder.countsPerRevolution(42);
		PickupMotor.configure(PickupMotorConfig, null, null);
	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
	 ************************************************************************/

	 public boolean getPhotoSensor() {
        boolean here=photoSensor.get();
		SmartDashboard.putBoolean("photoSensor",here);
        if (here) {
			Robot.Leds.setMode(LEDSubsystem.LEDModes.HaveNote);
		}
		return(here);
	}

	/************************************************************************
	 ************************************************************************/
    
	public void pickupMotorOn() {
		PickupMotor.set(-1);
		getPhotoSensor();
	}

	/************************************************************************
	 ************************************************************************/

	 public void pickupMotorReverse() {
		PickupMotor.set(1);
	}

	/************************************************************************
	 ************************************************************************/

	 public void pickupMotorOff() {
		if (!Robot.thrower.getThrowTriggered()) {
			PickupMotor.set(0);
		}	
	}
	
	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		setUserRunPickup(false);  
		setAutoRunPickup(false);  
		pickupMotorOff();
	}

	/************************************************************************
	 ************************************************************************/
 
    public void setUserRunPickup(boolean value) { 
		userRunPickup=value;  
	}
	
	/************************************************************************
	 ************************************************************************/

	public boolean getUserRunPickup() { 
		return(userRunPickup); 
	}

	/************************************************************************
	 ************************************************************************/

    public void setAutoRunPickup(boolean value) { 
		autoRunPickup=value;  
	}
	
	/************************************************************************
	 ************************************************************************/

	public boolean getAutoRunPickup() { 
		return(autoRunPickup); 
	}
	}
