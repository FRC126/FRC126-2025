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
//import frc.robot.Robot;
//import frc.robot.RobotMap;
import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.config.SparkBaseConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**********************************************************************************
 **********************************************************************************/

public class TestMotor extends SubsystemBase {
	boolean pickupDebug = false;
	double pickupRPM;
	int called = 0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Test Motors
  	TalonFX testMotor = new TalonFX(40);

	/************************************************************************
	 ************************************************************************/

	public TestMotor() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new TestMotorControl(this));
	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
	 ************************************************************************/

	public void runMotor(double speed) {

		testMotor.set(speed);
	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runMotor(0);
	}
}
