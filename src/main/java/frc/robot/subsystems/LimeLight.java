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
import frc.robot.Robot.targetTypes;
import frc.robot.commands.*;
import frc.robot.util.Smoother;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
//import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class LimeLight extends SubsystemBase {

    private boolean llTargetValid;
    private double llTargetArea;
    private double llTargetX;
    private double llTargetY;
    private int validCount;
    private int missedCount;
    private int centered;
 
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

     private double getTolerance(double distance) {
        if (distance > 48) { return(4); }
        if (distance > 36) { return(6);}    
        if (distance > 24) { return(8);}   
        if (distance < 24 && distance > 15) { return(10);} 
        return(5);
     }

    /************************************************************************
	 ************************************************************************/

     public boolean seekTarget(Robot.leftRight direction) {   

        double tolerance = 5;
        double moveSpeed = 0.06;
        double targetDistance = 14.5;
        double cameraOffset = 0;
        double dirOffset = 1;
        double rotateSpeed= 0.03;
        double rotateThres = 1;

        double ldist=Robot.distance.getLeftDistanceInches();
        double rdist=Robot.distance.getRightDistanceInches();
        if (ldist < 5) { ldist=rdist; }
        if (rdist < 5) { rdist=ldist; }
        double diff = ldist-rdist;
        double dist;
        if (diff < -15 || diff > -15) {
            dist=ldist<rdist?ldist:rdist;
        } else {
            dist=(ldist+rdist)/2.0;
        }

        tolerance = getTolerance(dist);
        cameraOffset=getTolerance(dist)*2.20;

        double leftRight=0, forwardBack=0, rotate=0;

        if (!llTargetValid ||
            validCount <= 3) {

            if (dist > targetDistance) {
                forwardBack=0.1;
        
                if ((diff > rotateThres || diff < rotateThres * -1) && diff < 10) {
                    forwardBack=0;
                    leftRight=0;    
                    if (diff > 0) {
                        rotate = rotateSpeed;
                    } else {
                        rotate = rotateSpeed * -1;
                    }
                }                
                Robot.swerveDrive.brakesOn();
                Robot.swerveDrive.setAutoMove(true);
                Robot.swerveDrive.Drive(forwardBack, leftRight, rotate, false, 0);
                return(false);
            }
            if (dist > 10 && dist<targetDistance+1) {
                return(true);
            }

            centered=0;
            Robot.swerveDrive.setAutoMove(false);
            Robot.elevator.setAutoMove(false);
            Robot.coralShooter.setAutoMove(false);
            return(false);
        }     

        if (direction == Robot.leftRight.Left) {
            dirOffset=-1;
        }

        // We found a valid vision target.
        double llTargetXOffset = llTargetX - (cameraOffset * dirOffset);

        if ( llTargetXOffset < (tolerance * -1) ) {
                if (llTargetXOffset > 10 || llTargetXOffset < -10) {
                    leftRight=moveSpeed * 2 * -1;
                } else {
                    leftRight=moveSpeed * -1;
                }
        } else if ( llTargetXOffset > tolerance ) {
            if (llTargetXOffset > 10 || llTargetXOffset < -10) {
                leftRight=moveSpeed * 2;
            } else {
                leftRight=moveSpeed;
            }
        } else {
            if (dist > targetDistance) {
                if (dist > 24) {
                    forwardBack=0.25;
                } else {
                   forwardBack=0.15;
                }  
            }
        }

        ////////////////////////////////////////////////////////////////
        // figure out the rotate vs left right?
        //
     
        if ((diff > rotateThres || diff < rotateThres * -1) && diff < 10) {
            forwardBack=0;
            leftRight=0;    
            if (diff > 0) {
                rotate = rotateSpeed;
                if (diff>3) { rotate = rotateSpeed * 2; }
            } else {
                rotate = rotateSpeed * -1;
                if (diff<-3) { rotate = rotateSpeed * -2; }
            }
        }

        if (leftRight != 0 || forwardBack != 0 || rotate != 0) {
            Robot.swerveDrive.brakesOn();
            Robot.swerveDrive.setAutoMove(true);
            Robot.swerveDrive.Drive(forwardBack, leftRight, rotate, false, 0);
            centered=0;
        } else {
            Robot.swerveDrive.brakesOn();
            Robot.swerveDrive.setAutoMove(false);
            Robot.swerveDrive.cancel();            
            centered++;
        }
        
        if (centered > 4) {
             return(true);
        }
        
        return(false);
    }
    
}