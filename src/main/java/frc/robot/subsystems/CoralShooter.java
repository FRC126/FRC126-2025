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

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import com.revrobotics.spark.config.SparkBaseConfig;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**********************************************************************************
 **********************************************************************************/

public class CoralShooter extends SubsystemBase {
	boolean coralShooterDebug = false;
	int called = 0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Pickup CAN Motor
    SparkMax CoralShooterMotor1 = new SparkMax(RobotMap.CoralShooterCanID, SparkMax.MotorType.kBrushless);
    SparkMax CoralShooterMotor2 = new SparkMax(RobotMap.CoralShooterCanID2, SparkMax.MotorType.kBrushless); 
	SparkMaxConfig CoralShooterMotorConfig = new SparkMaxConfig();

	// Photo sensor to stop the shooter once it has hold of the coral
	DigitalInput photoSensor = new DigitalInput(4);

	/************************************************************************
	 ************************************************************************/

	public CoralShooter() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new CoralShooterControl(this));

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

	 public boolean getPhotoSensor() {
        boolean here=photoSensor.get()?false:true;
		SmartDashboard.putBoolean("photoSensor",here);
		return(here);
	}	

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runCoralShooter(0);
	}
}