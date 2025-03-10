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
    private boolean active=false;

    private String limelightName;
 
    public static SequentialCommandGroup throwCommand;
    boolean limeLightDebug=false;
    double pipelineLast=0;

    static int itersToCapture = 5;

    private Smoother taSmoother = new Smoother(itersToCapture);
    private Smoother txSmoother = new Smoother(itersToCapture);
    private Smoother tySmoother = new Smoother(itersToCapture);

	/************************************************************************
	 ************************************************************************/

    public LimeLight(String limelightName) {
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
        this.limelightName = limelightName; 
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

        LimelightHelpers.setPipelineIndex(limelightName, pipeline);
        LimelightHelpers.setCameraMode_Processor(limelightName);
        LimelightHelpers.setLEDMode_PipelineControl(limelightName);

        if (limeLightDebug) {
            SmartDashboard.putNumber("Limelight Pipe", LimelightHelpers.getCurrentPipelineIndex(limelightName));
        }

        double tx = txSmoother.sampleAndGetAverage(LimelightHelpers.getTX(limelightName));
        double ty = tySmoother.sampleAndGetAverage(LimelightHelpers.getTY(limelightName));
        double ta = taSmoother.sampleAndGetAverage(LimelightHelpers.getTA(limelightName));        
        boolean tv = LimelightHelpers.getTV(limelightName);
        
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

    public boolean getActive () {
        return active;
    }

    /************************************************************************
	 ************************************************************************/

    public void setActive (boolean active) {
        this.active = active;
    }

    /************************************************************************
	 ************************************************************************/

     private double getTolerance(double distance) {
        if (distance > 48) { return(4); }
        if (distance > 42) { return(5); }
        if (distance > 36) { return(6);}    
        if (distance > 30) { return(7);}    
        if (distance > 24) { return(8);}   
        if (distance > 20) { return(9);}   
        if (distance <= 20 && distance >= 13) { return(10);} 
        return(5);
     }

    /************************************************************************
	 ************************************************************************/

    private double fixRotateSpeed ( double diff, double rotateSpeed) {
        double rotate;

        if (diff > 0) {
            rotate = rotateSpeed;
            if (diff > 5) { rotate = rotateSpeed * 2; }
        } else {
            rotate = rotateSpeed * -1;
            if (diff < -5) { rotate = rotateSpeed * -2; }
        }

        return rotate;
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
        double dist, diff;
    
        double ldist=Robot.distance.getLeftDistanceInches();
        double rdist=Robot.distance.getRightDistanceInches();

        if (ldist < 5) { ldist=rdist; }
        if (rdist < 5) { rdist=ldist; }
        diff = ldist-rdist;
        if (diff < -15 || diff > 15) {
            dist=ldist<rdist?ldist:rdist;
        } else {
            dist=(ldist+rdist)/2.0;
        }

        tolerance = getTolerance(dist);
        cameraOffset=getTolerance(dist)*2.20;

        double leftRight=0, forwardBack=0, rotate=0;

        if (!llTargetValid ||
            validCount <= 3) {

            // if we don't have a valid target, but can measure distance, just move forward
            if (dist > targetDistance) {
                forwardBack=0.1;
        
                if ((diff > rotateThres || diff < rotateThres * -1) && diff < 10) {
                    forwardBack=0;
                    leftRight=0;    
                    rotate = fixRotateSpeed(diff, rotateSpeed);
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
        } else if (direction == Robot.leftRight.Right) {
            dirOffset=1;
        } else {
            dirOffset=0;
        }

        // We found a valid vision target.
        double llTargetXOffset = llTargetX - (cameraOffset * dirOffset);

         ////////////////////////////////////////////////////////////////

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
            } else {
                if (dist < 5 ) {
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
            rotate = fixRotateSpeed(diff, rotateSpeed);
        }

        if (direction == Robot.leftRight.Center) {
            // If we are backing up, then we need to reverse the direction
            forwardBack*=-1;
            leftRight*=-1;
            rotate*=-1;
        }    

        ////////////////////////////////////////////////////////////////

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