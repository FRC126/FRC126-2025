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

	public void moveExtension(double speedIn) {
        double speed = speedIn;





		extensionMotor.set(speed);
	}

 	/************************************************************************
	 ************************************************************************/

	private double getPosition() {
		double pos=leftMotorEncoder.getPosition();

		SmartDashboard.putNumber("Elevator Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		// We only need to set Position of encoder on Motor1
		leftMotorEncoder.setPosition(value);

	}

    /************************************************************************
	 ************************************************************************/

	public void moveElevator(double speed) {
		if (speed > 1) {
			speed = 1;
		} else if (speed < -1) {
			speed = -1;
		}
/*
		if ( topLimit.get() == true || bottomLimit.get() == true ) {
			// TODO Reset encoder value based on which limit it hit to correct
			// for any encoder drift during the match
			cancel();
			return;
		}

		if ( getPosition() > RobotMap.elevatorExtendedPosition ||
		    getPosition() < RobotMap.elevatorRetractedPosition ) {
			cancel();
			return;
		}

		// Checking if close to top or bottom
		if ( getPosition() > RobotMap.elevatorExtendedPosition * ( 1 - RobotMap.elevatorBufferPercentage ) ||
		     getPosition() < RobotMap.elevatorRetractedPosition * RobotMap.elevatorBufferPercentage ) {
			speed *= .5;
		} 
*/		
		runMotor(speed);
	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runMotor(0);
		extensionMotor.set(0);
	}
}