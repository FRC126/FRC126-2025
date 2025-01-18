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

public class Climber extends SubsystemBase {
	boolean pickupDebug = false;
	double pickupRPM;
	int called = 0;

	static final double extendedPosition=0;
	static final double retractedPosition=600;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Pickup CAN Motor
    SparkMax ClimberMotor = new SparkMax(61, SparkMax.MotorType.kBrushless);
    SparkMax ClimberMotor2 = new SparkMax(62, SparkMax.MotorType.kBrushless);
	SparkMaxConfig ClimberMotorConfig = new SparkMaxConfig();
    RelativeEncoder ClimberMotorEncoder = ClimberMotor.getEncoder();

    DigitalInput climberBottomLimit = new DigitalInput(7);

	/************************************************************************
	 ************************************************************************/

	public Climber() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new ClimberControl(this));
		setPosition(0);

		//ClimberMotorConfig.encoder.countsPerRevolution(42);
		//ClimberMotorConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);
		//ClimberMotor.configure(ClimberMotorConfig, null, null);
	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
	 ************************************************************************/

	public void runMotor(double speed) {

		//ClimberMotor.configure(ClimberMotorConfig, null, null);
		ClimberMotor.set(speed);
		ClimberMotor2.set(speed * -1);
	
	}
		/************************************************************************
	 ************************************************************************/

	private double getPosition() {
		double pos=ClimberMotorEncoder.getPosition();

		SmartDashboard.putNumber("Climber Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		ClimberMotorEncoder.setPosition(value);
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
			if (climberBottomLimit.get() == true && useLimitSwiches) {
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
