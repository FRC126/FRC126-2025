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

import com.revrobotics.spark.config.SparkBaseConfig;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**********************************************************************************
 **********************************************************************************/

public class CoralShooter extends SubsystemBase {
	boolean coralShooterDebug = false;
	int called = 0;
	boolean autoMove=false;
	int ejectCount=0;
	public static enum intakeState{None, Run, RunSlow, Backup, Done, Eject};

	intakeState State=intakeState.None;	

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
        boolean here=photoSensor.get()?true:false;
		SmartDashboard.putBoolean("photoSensor",here);
		return(here);
	}	

	/************************************************************************
	 ************************************************************************/

	 public boolean getAutoMove() {
		return(autoMove);
	}

	/************************************************************************
	 ************************************************************************/

	public void resetState() {
		State=intakeState.None;
	} 

	/************************************************************************
	 ************************************************************************/

	 public boolean isStateDone() {	
		if (State == intakeState.Done) {
			return true;
		}
		return false;
	} 

	/************************************************************************
	 ************************************************************************/

    public double doIntake(double y, boolean doEject) {

		Robot.Leds.setMode(LEDs.LEDModes.ShootingCoral);		

		if (doEject) {
			State = intakeState.Eject;
		} else {
			ejectCount=0;
		}

		switch (State) {
			case None:
				State=intakeState.Run;
				y=0;
				break;
			case Run:	 
				if (getPhotoSensor()) {
					State=intakeState.RunSlow;
					y=-.25;
				}
				break;	
			case RunSlow:
				if (!getPhotoSensor()) {
					State=intakeState.Backup;
					y=0;
				} else {
					y=-.25;
				}
				break;
			case Backup:
				if (getPhotoSensor()) {
					State=intakeState.Done;
					y=0;
				} else {
					y=.15;
				}
				break;	
			case Done:
				Robot.Leds.setMode(LEDs.LEDModes.Rainbow);
				y=0;
				break;	
			case Eject:
			    y=-.5;
				break;	
		}


		return(y);
	}

	/************************************************************************
	 ************************************************************************/

	 public void setAutoMove(boolean move) {
		autoMove = move;
	}
	
	/************************************************************************
	 ************************************************************************/

	public void cancel() {
		runCoralShooter(0);
	}
}