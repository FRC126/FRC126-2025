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
import frc.robot.Robot.targetTypes;
import frc.robot.commands.*;
import frc.robot.util.Smoother;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
//import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class LimeLight extends SubsystemBase {

    private boolean activeSeek=false;
    private boolean llTargetValid;
    private double llTargetArea;
    private double llTargetX;
    private double llTargetY;
    private int validCount;
    private int missedCount;
    private int centered;
    private int aimed;    

    public static SequentialCommandGroup throwCommand;
    boolean limeLightDebug=false;
    double pipelineLast=0;

    static int itersToCapture = 5;

    private Smoother taSmoother = new Smoother(itersToCapture);
    private Smoother txSmoother = new Smoother(itersToCapture);
    private Smoother tySmoother = new Smoother(itersToCapture);

	/************************************************************************
	 ************************************************************************/

    public LimeLight() {
        // Register this subsystem with command scheduler and set the default command
        super();
        setDefaultCommand(new LimeLightControl(this));

        llTargetValid=false;
        llTargetArea = 0.0;
        llTargetX = 0.0;
        llTargetY = 0.0;
        validCount=0;
        missedCount=0;
        centered=0;
    }

    /************************************************************************
	 ************************************************************************/

     public void setActiveSeek(boolean seek) {
        activeSeek = seek;
    }   

   	/************************************************************************
	 ************************************************************************/

    public void setllTargetData(boolean isValid,
                                double targetArea,
                                double targetX,
                                double targetY) {
        llTargetValid = isValid;
        llTargetArea = targetArea;
        llTargetX = targetX;
        llTargetY = targetY;
    }    

	/************************************************************************
	 ************************************************************************/

     public void getCameraData() {
        if (Robot.targetType == targetTypes.NoTarget) {
            return;
        }
        
        int pipeline=Robot.targetType.getPipeline();

        if (pipeline != pipelineLast) {
            validCount=0;
            setllTargetData(false, 0, 0, 0);
        }
        pipelineLast=pipeline;

        LimelightHelpers.setPipelineIndex(null, pipeline);
        LimelightHelpers.setCameraMode_Processor(null);
        LimelightHelpers.setLEDMode_PipelineControl(null);

        if (limeLightDebug) {
            SmartDashboard.putNumber("Limelight Pipe", LimelightHelpers.getCurrentPipelineIndex(null));
        }

        double tx = txSmoother.sampleAndGetAverage(LimelightHelpers.getTX(null));
        double ty = tySmoother.sampleAndGetAverage(LimelightHelpers.getTY(null));
        double ta = taSmoother.sampleAndGetAverage(LimelightHelpers.getTA(null));        
        boolean tv = LimelightHelpers.getTV(null);
        
        if (limeLightDebug) {
            //post to smart dashboard periodically
            SmartDashboard.putNumber("LimelightX", tx);
            SmartDashboard.putNumber("LimelightY", ty);
            SmartDashboard.putNumber("LimelightArea", ta);
            SmartDashboard.putBoolean("LimelightValid", tv);
        }    

        if (tv) {
            setllTargetData(true, ta, tx, ty);
        } else {
            setllTargetData(false, 0, 0, 0);
        }        

   }

   	/************************************************************************
	 ************************************************************************/

    public void trackTarget() {
        if (Robot.targetType == Robot.targetTypes.NoTarget) {
            // If we are not seeking a target, then reset all target 
            // data and return
            setllTargetData(false, 0, 0, 0);
		 	return;
        }
        
        Robot.limeLight.getCameraData();

        if (llTargetValid){
            // We found a valid vision target.
            // Keep track of the number of time we seen a valid target
            validCount++;
            missedCount=0;
        } else {
            if ( missedCount < 10 ) {
                // Don't change the old data, so we won't stop on dropping a frame or 10
                missedCount++;
            } else {
                // Initialize all target data
                Robot.limeLight.setllTargetData(false, 0, 0, 0);
                validCount=0;
                missedCount=0;
            }    
        }                    
    }

    /************************************************************************
	 ************************************************************************/

     public boolean seekTarget() {   
        int cameraOffset = 8;    

        if (!activeSeek ||
            !llTargetValid ||
            validCount <= 3) {
            centered=0;
            Robot.swerveDrive.setAutoMove(false);
            return(false);
        }     

        // We found a valid vision target.
        double llTargetXOffset = llTargetX - cameraOffset;

        if ( llTargetXOffset < -1.5 || llTargetXOffset > 1.5) {
            Robot.swerveDrive.brakesOn();
            double driveRotate = Robot.swerveDrive.rotateToDegrees(llTargetXOffset);
            if (driveRotate!=0) {
                Robot.swerveDrive.setAutoMove(true);
            } else {
                Robot.swerveDrive.setAutoMove(false);
            }    
            centered=0;
        } else {
            Robot.swerveDrive.cancel();
            centered++;
        }   

        if (centered > 2) {
            Robot.swerveDrive.setAutoMove(false);
            return(true);
        } else {
            return(false);
        }
    }          
}