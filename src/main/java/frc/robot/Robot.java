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

    Team 126 2025 Code       
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
import frc.robot.subsystems.*;
//import edu.wpi.first.wpilibj.DriverStation;
//import frc.robot.commands.*;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
    // Global Robot Variables
    public int RobotID = 1;  

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Auto Routines
    public static boolean isAutoCommand=false;
    public static SequentialCommandGroup autoCommand;

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Subsystems
    public static Controllers oi;
    public static Log log;
    public static InternalData internalData;
    public static TestMotor testMotor;

    public static UsbCamera driveCam;
	public static VideoSink server;
    public static JoystickWrapper driveJoystick;
    public static JoystickWrapper operatorJoystick;

    public static enum allianceColor{Red,Blue};

    public static final int noAlliance=-1;
    public static final int redAlliance=0;
    public static final int blueAlliance=1;
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Automation Variables
    public static SequentialCommandGroup autonomous;

    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean overrideEncoders=false;

    int selectedAllianceColor;
	
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

        // Test Motor
        testMotor = new TestMotor();
     
        // Initialize the built in gyro
        internalData.initGyro();
        internalData.resetGyro();

        // Server for the drive camera
        //driveCam = CameraServer.startAutomaticCapture();
		//server = CameraServer.getServer();
        //driveCam.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);
		//server.setSource(driveCam);

        SmartDashboard.putBoolean(COMPETITION_ROBOT, true);

        Log.print(0, "Robot", "Robot Init Complete");
  
        // Dashboard Cooser for the Autonomous mode position
        allianceColor.setDefaultOption("No Alliance",noAlliance);
        allianceColor.addOption("Red Alliance",redAlliance);
        allianceColor.addOption("Blue Alliance",blueAlliance);
        SmartDashboard.putData("Alliance Color",allianceColor);        
    }

 	  /************************************************************************
	   * This function is run once each time the robot enters autonomous mode. 
     ************************************************************************/
    @Override
    public void autonomousInit() {
        autonomous=null;

        Log.print(0, "Robot", "Robot Autonomous Init");

		try {
			selectedAllianceColor = (int)allianceColor.getSelected();
		} catch(NullPointerException e) {
			selectedAllianceColor = noAlliance;
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
        CommandScheduler.getInstance().run();
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
    }

    /************************************************************************
     * This function is called periodically during teleoperated mode.
    ************************************************************************/
    @Override
    public void teleopPeriodic() {
        CommandScheduler.getInstance().run();
        check();
    }

    /************************************************************************
    ************************************************************************/

    private void check() {
        if (operatorJoystick==null) {
            operatorJoystick = new JoystickWrapper(Robot.oi.operatorController, 0.15);
        }
		
        if (operatorJoystick.isBackButton()) {
            Robot.overrideEncoders=true;
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
    }  

    /************************************************************************
     * This function is called periodically during test mode.
    ************************************************************************/
   @Override
    public void testPeriodic() {
        CommandScheduler.getInstance().run();
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

}
