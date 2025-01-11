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

package frc.robot;

public class RobotMap {

	public static int robotID=1;

    public static int NeoTicksPerRotation=42;
	public static double ThrowerGearRatio=750;

	public static int ampAngle=140;
	public static int ampSpeed=600;

	public static int throwerSpeed=3000;
	public static int idleSpeed=2000;
	public static int throwerCloseAngle=62;

    public static int throwerSixtyTicks=83;
	public static int throwerBottom=0;
	public static int throwerTop=throwerSixtyTicks+170;



	///////////////////////////////////////////////////////////////////////////
	// Controls for Xbox 360 / Xbox One
	public static final int lStickX = 0; // Left stick X
	public static final int lStickY = 1; // Left stick Y
	public static final int rStickX = 4; // Right stick X
	public static final int rStickY = 5; // Right stick Y
	public static final int Rtrigger = 3; // Right trigger
	public static final int Ltrigger = 2; // Left trigger
	public static final int xboxA = 1; // A
	public static final int xboxB = 2; // B
	public static final int xboxX = 3; // X
	public static final int xboxY = 4; // Y
	public static final int xboxLTrig = 5; // Left trigger button
	public static final int xboxRTrig = 6; // Right trigger button
	public static final int xboxBack = 7; // Back
	public static final int xboxStart = 8; // Start
	public static final int xboxLStick = 9; // Left stick button
	public static final int xboxRStick = 10; // Right stick button

	///////////////////////////////////////////////////////////////////////////
 	// Thrower Motor Can ID's
	public static final int throwerTalonMotorOneCanID = 26;
	public static final int throwerTalonMotorTwoCanID = 27;
	public static final int throwerTriggerMotorCanID = 28;
	public static final int throwerClimberMotorLeftCanID = 24;
	public static final int throwerClimberMotorRightCanID = 25;

	///////////////////////////////////////////////////////////////////////////
	public static final int LidarChannel = 3; 

	///////////////////////////////////////////////////////////////////////////
	//Pickup Motor Can ID's
	public static final int PickupCanID = 50;

    ///////////////////////////////////////////////////////////////////////////
	//Pickup Motor Can ID's
	public static final int ClimberCanID = 51;

	///////////////////////////////////////////////////////////////////////////
	// Swerve Drive Motors 
    public static int swerveFrontRightDriveCanID;
    public static int swerveFrontRightTurnCanID;
    public static int swerveFrontLeftDriveCanID;
    public static int swerveFrontLeftTurnCanID;
    public static int swerveRearRightDriveCanID;
    public static int swerveRearRightTurnCanID;
    public static int swerveRearLeftDriveCanID;
    public static int swerveRearLeftTurnCanID;

	///////////////////////////////////////////////////////////////////////////
	// Swerve Drive Encoders
	public static int SwerveFrontRightEncoderCanID;
	public static int SwerveFrontLeftEncoderCanID;
	public static int SwerveRearRightEncoderCanID;
	public static int SwerveRearLeftEncoderCanID;

	public static double yawOffset;

	public static int frontBackInversion;
	public static int leftRightInversion;
	public static int rotateInversion;

	public static int SwerveFrontRightInversion;
	public static int SwerveFrontLeftInversion;
	public static int SwerveRearRightInversion;
	public static int SwerveRearLeftInversion;

	/************************************************************************
	 * 
	 ************************************************************************/
	
	public static void setRobot(int robotIDIn){
		robotID = robotIDIn;

		if(robotID == 0) { 
			// 2024 DriveBase

			///////////////////////////////////////////////////////////////////////////
			// Swerve Drive Motors 
			swerveFrontRightDriveCanID = 40;
			swerveFrontRightTurnCanID = 41;
			swerveFrontLeftDriveCanID = 43;
			swerveFrontLeftTurnCanID = 42;
			swerveRearRightDriveCanID = 46;
			swerveRearRightTurnCanID = 47;
			swerveRearLeftDriveCanID = 44;
			swerveRearLeftTurnCanID = 45;

			///////////////////////////////////////////////////////////////////////////
			// Swerve Drive Encoders
			SwerveFrontRightEncoderCanID = 30;
			SwerveFrontLeftEncoderCanID = 31;
			SwerveRearRightEncoderCanID = 33;
			SwerveRearLeftEncoderCanID = 32;

			yawOffset=0;

			frontBackInversion=-1;
			leftRightInversion=1;
			rotateInversion=1;

	        SwerveFrontRightInversion=1;
			SwerveFrontLeftInversion=1;
			SwerveRearRightInversion=1;
			SwerveRearLeftInversion=1;

		} else { 
			// 2024 Official Robot

			///////////////////////////////////////////////////////////////////////////
			// Swerve Drive Motors 
			swerveFrontRightDriveCanID = 41;
			swerveFrontRightTurnCanID = 40;
			swerveFrontLeftDriveCanID = 42;
			swerveFrontLeftTurnCanID = 43;
			swerveRearRightDriveCanID = 47;
			swerveRearRightTurnCanID = 46;
			swerveRearLeftDriveCanID = 44;
			swerveRearLeftTurnCanID = 45;

			///////////////////////////////////////////////////////////////////////////
			// Swerve Drive Encoders
			SwerveFrontRightEncoderCanID = 30;
			SwerveFrontLeftEncoderCanID = 31;
			SwerveRearRightEncoderCanID = 32;
			SwerveRearLeftEncoderCanID = 33;

			yawOffset=0;

			frontBackInversion=1;
			leftRightInversion=-1;
			rotateInversion=-1;

	        SwerveFrontRightInversion=1;
			SwerveFrontLeftInversion=-1;
			SwerveRearRightInversion=1;
			SwerveRearLeftInversion=-1;
		}
	}
}


/******************************************************************************
Controls

Driver:
    Left Joystick Y-Axis: Robot Forward and Backwards (LEDs light up green)
	Left Joystick X-Axis: Robot Left and Right (LEDs light up green)
	Right Joystick Y-Axis:
	Right Joystick X-Axis: Robot Rotate Left and Right (LEDs light up green)

	POV-Up: Hold down for active aiming at selected target
	POV-Down: 
	POV-Right: Seek Target 2 (LEDs light up green and blue when targeting)
	POV_Left: Seek Target 1 (LEDs light up green and red when targeting)

	A Button:
	B Button: Zero Drive Gyro
	X Button: Cancel Auto Command
	Y Button:

	Left Trigger: Drive Slow (25%) (LED's light up Yellow)
	Right Trigger: Brake Mode On (LED's light up Red)

	Left Shoulder Button:
	Right Shoulder Button:

	Back Button:
	Start Button: Toggle Full Speed (On by default)

Operator:
    Left Joystick Y-Axis: Move Thrower Up and Down
	Left Joystick X-Axis:
	Right Joystick Y-Axis: Climber Up and Down (LEDs light up rainbow)
	Right Joystick X-Axis:

	POV-Up: Set Thrower to Climb Position
	POV-Down: Set Thrower to Down Position
	POV-Right: set Thrower to 45 degrees
	POV_Left: set Thrower to 145 degrees

	A Button: Hold A button, trigger throws once wheels spin up. 
	B Button: Toggle Thrower Idle on and off
	X Button: Run Pick and Thrower Trigger at the same time 
	Y Button: Pickup 

	Left Trigger: Run Pickup (LEDs light up green and white)
	Right Trigger: Hold Right Shoulder Button, trigger throws once wheels spin up (LEDs light purple and blue).

	Left Shoulder Button: Reverse Pickup, Thrower Trigger and Thrower WHeels
	Right Shoulder Button: Auto Amp Throw

	Back Button: Ignore encoders (to lower thrower angle)
	Start Button:
******************************************************************************/

