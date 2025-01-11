// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

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

import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.subsystems.*;
import frc.robot.commands.*;

// Navx-MXP Libraries and Connection Library
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
    // Global Robot Variables
    public int RobotID = 1;  
    public static boolean useNavx=true;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // NavX-MXP
    public static AHRS navxMXP;

     /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Auto Routines
    public static boolean isAutoCommand=false;
    public static SequentialCommandGroup autoCommand;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Subsystems
    public static Controllers oi;
    public static Log log;
    public static InternalData internalData;
    public static SwerveDrive swerveDrive;
    public static Thrower thrower;    
    public static Climber climber;
    public static Pickup pickup;
    public static LEDSubsystem Leds;
    // Lidar Light Distance Measure
    //public static LidarLite lidar;
    // Lime Light
    //public static LimeLight limeLight;

	public static UsbCamera driveCam;
	public static VideoSink server;
    public static JoystickWrapper driveJoystick;
    public static JoystickWrapper operatorJoystick;

    public static enum targetTypes{
        NoTarget(-1),TargetSeek(0), TargetRed(1), TargetBlue(2);
        private final int pipeline;
        private targetTypes(int v) {pipeline = v;}
        public int getPipeline() {
            return pipeline;
        }
    };
    public static enum allianceColor{Red,Blue};

    public static final int noAlliance=-1;
    public static final int redAlliance=0;
    public static final int blueAlliance=1;

    public static final int speakerAuto=0;
    public static final int ampAuto=1;

    public static final int oneNoteAutoNoMove=0;
    public static final int oneNoteAutoBackup=1;
    public static final int twoNoteAuto=2;
    public static final int threeNoteAuto=3;
    public static final int oneNoteAndAmp=4;
    public static final int oneNoteFarAutoBackup=5;
    public static final int TwoNoteFarAuto=6;
    public static final int justAutoBackup=7;
    public static final int twoNoteAutoMidField=8;
    public static final int autoNothing=9;
        public static final int sideShoot=10;

    
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Automation Variables
    public static SequentialCommandGroup autonomous;
    public static targetTypes targetType = Robot.targetTypes.TargetSeek;

    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean overrideEncoders=false;

    int selectedAutoPosition;
	int selectedAutoFunction;
    int selectedautoNext;
    int selectedAllianceColor;
	
    private final SendableChooser<Integer> autoFunction = new SendableChooser<>();
    private final SendableChooser<Integer> autoPosition = new SendableChooser<>();
    private final SendableChooser<Integer> autoNext = new SendableChooser<>();
    private final SendableChooser<Integer> allianceColor = new SendableChooser<>();

    public static final String COMPETITION_ROBOT = "Competition Robot";
    
 	  /************************************************************************
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
	   ************************************************************************/
    @Override
    public void robotInit() {
        // Set the robot id for use by RobotMap
        RobotMap.setRobot(RobotID);

        // Enable the command scheduler
        CommandScheduler.getInstance().enable();

        // Create and register the robot Subsystems
        oi = new Controllers();
        log = new Log();
        internalData = new InternalData();

        // Swerve drive subsystem 
        swerveDrive = new SwerveDrive();

        // Thrower Devices
        thrower = new Thrower();
        thrower.resetEncoders(RobotMap.throwerSixtyTicks);

        // Climber
        climber = new Climber();
  
        // Pickup SubSystem
        pickup = new Pickup();

        // LED Subsystem
        Leds = new LEDSubsystem();

        // Limelight subsystem1
        //limeLight = new LimeLight();
       
        // Navx Subsystem
        try {
            navxMXP = new AHRS(SPI.Port.kMXP);
        } catch (RuntimeException ex) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
    
        // Initialize the built in gyro
        internalData.initGyro();
        internalData.resetGyro();

        // create the lidarlite class
        // lidar = new LidarLite();

        // Server for the drive camera
        //driveCam = CameraServer.startAutomaticCapture();
		//server = CameraServer.getServer();
        //driveCam.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);
		//server.setSource(driveCam);

        SmartDashboard.putBoolean(COMPETITION_ROBOT, true);

        Log.print(0, "Git Info", "branch: %s buildDate: %s gitDate: %s sha: %s".formatted(
            BuildConstants.GIT_BRANCH,
            BuildConstants.BUILD_DATE,
            BuildConstants.GIT_DATE,
            BuildConstants.GIT_SHA
            ));
        Log.print(0, "Robot", "Robot Init Complete");
        // Put information above onto Smart Dashboard
        SmartDashboard.putString("Git Branch", BuildConstants.GIT_BRANCH);
        SmartDashboard.putString("Build Date", BuildConstants.BUILD_DATE);

        // Dashboard Cooser for the Autonomous mode move
        autoFunction.setDefaultOption("Speaker Shot",speakerAuto);
        //autoFunction.addOption("Amplifier",ampAuto);
        SmartDashboard.putData("Auto Target",autoFunction);

   
        // Dashboard Cooser for the Autonomous mode position
        allianceColor.setDefaultOption("No Alliance",noAlliance);
        allianceColor.addOption("Red Alliance",redAlliance);
        allianceColor.addOption("Blue Alliance",blueAlliance);
        SmartDashboard.putData("Alliance Color",allianceColor);
        
        autoNext.setDefaultOption("do nothing dummy!",autoNothing);
        autoNext.addOption("just backup",justAutoBackup);
        autoNext.addOption("1 note, do nothing",oneNoteAutoNoMove);
        autoNext.addOption("1 note far side (source), backup",oneNoteAutoBackup);
        autoNext.addOption("2 note close side (amp), backup",sideShoot);
        autoNext.addOption("2 notes center",twoNoteAuto);
        autoNext.addOption("2 notes center, go midfield",twoNoteAutoMidField);
        autoNext.addOption("3 notes center",threeNoteAuto);
        SmartDashboard.putData("Auto Follow Choices",autoNext);
    }

 	  /************************************************************************
	   * This function is run once each time the robot enters autonomous mode. 
     ************************************************************************/
    @Override
    public void autonomousInit() {
        autonomous=null;

        Log.print(0, "Robot", "Robot Autonomous Init");

		Robot.swerveDrive.cancel();
		Robot.swerveDrive.resetYaw();

		try {
			selectedautoNext = (int)autoNext.getSelected();
		} catch(NullPointerException e) {
			selectedautoNext = oneNoteAutoNoMove;
		}
		try {
			selectedAllianceColor = (int)allianceColor.getSelected();
		} catch(NullPointerException e) {
			selectedAllianceColor = noAlliance;
		}

        Robot.targetTypes target;
        if (selectedAllianceColor == redAlliance) {
            target=Robot.targetTypes.TargetRed; 
            Robot.targetType = Robot.targetTypes.TargetRed;
            // target ID=4
        } else if (selectedAllianceColor == blueAlliance) {
            target=Robot.targetTypes.TargetBlue; 
            Robot.targetType = Robot.targetTypes.TargetBlue;
            // target ID=7
        } else {
            target=Robot.targetTypes.TargetSeek; 
        }

        switch (selectedautoNext) {
            case threeNoteAuto:
                SmartDashboard.putString("AutoCommand","Speaker Three Notes");
                autonomous = new AutoShootThreeSpeaker(target); 
                break;
            case twoNoteAuto:
                SmartDashboard.putString("AutoCommand","Speaker Two Notes");
                autonomous = new AutoShootTwoSpeaker(target); 
                break;   
            case twoNoteAutoMidField:
                SmartDashboard.putString("AutoCommand","Speaker Two Notes - Midfield");
                autonomous = new AutoShootTwoSpeakerAndMidField(target); 
                break;   
            case oneNoteAutoNoMove:
                SmartDashboard.putString("AutoCommand","Speaker One Note, No Move");
                autonomous = new AutoShootSpeakerAndStop();
                break;
            case oneNoteAutoBackup:
                SmartDashboard.putString("AutoCommand","Speaker One Note - Backup");
                autonomous = new AutoShootSpeakerFarSideAndBackup(target);
                break;
            case justAutoBackup:
                SmartDashboard.putString("AutoCommand","Just Backup");
                autonomous = new AutoJustBackup();
                break;
            case sideShoot:
                SmartDashboard.putString("AutoCommand","Just Backup");
                autonomous = new AutoSideShootBackup(target);
                break;
            case autoNothing:
                // Do Nothing!
                autonomous=null;
                break;    
        }         

        if (autonomous != null) {
            autonomous.schedule();
        }   
    }

    /************************************************************************
     * This function is called periodically during autonomous.
    ************************************************************************/
    @Override
    public void autonomousPeriodic() {
        Robot.Leds.forceMode(LEDSubsystem.LEDModes.GaelForce);
        CommandScheduler.getInstance().run();
        Robot.Leds.doLights();
    }

    /************************************************************************
     * This function is called once each time the robot enters teleoperated mode.
    ************************************************************************/
    @Override
    public void teleopInit() { 
        Log.print(0, "Robot", "Robot Teleop Init");
  
        if(autonomous != null){
            // Cancel the auto command if it was created
	          autonomous.cancel();
        }

        Robot.stopAutoCommand();

		Robot.swerveDrive.cancel();
        Robot.swerveDrive.brakesOff();
        Robot.thrower.cancel();
        Robot.pickup.cancel();
    }

    /************************************************************************
     * This function is called periodically during teleoperated mode.
    ************************************************************************/
    @Override
    public void teleopPeriodic() {
        Robot.Leds.forceMode(LEDSubsystem.LEDModes.GaelForce);
        CommandScheduler.getInstance().run();
        Robot.Leds.doLights();
        check();
    }

    /************************************************************************
    ************************************************************************/

    private void check() {
        if (operatorJoystick==null) {
            operatorJoystick = new JoystickWrapper(Robot.oi.operatorController, 0.15);
        }
		
        //if (operatorJoystick.isRShoulderButton()) {
		//	if (Robot.doAutoCommand()) {
		//		Robot.swerveDrive.setAutoMove(true);
		//		Robot.autoCommand = new AutoAmp();
		//		Robot.autoCommand.schedule();
		//	}
		//}

        if (operatorJoystick.isBackButton()) {
            Robot.overrideEncoders=true;
            Robot.climber.setPosition(0);
            //Robot.thrower.resetEncoders(); 
        } else {
            Robot.overrideEncoders=false;
        }
    }

    /************************************************************************
     * This function is called once each time the robot enters test mode.  
    ************************************************************************/
    @Override
    public void testInit() {
        Log.print(0, "Robot", "Robot Test Init");

		Robot.swerveDrive.cancel();
    }  

    /************************************************************************
     * This function is called periodically during test mode.
    ************************************************************************/
   @Override
    public void testPeriodic() {
        Robot.Leds.forceMode(LEDSubsystem.LEDModes.GaelForce);
        CommandScheduler.getInstance().run();
        Robot.Leds.doLights();
}

    /************************************************************************
	 ************************************************************************/

    static public boolean checkAutoCommand() {
		if (Robot.internalData.isAuto()) {
            return true;
        }
		
		if (Robot.isAutoCommand) {
			return true;
		}	

        return false;
    }    


    /************************************************************************
	 ************************************************************************/

    static public boolean doAutoCommand() {
		if (Robot.internalData.isAuto()) {
            return false;
        }
		
		if (Robot.isAutoCommand) {
			return false;
		}	

        if (Robot.swerveDrive.getAutoMove()) { 
    		Robot.swerveDrive.cancel();
        }

	    Robot.isAutoCommand = true;

   		SmartDashboard.putBoolean("RobotIsAutoCommand",Robot.isAutoCommand);
		return true;
	}

    /************************************************************************
	 ************************************************************************/

	static public void stopAutoCommand() {
        if (Robot.isAutoCommand) {
            Robot.autoCommand.cancel();
		}	
		Robot.isAutoCommand=false;

        if (Robot.internalData.isAuto()) {
            return;
        }

        SmartDashboard.putBoolean("RobotIsAutoCommand",Robot.isAutoCommand);

      	Robot.swerveDrive.cancel();
        Robot.thrower.cancel();
        Robot.pickup.cancel();
        Robot.climber.cancel();

        Robot.swerveDrive.setAutoMove(false);
	}		

    /************************************************************************
	 ************************************************************************/

    static public double boundSpeed(double speedIn, double highSpeed, double lowSpeed ) {
        double speedOut=speedIn;

        if (speedIn < 0) {
            if (speedIn < highSpeed)  { speedOut = highSpeed; }
            if (speedIn > lowSpeed) { speedOut = lowSpeed; }  
        } else {
            if (speedIn > highSpeed)  { speedOut = highSpeed; }
            if (speedIn < lowSpeed) { speedOut = lowSpeed; }  
        }

        return(speedOut);
    }   

    /************************************************************************
	 ************************************************************************/

    static public int getDirection(targetTypes targetType) {
        if (targetType == Robot.targetTypes.TargetRed) {
            return(1);
        } else {
            return(-1);
        }        
    }    

}
