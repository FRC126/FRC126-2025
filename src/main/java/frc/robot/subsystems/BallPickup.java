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

import frc.robot.RobotMap;
import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import com.revrobotics.spark.config.SparkBaseConfig;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**********************************************************************************
 **********************************************************************************/

public class BallPickup extends SubsystemBase {
	boolean coralShooterDebug = false;
	int called = 0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Pickup CAN Motor
    SparkMax raiseLowerMotor = new SparkMax(RobotMap.BallPickupRaiseLowerCanID, SparkMax.MotorType.kBrushless);
    SparkMax wheelMotor = new SparkMax(RobotMap.BallPickupWheelCanID, SparkMax.MotorType.kBrushless); 

	RelativeEncoder raiseLowerEncoder = raiseLowerMotor.getEncoder();

	SparkMaxConfig motorConfig = new SparkMaxConfig();

    DigitalInput BottomLimit = new DigitalInput(5);
    DigitalInput TopLimit = new DigitalInput(6);

	boolean useLimitSwiches=false;

	/************************************************************************
	 ************************************************************************/

	public BallPickup() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new BallPickupControl(this));

		motorConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);
		raiseLowerMotor.configure(motorConfig, null, null);
		wheelMotor.configure(motorConfig, null, null);

	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

 	/************************************************************************
	 ************************************************************************/

	 private double getPosition() {
		double pos=raiseLowerEncoder.getPosition();

		SmartDashboard.putNumber("ballPickup Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		// We only need to set Position of encoder on Motor1
		raiseLowerEncoder.setPosition(value);

	}
	
	/************************************************************************
	 ************************************************************************/

	public void raiseLower(double speedin) {
		double speed = speedin;

        if (speed > .1) { speed = .1; }
		if (speed < -.1) { speed = -.1; }

		// TODO Check Encoder

		raiseLowerMotor.set(speed);
	}

	/************************************************************************
	 ************************************************************************/

	 public void runWheel(double speed) {
		wheelMotor.set(speed);
	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		raiseLower(0);
	}
}