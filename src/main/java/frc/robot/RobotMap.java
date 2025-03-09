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

package frc.robot;

public class RobotMap {
    public static double joystickDrift = 0.15;

	public static int robotID=0;

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

	public static final int elevatorExtendedPosition=250;
	public static final int elevatorRetractedPosition=0;
	public static final double elevatorBufferPercentage=.1;

	public static final int pickupExtendedPosition=250;
	public static final int pickupRetracedPosition=0;

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
	// CTRE components
	public static final int pigeonCanID = 50;
	public static final int canRangeCanID = 51;
	public static final int canRangeCanID2 = 52;

	///////////////////////////////////////////////////////////////////////////
	public static final int LidarChannel = 3; 

    ///////////////////////////////////////////////////////////////////////////
	//Elevator Motor Can ID's
	public static final int ElevatorLeftCanID = 40;
	public static final int ElevatorRightCanID = 47;
	public static final int ElevatorExtensionID = 25;

	///////////////////////////////////////////////////////////////////////////
	//CoralShooter Motor Can ID's
	public static final int CoralShooterCanID = 17;

		///////////////////////////////////////////////////////////////////////////
	//CoralShooter Motor Can ID's
	public static final int BallPickupRaiseLowerCanID = 43;
	public static final int BallPickupWheelCanID = 44;

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
			// 2025 DriveBase

			///////////////////////////////////////////////////////////////////////////
			// Swerve Drive Motors 
			swerveFrontRightDriveCanID = 11;
			swerveFrontRightTurnCanID = 19;
			swerveFrontLeftDriveCanID = 12;
			swerveFrontLeftTurnCanID = 18;
			swerveRearRightDriveCanID = 13;
			swerveRearRightTurnCanID = 16;
			swerveRearLeftDriveCanID = 10;
			swerveRearLeftTurnCanID = 9;

			///////////////////////////////////////////////////////////////////////////
			// Swerve Drive Encoders
			SwerveFrontRightEncoderCanID = 30;
			SwerveFrontLeftEncoderCanID = 32;
			SwerveRearRightEncoderCanID = 33;
			SwerveRearLeftEncoderCanID = 31;

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

	POV-Up:
	POV-Down: 
	POV-Right:
	POV_Left:

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
    Left Joystick Y-Axis: Run Coral Shooter
	Left Joystick X-Axis:
	Right Joystick Y-Axis: Elevator Up and Down
	Right Joystick X-Axis:

	POV-Up: Raise Ball Pickup
	POV-Down: Lower Ball Pickup
	POV-Right: Run Ball Pickup In
	POV_Left: Run Ball Pickup Out

	A Button: Hold Button, elevator and extension to bottom 
	B Button: Hold Button, elevator and extension to first Coral Position
	X Button: Hold Button, elevator and extension to second Coral Position
	Y Button: Hold Button, elevator and extension to thrid Coral Position

	Left Trigger: Lower elevator extension
	Right Trigger: Raise elevator extension

	Left Shoulder Button: Hold Button, Raise Ball Pickup'
	Right Shoulder Button: Hold Button, Lower and run Ball Pickup

	Back Button: Ignore encoders (to lower thrower angle)
	Start Button:
	
******************************************************************************/

