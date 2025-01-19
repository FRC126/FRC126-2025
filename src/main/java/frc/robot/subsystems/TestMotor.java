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

//import frc.robot.Robot;
//import frc.robot.RobotMap;
import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

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
    SparkMax TestMotor = new SparkMax(61, SparkMax.MotorType.kBrushless);
    SparkMax TestMotor2 = new SparkMax(62, SparkMax.MotorType.kBrushless);
	SparkMaxConfig TestMotorConfig = new SparkMaxConfig();
    RelativeEncoder TestMotorEncoder = TestMotor.getEncoder();

	/************************************************************************
	 ************************************************************************/

	public TestMotor() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new TestMotorControl(this));
		setPosition(0);

		TestMotorConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);
		TestMotor.configure(TestMotorConfig, null, null);
	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
	 ************************************************************************/

	public void runMotor(double speed) {

		TestMotor.set(speed);
		TestMotor2.set(speed * -1);

		getPosition();
	}

	/************************************************************************
	 ************************************************************************/

	private double getPosition() {
		double pos=TestMotorEncoder.getPosition();

		SmartDashboard.putNumber("Test Motor Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		TestMotorEncoder.setPosition(value);
	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runMotor(0);
	}
}
