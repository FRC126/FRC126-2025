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

import com.revrobotics.spark.config.SparkBaseConfig;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**********************************************************************************
 **********************************************************************************/

public class CoralShooter extends SubsystemBase {
	boolean coralShooterDebug = false;
	int called = 0;
	static boolean sensorTriggered = false;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Pickup CAN Motor
    SparkMax motor = new SparkMax(RobotMap.CoralShooterCanID, SparkMax.MotorType.kBrushless);
	SparkMaxConfig motorConfig = new SparkMaxConfig();

	// Photo sensor to stop the shooter once it has hold of the coral
	DigitalInput photoSensor = new DigitalInput(7);

	/************************************************************************
	 ************************************************************************/

	public CoralShooter() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new CoralShooterControl(this));

		motorConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);
		motor.configure(motorConfig, null, null);

		sensorTriggered = false;
	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {
	}

	/************************************************************************
	 ************************************************************************/

	public  void runCoralShooter(double speed) {
		motor.set(speed);
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

	public boolean getSensorTriggered() {
		return(sensorTriggered);
	}

	/************************************************************************
	 ************************************************************************/

	public void setSensorTriggered(boolean triggered) {
		sensorTriggered = triggered;
	}

	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runCoralShooter(0);
		sensorTriggered = false;
	}
}