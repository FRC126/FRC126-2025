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
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import com.ctre.phoenix6.*;
import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkRelativeEncoder;

/**********************************************************************************
 **********************************************************************************/

public class SwerveDrive extends SubsystemBase {
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Swerve Motors
    CANSparkMax swerveFrontRightDriveMotor = new CANSparkMax(RobotMap.swerveFrontRightDriveCanID, CANSparkMax.MotorType.kBrushless);
    CANSparkMax swerveFrontRightTurnMotor = new CANSparkMax(RobotMap.swerveFrontRightTurnCanID, CANSparkMax.MotorType.kBrushless);

    CANSparkMax swerveFrontLeftDriveMotor = new CANSparkMax(RobotMap.swerveFrontLeftDriveCanID, CANSparkMax.MotorType.kBrushless);
    CANSparkMax swerveFrontLeftTurnMotor = new CANSparkMax(RobotMap.swerveFrontLeftTurnCanID, CANSparkMax.MotorType.kBrushless);

    CANSparkMax swerveRearLeftDriveMotor = new CANSparkMax(RobotMap.swerveRearLeftDriveCanID, CANSparkMax.MotorType.kBrushless);
    CANSparkMax swerveRearLeftTurnMotor = new CANSparkMax(RobotMap.swerveRearLeftTurnCanID, CANSparkMax.MotorType.kBrushless);

    CANSparkMax swerveRearRightDriveMotor = new CANSparkMax(RobotMap.swerveRearRightDriveCanID, CANSparkMax.MotorType.kBrushless);
    CANSparkMax swerveRearRightTurnMotor = new CANSparkMax(RobotMap.swerveRearRightTurnCanID, CANSparkMax.MotorType.kBrushless);

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Built in Motor Encoders for Swerve Drive
    RelativeEncoder swerveFrontRightDriveRelativeEncoder = swerveFrontRightDriveMotor.getEncoder(SparkRelativeEncoder.Type.kHallSensor, RobotMap.NeoTicksPerRotation);
    RelativeEncoder swerveFrontRightTurnRelativeEncoder = swerveFrontRightTurnMotor.getEncoder(SparkRelativeEncoder.Type.kHallSensor, RobotMap.NeoTicksPerRotation);

    RelativeEncoder swerveFrontLeftDriveRelativeEncoder = swerveFrontLeftDriveMotor.getEncoder(SparkRelativeEncoder.Type.kHallSensor, RobotMap.NeoTicksPerRotation);
    RelativeEncoder swerveFrontLeftTurnRelativeEncoder = swerveFrontLeftTurnMotor.getEncoder(SparkRelativeEncoder.Type.kHallSensor, RobotMap.NeoTicksPerRotation);

    RelativeEncoder swerveRearLeftDriveRelativeEncoder = swerveRearLeftDriveMotor.getEncoder(SparkRelativeEncoder.Type.kHallSensor, RobotMap.NeoTicksPerRotation);
    RelativeEncoder swerveRearLeftTurnRelativeEncoder = swerveRearLeftTurnMotor.getEncoder(SparkRelativeEncoder.Type.kHallSensor, RobotMap.NeoTicksPerRotation);

    RelativeEncoder swerveRearRightDriveRelativeEncoder = swerveRearRightDriveMotor.getEncoder(SparkRelativeEncoder.Type.kHallSensor, RobotMap.NeoTicksPerRotation);
    RelativeEncoder swerveRearRightTurnRelativeEncoder = swerveRearRightTurnMotor.getEncoder(SparkRelativeEncoder.Type.kHallSensor, RobotMap.NeoTicksPerRotation);

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Swerve Drive CAN Coders
    CANcoder swerveFrontRightEncoder = new CANcoder(RobotMap.SwerveFrontRightEncoderCanID);
    CANcoder swerveFrontLeftEncoder = new CANcoder(RobotMap.SwerveFrontLeftEncoderCanID);
    CANcoder swerveRearRightEncoder = new CANcoder(RobotMap.SwerveRearRightEncoderCanID);
    CANcoder swerveRearLeftEncoder = new CANcoder(RobotMap.SwerveRearLeftEncoderCanID);
	
    boolean swerveDebug=false;
	boolean enableFullSpeed=true;
	boolean autoMove=false;

	double[] wheelSpeed = {0,0,0,0};

	static final int frontLeft=0;
	static final int frontRight=1;
	static final int rearLeft=2;
	static final int rearRight=3;

	boolean areBrakesOn = false;
	boolean driveSlow = false;
	
    double frontLeftRPM, frontRightRPM, rearLeftRPM, rearRightRPM;
	double previousLimiter = 1;
	double fbLast=0;
	double rotLast=0;
    double pi = 3.14159;
    double inchesPerMeter = 2.54/100;

	public static SequentialCommandGroup autoCommand;
	public final double LENGTH = 26 * inchesPerMeter;
	public final double WIDTH = 26 * inchesPerMeter;

	private static final double testTurnRatio = .6;
	private static final double testSpeedRatio = .7;
	private static final double competitionTurnRatio = 1.0;
	private static final double competitionSpeedRatio = 1.0;

	private double currentTurnRatio = testTurnRatio;
	private double currentSpeedRatio = testSpeedRatio;
			
	/************************************************************************
	 ************************************************************************/

	public SwerveDrive() {
		// Register this subsystem with command scheduler and set the default command
		CommandScheduler.getInstance().registerSubsystem(this);
		setDefaultCommand(new SwerveControl(this));

		wheelSpeed[frontLeft] = 0;
		wheelSpeed[frontRight] = 0;
		wheelSpeed[rearLeft] = 0;
		wheelSpeed[rearRight] = 0;

		swerveFrontRightTurnMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
		swerveFrontLeftTurnMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
		swerveRearLeftTurnMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
		swerveRearRightTurnMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);

		if (RobotMap.robotID == 0) {
			enableFullSpeed=false;
		}		
	}

	/************************************************************************
	 ************************************************************************/

	public void periodic() {}

    /************************************************************************
	 ************************************************************************/

	public void toggleFullSpeed() {
		if (enableFullSpeed) { 
			enableFullSpeed=false;
		} else {
			enableFullSpeed=true;
		}	
	} 

	/************************************************************************
	 ************************************************************************/

	 public double getYaw() {
        double angle=0;

        if (Robot.useNavx) { 
			angle=Robot.navxMXP.getAngle();
		} else {
		    angle=Robot.internalData.getGyroAngle();
		}	

		angle=angle + RobotMap.yawOffset;

		return(angle);
	}

	/************************************************************************
	 ************************************************************************/

	 public void resetYaw() {
		resetYaw(0);
	}	

	/************************************************************************
	 ************************************************************************/

	 public void resetYaw(double value) {
        if (Robot.useNavx) { 
			if (value==0) {
			    Robot.navxMXP.zeroYaw();
			} else {
				Robot.navxMXP.setAngleAdjustment(value);
			}	
		} else {	
		    Robot.internalData.resetGyro();
		}	
	} 

    /************************************************************************
	 ************************************************************************/

	public boolean driveSlow(boolean in) {
		boolean ret=false;
		if (driveSlow != in) { ret = true; }
		driveSlow=in;
		return(ret);
	} 

    /************************************************************************
	 ************************************************************************/

	public boolean brakesOn() {
		if (!areBrakesOn) {
			swerveFrontRightDriveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
			swerveFrontLeftDriveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
			swerveRearLeftDriveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
			swerveRearRightDriveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
			areBrakesOn=true;
            return(true);
		}	
		return(false);
	}

	/************************************************************************
	 ************************************************************************/

	public boolean brakesOff() {
		if (areBrakesOn) {
			swerveFrontRightDriveMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
			swerveFrontLeftDriveMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
			swerveRearLeftDriveMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
			swerveRearRightDriveMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
			areBrakesOn=false;
			return(true);
		}	
		return(false);
	}

	/************************************************************************
	 ************************************************************************/

	 public double CalcTurnSpeed(double targetAngle, double currentAngle) {
		double speed=0;
		double reverse=1;
		boolean skip=false;

		// If it's quicker, turn the other way
	    if (targetAngle < -0.25 && currentAngle > .25) { reverse=-1; }
        if (targetAngle > .25 && currentAngle < -.25) { reverse=-1; }

		// Skip the fast move if we are just crossing the boundry
        if (targetAngle < -0.45 && currentAngle > .45) { skip=true; }
        if (targetAngle > .45 && currentAngle < -.45) { skip=true; }

     	//SmartDashboard.putNumber("reverse angle", reverse);

		if ( targetAngle < (currentAngle) - 0.1 && !skip) {
			speed=-0.4 * reverse;
		} else if (targetAngle > (currentAngle + 0.1) && !skip) {
			speed=0.4 * reverse;
		} else if ( targetAngle < (currentAngle - 0.02) && !skip ) {
			speed=-0.1 * reverse;
		} else if (targetAngle > (currentAngle + 0.02) && !skip) {
			speed=0.1 * reverse;
		} else if ( targetAngle < (currentAngle - 0.0010) ) {
			speed=-0.025 * reverse;
		} else if (targetAngle > (currentAngle + 0.0010) ) {
			speed=0.025 * reverse;
		} else if ( targetAngle < (currentAngle - 0.0005) ) {
			speed=-0.01 * reverse;
		} else if (targetAngle > (currentAngle + 0.0005) ) {
			speed=0.01 * reverse;
		}
		return(speed*currentTurnRatio);
	}

	/************************************************************************
	 * Soft start for accelleration to make it more controlable.
	 ************************************************************************/

	 public double smoothWheelSpeed(double input, int index) {
        double result=0;

    	double softStartIncrement=0.06;

		if (driveSlow) {
			// Cap at 20 percent for driveSlow
			if (input > 0.25) { input=0.25; }
		} else {
			// Cap at 50 percent for now
			if (input > 0.5 && !enableFullSpeed ) { input=0.5; }
		}

        if (input > 0) {
			// if the input speed is positive
			if (input <= wheelSpeed[index]) {
				// if the input speed is less than last speed, just set to input
				result = input;
			} else if (input > wheelSpeed[index] ) {
				// if the input speed is greater than the last speed, increment last speed
				// and use that value
				result = wheelSpeed[index] + softStartIncrement;
			}
		} else if (input < 0) {
			// if the input speed is negative
			if (input >= wheelSpeed[index]) {
				// if the input speed is greater than last speed, just set to input
				result = input;
			} else if (input < wheelSpeed[index] ) {
				// if the input speed is less than the last speed, decrement last speed
				// and use that value
				result = wheelSpeed[index] - softStartIncrement;
			}

		}

		// Save the new speed in the class for future smoothing
		wheelSpeed[index] = result;
        return(wheelSpeed[index]);
	}

	/************************************************************************
	 ************************************************************************/

	 public void Drive(double forwardBackIn, double leftRightIn, double rotateIn) { 
        Drive(forwardBackIn, leftRightIn, rotateIn, false, 0);
	}		

	/************************************************************************
	 * Swerve Drive will speed and position calculations
	 * 
	 * https://jacobmisirian.gitbooks.io/frc-swerve-drive-programming/content/chapter1.html
	 ************************************************************************/

	public void Drive(double forwardBackIn, double leftRightIn, double rotateIn,
	                boolean driveStraight, double straightDegrees) { 
		if (SmartDashboard.getBoolean(Robot.COMPETITION_ROBOT, true)) {
			currentTurnRatio = competitionTurnRatio;
			currentSpeedRatio = competitionSpeedRatio;
		} else {
			currentTurnRatio = testTurnRatio;
			currentSpeedRatio = testSpeedRatio;
		}

		double forwardBack = forwardBackIn;
        double leftRight = leftRightIn;
		double rotate = rotateIn;
		double[] newWheelSpeed = {0,0,0,0};
		double rearRightAngle=0, rearLeftAngle=0, frontRightAngle=0, frontLeftAngle=0;

		if (!swerveDebug) { 
 		    // Log debug data to the smart dashboard
			SmartDashboard.putNumber("forwardBack", forwardBack);
			SmartDashboard.putNumber("leftRight", leftRight);
			SmartDashboard.putNumber("rotate", rotate);
		}	

		// Get the current angle of the robot, and rotate the control inputs the oppsite 
		// direction, and the controls are driver relative, not robot relative
        double currentAngle = getYaw();

		if (!Robot.isAutoCommand || Robot.internalData.isAuto()) {
			// 2 dimensional rotation of the control inputs corrected to make the motion
			// driver relative instead of robot relative
			double angle=Math.toRadians(currentAngle); 
			leftRight = ( leftRightIn * Math.cos(angle) - (forwardBackIn * Math.sin(angle)));
			forwardBack = ( forwardBackIn * Math.cos(angle) + (leftRightIn * Math.sin(angle)));
		}

		if (driveStraight) {
			// If driveStraight is true, keep the robot facing the right direction
			if (currentAngle < straightDegrees-1.0) {
				rotate=.015;	
				if (leftRight > .2 || leftRight < -.2 || forwardBack > .2 || forwardBack < -.2 ) { rotate=.05; }
				if (leftRight > .4 || leftRight < -.4 || forwardBack > .4 || forwardBack < -.4 ) { rotate=.15; }
			} else if (currentAngle > straightDegrees+1.0) {
				rotate=-.015;	
				if (leftRight > .2 || leftRight < -.2 || forwardBack > .2 || forwardBack < -.2 ) { rotate=-.05; }
				if (leftRight > .4 || leftRight < -.4 || forwardBack > .4 || forwardBack < -.4 ) { rotate=-.15; }
			} else {
				rotate=0;
			}
		}

		// Get the Encoder information from each swerve drive module
    	StatusSignal<Double> FRPosSS = swerveFrontRightEncoder.getAbsolutePosition();
		StatusSignal<Double> FLPosSS = swerveFrontLeftEncoder.getAbsolutePosition();
		StatusSignal<Double> RRPosSS = swerveRearRightEncoder.getAbsolutePosition();
		StatusSignal<Double> RLPosSS = swerveRearLeftEncoder.getAbsolutePosition();

		double frontRightPos = FRPosSS.getValueAsDouble();
		double frontLeftPos = FLPosSS.getValueAsDouble();
		double rearRightPos = RRPosSS.getValueAsDouble();
		double rearLeftPos = RLPosSS.getValueAsDouble();

		if (forwardBack == 0 && leftRight == 0 && rotate == 0) {
			// If not joysticks are moved, just stop the motors, and
			// zero the speed offsets
            swerveFrontRightDriveMotor.set(0);
			swerveFrontLeftDriveMotor.set(0);
			swerveRearLeftDriveMotor.set(0);
			swerveRearRightDriveMotor.set(0);

			swerveFrontRightTurnMotor.set(0);
   			swerveFrontLeftTurnMotor.set(0);
   			swerveRearRightTurnMotor.set(0);
   			swerveRearLeftTurnMotor.set(0);

	        newWheelSpeed[frontRight] = smoothWheelSpeed(0,frontRight);
			newWheelSpeed[frontLeft] = smoothWheelSpeed(0,frontLeft);
			newWheelSpeed[rearRight] = smoothWheelSpeed(0,rearRight);
			newWheelSpeed[rearLeft] = smoothWheelSpeed(0,rearLeft);
		} else {
			// Calculate the speed and position of the 4 wheels based on 
			// the joystick input

			double r = Math.sqrt ((LENGTH * LENGTH) + (WIDTH * WIDTH));
			forwardBack *= RobotMap.frontBackInversion;
			leftRight *= RobotMap.leftRightInversion;
			rotate *= RobotMap.rotateInversion;

			double a = leftRight - rotate * (LENGTH / r);
			double b = leftRight + rotate * (LENGTH / r);
			double c = forwardBack - rotate * (WIDTH / r);
			double d = forwardBack + rotate * (WIDTH / r);
		
			newWheelSpeed[rearRight] = Math.sqrt ((a * a) + (d * d));
			newWheelSpeed[rearLeft] = Math.sqrt ((a * a) + (c * c));
			newWheelSpeed[frontRight] = Math.sqrt ((b * b) + (d * d));
			newWheelSpeed[frontLeft] = Math.sqrt ((b * b) + (c * c));

			rearRightAngle = Math.atan2 (a, d) / pi *.49;
			rearLeftAngle = Math.atan2 (a, c) / pi * .49;
			frontRightAngle = Math.atan2 (b, d) / pi * .49;
			frontLeftAngle = Math.atan2 (b, c) / pi * .49;

			// Run the turning motors based on the calculated target
			swerveFrontRightTurnMotor.set(CalcTurnSpeed(frontRightPos,frontRightAngle));
			swerveFrontLeftTurnMotor.set(CalcTurnSpeed(frontLeftPos,frontLeftAngle));
			swerveRearLeftTurnMotor.set(CalcTurnSpeed(rearLeftPos,rearLeftAngle));
			swerveRearRightTurnMotor.set(CalcTurnSpeed(rearRightPos,rearRightAngle));
			
			// Smooth the wheel speed so the robot isn't so jumpy
			newWheelSpeed[frontRight] = smoothWheelSpeed(newWheelSpeed[frontRight],frontRight);
			newWheelSpeed[frontLeft] = smoothWheelSpeed(newWheelSpeed[frontLeft],frontLeft);
			newWheelSpeed[rearRight] = smoothWheelSpeed(newWheelSpeed[rearRight],rearRight);
			newWheelSpeed[rearLeft] = smoothWheelSpeed(newWheelSpeed[rearLeft],rearLeft);

			// Run the drive motors to the smoothed speed
			swerveFrontRightDriveMotor.set(newWheelSpeed[frontRight] * currentSpeedRatio * RobotMap.SwerveFrontRightInversion);
			swerveFrontLeftDriveMotor.set(newWheelSpeed[frontLeft] * currentSpeedRatio  * RobotMap.SwerveFrontLeftInversion);
			swerveRearLeftDriveMotor.set(newWheelSpeed[rearLeft] * currentSpeedRatio  * RobotMap.SwerveRearLeftInversion);
			swerveRearRightDriveMotor.set(newWheelSpeed[rearRight] * currentSpeedRatio  * RobotMap.SwerveRearRightInversion);
		}

   		SmartDashboard.putNumber("currentAngle", currentAngle);

		if (swerveDebug) { 
 		    // Log debug data to the smart dashboard
			SmartDashboard.putNumber("forwardBack", forwardBack);
			SmartDashboard.putNumber("leftRight", leftRight);

			SmartDashboard.putNumber("frontRightPos", frontRightPos);
			SmartDashboard.putNumber("frontLeftPos", frontLeftPos);
			SmartDashboard.putNumber("rearRightPos", rearRightPos);
			SmartDashboard.putNumber("rearLeftPos", rearLeftPos);

			SmartDashboard.putNumber("frontRightSpeed", newWheelSpeed[frontRight]);
			SmartDashboard.putNumber("frontLeftSpeed", newWheelSpeed[frontLeft]);
			SmartDashboard.putNumber("rearRightSpeed", newWheelSpeed[rearRight]);
			SmartDashboard.putNumber("rearLeftSpeed", newWheelSpeed[rearLeft]);

			SmartDashboard.putNumber("frontRightAngle", frontRightAngle);
			SmartDashboard.putNumber("frontLeftAngle", frontLeftAngle);
			SmartDashboard.putNumber("rearRightAngle", rearRightAngle);
			SmartDashboard.putNumber("rearLeftAngle", rearLeftAngle);
  		}

		getDistanceInches();

	}

    /************************************************************************
	 ************************************************************************/

	public void resetEncoders() {
    	swerveFrontLeftDriveRelativeEncoder.setPosition(0);
    	swerveRearLeftDriveRelativeEncoder.setPosition(0);
    	swerveFrontRightDriveRelativeEncoder.setPosition(0);
    	swerveRearRightDriveRelativeEncoder.setPosition(0);
	}

    /************************************************************************
	 ************************************************************************/

	public double getDistanceInches() {
		double wheelDiameter = 4;
		//double gearRatio=18;
		double gearRatio=14;
		
		double left1 = swerveFrontLeftDriveRelativeEncoder.getPosition() * -1;
		double left2 = swerveFrontLeftDriveRelativeEncoder.getPosition() * -1;
		double right1 = swerveFrontRightDriveRelativeEncoder.getPosition();
		double right2 = swerveRearRightDriveRelativeEncoder.getPosition();

		double avg = Math.abs((left1 + left2 + right1 + right2) / 4);
	
		double distance = (avg / gearRatio) * (wheelDiameter * 3.1459);

		if (swerveDebug) { 
  			SmartDashboard.putNumber("Drive Distance",distance);
  			SmartDashboard.putNumber("Drive AVG",avg);
		}	

		return(distance);
	}

    /************************************************************************
	 *************************************************************************/

	public double rotateToDegrees(double offset) {
		double startAngle = Robot.swerveDrive.getYaw();    

		return(rotateToDegrees(offset,startAngle));
	}

    /************************************************************************
	 *************************************************************************/

	public double rotateToDegrees(double offset, double startAngle) {
		double driveRotate=0;
		double driftAllowance=1.00;
		//double currentAngle=Robot.swerveDrive.getYaw();    


		double target = startAngle + offset;
		double diff = Math.abs(target) - Math.abs(startAngle);
		//double diff = Math.abs(target) - Math.abs(currentAngle);

		double tmp = diff / 250;
		tmp = Robot.boundSpeed(tmp, .25, .03 );

		if (Math.abs(diff) < driftAllowance) {
			driveRotate=0;
			Robot.swerveDrive.brakesOn();
		} else if (startAngle < target) {
			driveRotate=tmp;
		} else {
			driveRotate=tmp*-1;
		}

        if (swerveDebug) {
            SmartDashboard.putNumber("Turn Current Degrees",startAngle);
            SmartDashboard.putNumber("Turn Target Degrees",target);
            SmartDashboard.putNumber("Turn diff",diff);
        }
		
		Robot.swerveDrive.Drive(0, 0, driveRotate);

		return(driveRotate);
	}

    /************************************************************************
	 *************************************************************************/

	public double rotateToDegreesFixed(double offset, double startAngle) {
		double driveRotate=0;
		double driftAllowance=1.00;
		double currentAngle=Robot.swerveDrive.getYaw();    

		double target = startAngle + offset;
		double diff = Math.abs(target) - Math.abs(currentAngle);



		double tmp = diff / 100;
		tmp = Robot.boundSpeed(tmp, .25, .04 );

		if (Math.abs(diff) < driftAllowance) {
			driveRotate=0;
			Robot.swerveDrive.brakesOn();
		} else if (currentAngle < target) {
			driveRotate=tmp;
		} else {
			driveRotate=tmp*-1;
		}

        if (swerveDebug) {
            SmartDashboard.putNumber("Turn Current Degrees",startAngle);
            SmartDashboard.putNumber("Turn Target Degrees",target);
            SmartDashboard.putNumber("Turn diff",diff);
        }
		
		Robot.swerveDrive.Drive(0, 0, driveRotate);

		return(driveRotate);
	}	

    /************************************************************************
	 *************************************************************************/
	 
	public void cancel() {
        Drive(0,0,0); 
		Robot.swerveDrive.brakesOn();
	}

	/************************************************************************
	 ************************************************************************/

	 public void setAutoMove(boolean value) { 
		autoMove=value;  
	}
	
	/************************************************************************
	 ************************************************************************/

	public boolean getAutoMove() { 
		return(autoMove); 
	}

}

