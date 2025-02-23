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

package frc.robot.subsystems;

import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.config.SparkBaseConfig;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**********************************************************************************
 **********************************************************************************/

public class Elevator extends SubsystemBase {
	boolean pickupDebug = false;
	double pickupRPM;
	int called = 0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Pickup CAN Motor
    SparkMax leftMotor = new SparkMax(RobotMap.ElevatorLeftCanID, SparkMax.MotorType.kBrushless);
    SparkMax rightMotor = new SparkMax(RobotMap.ElevatorRightCanID, SparkMax.MotorType.kBrushless);
    SparkMax extensionMotor = new SparkMax(RobotMap.ElevatorExtensionID, SparkMax.MotorType.kBrushless);

	RelativeEncoder leftMotorEncoder = leftMotor.getEncoder();
    RelativeEncoder rightMotorEncoder = rightMotor.getEncoder();
    RelativeEncoder extensionMotorEncoder = extensionMotor.getEncoder();

	SparkMaxConfig motorConfig = new SparkMaxConfig();

    DigitalInput bottomLimit = new DigitalInput(7);
    DigitalInput topLimit = new DigitalInput(8);

	boolean useLimitSwiches=true;

	/************************************************************************
	 ************************************************************************/

	public Elevator() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new ElevatorControl(this));
		setPosition(0);

		motorConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);
		leftMotor.configure(motorConfig, null, null);
		rightMotor.configure(motorConfig, null, null);
		extensionMotor.configure(motorConfig, null, null);
	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
	 ************************************************************************/

	private void runMotor(double speed) {
		leftMotor.set(speed*-1);
		rightMotor.set(speed);
	}

    /************************************************************************
	 ************************************************************************/

	 public void moveExtensionTarget(double target) {
		double speed = 0;
		double currentPosition = getExtensionPosition();

		if (target > currentPosition + 3) {
			speed = .5;
			if (target - currentPosition < 10) { speed = .25;}
			if (target - currentPosition < 5) { speed = .1;}
		} else if (target < currentPosition - 3) {
			speed = -.5;
			if (currentPosition - target < 10) { speed = -.25;}
			if (currentPosition - target < 5) { speed = -.1;}
		} else {
			speed = 0;
		}

		moveExtension(speed);
	}

	/************************************************************************
	 ************************************************************************/

	 public void moveExtension(double speedIn) {
        double speed = speedIn;

		if (speed < 0 && getExtensionPosition() < 20) { speed=speed*.5; }
        if (speed < 0 && getExtensionPosition() <= 1) { speed=0; }

		/*
		if (speed > 0 && getExtensionPosition() > 500) { speed=speed*.5; }
        if (speed > 0 && getExtensionPosition() > 550) { speed=0; }
        */
		
		extensionMotor.set(speed);
	}

	/************************************************************************
	 ************************************************************************/

	 private double getExtensionPosition() {
		double pos=extensionMotorEncoder.getPosition() * -1;

		SmartDashboard.putNumber("Extension Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	 public void setExtensionPosition(double value) {
		// We only need to set Position of encoder on Motor1
		extensionMotorEncoder.setPosition(value);
	}

 	/************************************************************************
	 ************************************************************************/

	private double getPosition() {
		double pos=rightMotorEncoder.getPosition();

		SmartDashboard.putNumber("Elevator Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		// We only need to set Position of encoder on Motor1
		rightMotorEncoder.setPosition(value);

	}

    /************************************************************************
	 ************************************************************************/

	public void moveElevatorTarget(double target) {
		double speed = 0;
		double currentPosition = getPosition();

		if (target > currentPosition + 3) {
			speed = .5;
			if (target - currentPosition < 10) { speed = .25;}
			if (target - currentPosition < 5) { speed = .1;}
		} else if (target < currentPosition - 3) {
			speed = -.5;
			if (currentPosition - target < 10) { speed = -.25;}
			if (currentPosition - target < 5) { speed = -.1;}
		} else {
			speed = 0;
		}

		moveElevator(speed);
	}

    /************************************************************************
	 ************************************************************************/	

	public void moveElevator(double speed) {

		if (speed > 1) {
			speed = 1;
		} else if (speed < -1) {
			speed = -1;
		}

		if (Robot.overrideEncoders != true ) {
			if (speed < 0 && getPosition() < 20) { speed=speed*.5; }
			if (speed < 0 && getPosition() <= 1) { speed=0; }
		}	

	    if (bottomLimit.get() == true && speed < 0) {
			speed = 0;
			setPosition(0);
		}

        if (speed > 0) {
			Robot.Leds.setMode(LEDs.LEDModes.ElevatorUp);
		} else if (speed < 0) {	
			Robot.Leds.setMode(LEDs.LEDModes.ElevatorDown);
		}

		runMotor(speed);
	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runMotor(0);
		extensionMotor.set(0);
	}
}