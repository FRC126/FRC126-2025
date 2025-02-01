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

	static final double extendedPosition=0;
	static final double retractedPosition=600;

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

	/************************************************************************
	 ************************************************************************/

	public Elevator() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new ElevatorControl(this));
		setPosition(0);

		//ClimberMotorConfig.encoder.countsPerRevolution(42);
		elevatorMotor1.configure(elevatorMotor1Config, null, null);
		elevatorMotor1Config.idleMode(SparkBaseConfig.IdleMode.kBrake);
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
	}

 	/************************************************************************
	 ************************************************************************/

	private double getPosition() {
		double pos=elevatorMotor1Encoder.getPosition();

		SmartDashboard.putNumber("Climber Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		elevatorMotor1Encoder.setPosition(value);
	}

	/************************************************************************
	 ************************************************************************/

	public boolean extendClimber(double speed) {
		if (speed < 0) { 
			return(true);
		}

		// Check Thrower Position before moving Climber
		if (getPosition() > extendedPosition || Robot.overrideEncoders ) {
			if ( getPosition() > 350 ) { speed *= .5; }
    		runMotor(speed*-1);
			return(false);
		} else {
			cancel();
			return(true);
		}	
	}

    /************************************************************************
	 ************************************************************************/

	public boolean retractClimber(double speed) {
		boolean useLimitSwiches=true;

		if (speed > 0) { 
			return(true);
		}

		// Check Thrower Position before moving Climber
		if (getPosition() < retractedPosition || Robot.overrideEncoders ) {
			if (elevatorBottomLimit.get() == true && useLimitSwiches) {
       		    cancel();
				return(true);
			} else {	
     		    runMotor(speed*-1);
				return(false);
			}	
		} else {
     		cancel();
			return(true);
		}	
	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runMotor(0);
	}
}