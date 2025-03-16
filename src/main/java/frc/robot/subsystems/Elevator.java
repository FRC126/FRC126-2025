/**********************************
	   _      ___      ____
	 /' \   /'___`\   /'___\
	/\_, \ /\_\ /\ \ /\ \__/
	\/_/\ \\/_/// /__\ \  _``\
	   \ \ \  // /_\ \\ \ \L\ \
	    \ \_\/\______/ \ \____/
		 \/_/\/_____/   \/___/

    Team 126 2025 Code       
	Go get em gaels!7
***********************************/

package frc.robot.subsystems;

import frc.robot.Robot;
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
	boolean autoMove=false;
	double startSpeed=0;
	double extStartSpeed=0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Pickup CAN Motor
    SparkMax leftMotor = new SparkMax(RobotMap.ElevatorLeftCanID, SparkMax.MotorType.kBrushless);
    SparkMax rightMotor = new SparkMax(RobotMap.ElevatorRightCanID, SparkMax.MotorType.kBrushless);
    SparkMax extensionMotor = new SparkMax(RobotMap.ElevatorExtensionID, SparkMax.MotorType.kBrushless);

	RelativeEncoder leftMotorEncoder = leftMotor.getEncoder();
    RelativeEncoder rightMotorEncoder = rightMotor.getEncoder();
    RelativeEncoder extensionMotorEncoder = extensionMotor.getEncoder();

	SparkMaxConfig motorConfig = new SparkMaxConfig();

    DigitalInput bottomLimit = new DigitalInput(2);

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

	private void runElevatorMotor(double speed) {
		leftMotor.set(speed*-1);
		rightMotor.set(speed);
	}

    /************************************************************************
	 ************************************************************************/

	 private boolean moveExtensionTarget(Robot.heightTargets targetIn) {
		double speed = 0;
		double currentPosition = getExtensionPosition();

		double target=0;
		switch(targetIn) {
			case LOne:              // Bottom
				target=0;
				break;
			case LTwo:              // Low
				target=0;
				break;
			case LThree:             // Middle
				target=0;
				break;
			case LFour:		    	// High
				target=55;
				break;
		}		
		SmartDashboard.putNumber("Extension target",target);
		SmartDashboard.putNumber("Extension current",currentPosition);

		if (target > currentPosition + .5) {
			speed=-.8;
			if (target - currentPosition < 8) { speed = -.4;}
			if (target - currentPosition < 4) { speed = -.25;}
			if (target - currentPosition < 1) { speed = -.1;}
		} else if (target < currentPosition - .5) {
			speed=.8;
			if (currentPosition - target < 8) { speed = .4;}
			if (currentPosition - target < 4) { speed = .25;}
			if (currentPosition - target < 1) { speed = .1;}
		} else {
			extStartSpeed=0;
			speed = 0;
		}

		moveExtension(speed);

		if (speed == 0) {
			return true;
		} else {
			return false;
		}
	}

	/************************************************************************
	 ************************************************************************/

	 public void moveExtension(double speedIn) {
        double speed = speedIn;
		double currentPosition = getExtensionPosition();

		if (Robot.overrideEncoders != true ) {
			if (speed > 0 && currentPosition < 20) { speed=speed*.5; }
            if (speed > 0 && currentPosition <= .1) { speed=0; }
		}	
		SmartDashboard.putNumber("Extension current2",currentPosition);
		SmartDashboard.putNumber("Extension speed",speed);

		if (speed < 0 && currentPosition > 62) { speed=speed*.5; }
        if (speed < 0 && currentPosition > 64) { speed=0; }
		
		runExtensionMotor(speed);
	}

	/************************************************************************
	 ************************************************************************/

	 public void runExtensionMotor(double speed) {
		extensionMotor.set(speed);
	}

	/************************************************************************
	 ************************************************************************/

	 private double getExtensionPosition() {
		double pos=extensionMotorEncoder.getPosition() * -1;

		SmartDashboard.putNumber("Extension Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	 public void setExtensionPosition(double value) {
		// We only need to set Position of encoder on Motor1
		extensionMotorEncoder.setPosition(value);
	}

 	/************************************************************************
	 ************************************************************************/

	private double getPosition() {
		double pos=rightMotorEncoder.getPosition();

		SmartDashboard.putNumber("Elevator Position",pos);

		return(pos);
	}

 	/************************************************************************
	 ************************************************************************/

	public void setPosition(double value) {
		// We only need to set Position of encoder on Motor1
		rightMotorEncoder.setPosition(value);
	}

    /************************************************************************
	 ************************************************************************/
 
	private boolean moveElevatorTarget(Robot.heightTargets targetIn) {
		double speed = 0;
		double currentPosition = getPosition();
		double topSpeed=1;
		double speedIncr = 0.15;
		double startSpeedIncr = .05;

		double target=0;
		switch(targetIn) {
			case LOne:              // Bottom
				target=0;
				break;
			case LTwo:              // Low
				target=32;
				break;
			case LThree:             // Middle
				target=65;
				break;
			case LFour:		    	// High
				target=111.5;
				break;
		}

		if (target > currentPosition + .5) {
			if ( startSpeed < topSpeed-.05) {
				speed = startSpeed + speedIncr;
				if (startSpeed < .3) {
					speed = startSpeed + startSpeedIncr;
				}
				startSpeed = speed;
			} else {
				speed = topSpeed;
			}
			if (target - currentPosition < 6) { speed = .4;}
			if (target - currentPosition < 3) { speed = .25;}
			if (target - currentPosition < 1) { speed = .15;}
		} else if (target < currentPosition - .5) {
			if ( startSpeed > (topSpeed -.05) * -1) {
				speed = startSpeed - speedIncr;
				if (startSpeed > -0.3) {
					speed = startSpeed + startSpeedIncr;
				}
				startSpeed=speed;
			} else {
				speed = topSpeed * -1;
			}
			if (currentPosition - target < 6) { speed = -.4;}
			if (currentPosition - target < 3) { speed = -.25;}
			if (currentPosition - target < 1) { speed = -.15;}
		} else {
			motorConfig.idleMode(SparkBaseConfig.IdleMode.kBrake);
	    	leftMotor.configure(motorConfig, null, null);
		    rightMotor.configure(motorConfig, null, null);
			startSpeed=0;
			speed = 0;
		}

		moveElevator(speed);

		if (speed == 0) {
			return(true);
		} else {
			return(false);
		}
	}

    /************************************************************************
	 ************************************************************************/	

	public void moveElevator(double speed) {

		if (speed > 1) {
			speed = 1;
		} else if (speed < -1) {
			speed = -1;
		}

		double currentPosition = getPosition();

		if (Robot.overrideEncoders != true ) {
			if (speed < 0 && currentPosition < 20) { speed=speed*.5; }
			if (speed < 0 && currentPosition <= 1) { speed=0; }
		}	

		if (speed > 0 && currentPosition >105) { speed=speed*.5; }
		if (speed > 0 && currentPosition >110) { speed=speed*.25; }
		if (speed > 0 && currentPosition >112) { speed=0; }

	    if (bottomLimit.get() == true && speed < 0) {
			speed = 0;
			setPosition(0);
		}

        if (speed > 0) {
			Robot.Leds.setMode(LEDs.LEDModes.ElevatorUp);
		} else if (speed < 0) {	
			Robot.Leds.setMode(LEDs.LEDModes.ElevatorDown);
		}

		runElevatorMotor(speed);
	}

    /************************************************************************
	 ************************************************************************/
    
	public boolean moveTarget(Robot.heightTargets targetIn) {
		boolean extReached, elevReached;

		extReached=moveExtensionTarget(targetIn); 
		elevReached=moveElevatorTarget(targetIn);

		if (extReached && elevReached) {
			return(true);
		}

		return(false);
	}

	/************************************************************************
	 ************************************************************************/

	public boolean getAutoMove() {
		return(autoMove);
	}

	/************************************************************************
	 ************************************************************************/

	 public void setAutoMove(boolean move) {
		autoMove = move;
	}

	/************************************************************************
	 ************************************************************************/

	 public void cancel() {
		runElevatorMotor(0);
		runExtensionMotor(0);
	}
}
