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
import com.revrobotics.spark.SparkRelativeEncoder;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkLowLevel;

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
    SparkMax elevatorMotor1 = new SparkMax(RobotMap.ClimberCanID, SparkMax.MotorType.kBrushless);
    SparkMax elevatorMotor2 = new SparkMax(RobotMap.ClimberCanID, SparkMax.MotorType.kBrushless);
	SparkMaxConfig elevatorMotor1Config = new SparkMaxConfig();
	SparkMaxConfig elevatorMotor2Config = new SparkMaxConfig();
    RelativeEncoder elevatorMotor1Encoder = elevatorMotor1.getEncoder();
    RelativeEncoder elevatorMotor2Encoder = elevatorMotor1.getEncoder();

    DigitalInput elevatorBottomLimit = new DigitalInput(7);
    DigitalInput elevatorTopLimit = new DigitalInput(8);
	boolean useLimitSwiches=true;


	/************************************************************************
	 ************************************************************************/

	public Elevator() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new ElevatorControl(this));
		setPosition(0);

		elevatorMotor1.configure(elevatorMotor1Config, null, null);
		elevatorMotor1Config.idleMode(SparkBaseConfig.IdleMode.kBrake);
		elevatorMotor2.configure(elevatorMotor2Config, null, null);
		elevatorMotor2Config.idleMode(SparkBaseConfig.IdleMode.kBrake);

	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
	 ************************************************************************/

	private void runMotor(double speed) {
		elevatorMotor1.configure(elevatorMotor1Config, null, null);
		elevatorMotor1.set(speed);
		elevatorMotor2.configure(elevatorMotor2Config, null, null);
		elevatorMotor2.set(-1*speed);
	}

 	/************************************************************************
	 ************************************************************************/

	private double getPosition() {
		double pos=elevatorMotor1Encoder.getPosition();

		SmartDashboard.putNumber("Elevator Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		// We only need to set Position of encoder on Motor1
		elevatorMotor1Encoder.setPosition(value);

	}

    /************************************************************************
	 ************************************************************************/

	public void moveElevator(double speed) {
		if (speed > 1) {
			speed = 1;
		} else if (speed < -1) {
			speed = -1;
		}
		if ( elevatorTopLimit.get() == true || elevatorBottomLimit.get() == true ) {
			runMotor(0);
			cancel();
			return;
		}
		if (getPosition() > RobotMap.elevatorExtendedPosition || getPosition() < RobotMap.elevatorRetractedPosition) {
			runMotor(0);
			cancel();
			return;
		}
		// Checking if close to top or bottom
		if ( getPosition() > RobotMap.elevatorExtendedPosition*(1-RobotMap.elevatorBufferPercentage) 
		|| getPosition() < RobotMap.elevatorRetractedPosition*RobotMap.elevatorBufferPercentage) {
			speed*=.5;
		} 
		
		runMotor(speed);
		
	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runMotor(0);
	}
}