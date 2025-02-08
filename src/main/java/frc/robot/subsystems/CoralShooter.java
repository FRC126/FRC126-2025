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

public class CoralShooter extends SubsystemBase {
	boolean pickupDebug = false;
	double pickupRPM;
	int called = 0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Pickup CAN Motor
    SparkMax CoralShooterMotor1 = new SparkMax(RobotMap.CoralShooterCanID, SparkMax.MotorType.kBrushless);
    SparkMax CoralShooterMotor2 = new SparkMax(RobotMap.CoralShooterCanID2, SparkMax.MotorType.kBrushless);
    
	RelativeEncoder CoralShooterMotor1Encoder = CoralShooterMotor1.getEncoder();
    RelativeEncoder CoralShooterMotor2Encoder = CoralShooterMotor1.getEncoder();

	SparkMaxConfig CoralShooterMotorConfig = new SparkMaxConfig();

    DigitalInput elevatorBottomLimit = new DigitalInput(7);
    DigitalInput elevatorTopLimit = new DigitalInput(8);

	boolean useLimitSwiches=true;

	/************************************************************************
	 ************************************************************************/

	public CoralShooter() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new CoralShooterControl(this));
		setPosition(0);

		CoralShooterMotorConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);
		CoralShooterMotor1.configure(CoralShooterMotorConfig, null, null);
		CoralShooterMotor2.configure(CoralShooterMotorConfig, null, null);
	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
	 ************************************************************************/

	public  void runCoralShooter(double speed) {
		CoralShooterMotor1.set(speed);
		CoralShooterMotor2.set(-1*speed);
	}

 	/************************************************************************
	 ************************************************************************/

	private double getPosition() {
		double pos=CoralShooterMotor1Encoder.getPosition();

		SmartDashboard.putNumber("CoralShooter Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		// We only need to set Position of encoder on Motor1
		CoralShooterMotor1Encoder.setPosition(value);

	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runCoralShooter(0);
	}
}