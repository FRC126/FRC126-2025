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
    private int strafeCount=0;

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
        double ret = (120.0 / distance);
        if (ret < 1.5) { ret=1.5;}
        if (ret > 4) { ret=4;}
        return ret;
    }

    /************************************************************************
	 ************************************************************************/

    private double getoffset(double distance) {
        double ret = 460/distance;
        if (ret > 30) { ret=30; }
        if (distance < 10) { ret=9;}
        return(ret);
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

        double tolerance = 3;
        double moveSpeed = 0.03;
        double targetDistance = 14.5;
        double cameraOffset = 0;
        double dirOffset = 1;
        double rotateSpeed= 0.03;
        double rotateThres = 2.5;
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
        cameraOffset=getoffset(dist);

        double leftRight=0, forwardBack=0, rotate=0;

        if (!llTargetValid ||
            validCount <= 3) {

            // if we don't have a valid target, but can measure distance, just move forward
            if (dist > targetDistance) {
                forwardBack=0.1;
        
                if ((diff > rotateThres || diff < rotateThres * -1) && diff < 10) {
                    //forwardBack=0;
                    //leftRight=0;    
                    //rotate = fixRotateSpeed(diff, rotateSpeed);
                }
                doMove(forwardBack, leftRight, rotate);                
                return(false);
            }
            if (dist > 10 && dist<targetDistance+1) {
                return(true);
            }

            centered=0;
            stopMove();
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
                    leftRight=moveSpeed * (dist / 20) * -1;
                    double foo = tolerance - llTargetXOffset;
                    if (foo < 2) {
                        leftRight=moveSpeed*-1;
                    }
        } else if ( llTargetXOffset > tolerance ) {
                leftRight=moveSpeed * (dist/20);
                double foo = llTargetXOffset - tolerance;
                if (foo < 2) {
                    leftRight=moveSpeed;
                }
        } else {
            if (dist > targetDistance) {
                    forwardBack=dist/150.0;
                    if (forwardBack > 0.35) {
                        forwardBack=0.35;
                    }
            } else {
                if (dist < 5 ) {
                    forwardBack=0.15;
                }
            }
        }

/*
        ////////////////////////////////////////////////////////////////
        // figure out the rotate vs left right?
        //
        if ((diff > rotateThres || diff < rotateThres * -1) && diff < 10) {
            forwardBack=0;
            leftRight=0;    
            rotate = fixRotateSpeed(diff, rotateSpeed);
        }
*/
        if (leftRight != 0) {
            forwardBack=0;
        }

        SmartDashboard.putNumber("limeleftRight", leftRight);
        SmartDashboard.putNumber("limeforwardBack", forwardBack);
        ////////////////////////////////////////////////////////////////

        if (leftRight != 0 || forwardBack != 0 || rotate != 0) {
            doMove(forwardBack, leftRight, rotate);
            centered=0;
        } else {
            stopMove();
            centered++;
        }
        
        if (centered > 4) {
             return(true);
        }
        
        return(false);
    }

    /************************************************************************
	 ************************************************************************/
    private void doMove(double forwardBack , double leftRight, double rotate) {
        if (leftRight != 0 || forwardBack != 0 || rotate != 0) {
            Robot.swerveDrive.brakesOn();
            Robot.swerveDrive.setAutoMove(true);
            Robot.swerveDrive.Drive(forwardBack, leftRight, rotate, false, 0, false);
        } else {
            stopMove();
        }    
    }

    /************************************************************************
	 ************************************************************************/

     private void stopMove() {
        Robot.swerveDrive.brakesOn();
        Robot.swerveDrive.setAutoMove(false);
        Robot.swerveDrive.cancel();            
    } 

    /************************************************************************
	 ************************************************************************/

    private double getToleranceNew(double distance) {
        double ret = (200.0/distance);
        if (ret > 8) { ret=8;}
        if (ret < 4) { ret=4;}
        return ret;
    }

    /************************************************************************
	 ************************************************************************/

    private double getLeftRightSpeed(double distance) {
        if (distance > 55) { return .2; }
        if (distance > 45) { return .1; }
        if (distance > 35) { return .075; }
        if (distance > 25) { return .05; }
        return .0375;
    }

    /************************************************************************
	 ************************************************************************/

     private double getMoveSpeed(double distance) {
        if (distance > 55) { return .35; }
        if (distance > 45) { return .25; }
        if (distance > 35) { return .20; }
        if (distance > 25) { return .15; }
        return .1;
    }

    /************************************************************************
	 ************************************************************************/

     public boolean seekTargetNew(Robot.leftRight direction) {   

        double tolerance;
        double leftRightSpeed;
        double moveSpeed;
        double targetDistance = 13.0;
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

        tolerance = getToleranceNew(dist);
        leftRightSpeed = getLeftRightSpeed(dist);
        moveSpeed = getMoveSpeed(dist); 

        double leftRight=0, forwardBack=0, rotate=0;

        if (!llTargetValid ||
            validCount <= 3 || (dist < targetDistance+1 && dist > 10)) {

            // if we don't have a valid target, but can measure distance, just move forward
            if (dist > targetDistance+2) {
                doMove(0.1, 0, 0);
                strafeCount=0;
                return(false);
            }

            // if we have reached the target we need to strafe left or right
            if (dist > 10 && dist<=targetDistance+2) {
                strafeCount++;
                if (strafeCount < 1000) { 
                    if (strafeCount < 5) {
                        Robot.swerveDrive.resetEncoders();
                    } else {
                        double driveDistance = Robot.swerveDrive.getDistanceInches();
                        SmartDashboard.putNumber("limenewdriveDistance", driveDistance);              
                        if (driveDistance > 3.5) {
                           stopMove();
                           strafeCount=1000;
                        }    
                        switch (direction) {
                        case Left:
                            doMove(0, 0.075, 0);
                            break;
                        case Right:
                            doMove(0, -0.075, 0);
                            break;
                        }    
                    }   
                } else if (strafeCount < 1010 ) {
                    doMove(.1, 0, 0);
                } else if (strafeCount == 1010) {    
                    stopMove();
                    return(true);
                }    

                return(false);
            }

            stopMove();
            Robot.swerveDrive.setAutoMove(false);
            return(false);
        }  
        
        strafeCount=0;
        SmartDashboard.putNumber("strafeCount", strafeCount);

        ////////////////////////////////////////////////////////////////
        // We found a valid vision target.
         if ( llTargetX < (tolerance * -1) ) {
            leftRight=leftRightSpeed * -1;
        } else if ( llTargetX > tolerance ) {
            leftRight=leftRightSpeed;
        } else if (dist > targetDistance) {
            forwardBack=moveSpeed;
        } 


        SmartDashboard.putNumber("limenewleftRight", leftRight);
        SmartDashboard.putNumber("limenewforwardBack", forwardBack);

        ////////////////////////////////////////////////////////////////

        doMove(forwardBack, leftRight, rotate);
        
        return(false);
    }
 
    /************************************************************************
	 ************************************************************************/

     public boolean seekTargetNoDistance() {   

        double tolerance;
        double leftRightSpeed;
        double moveSpeed;
        double targetDistance = 13.0;
   
        double leftRight=0, forwardBack=0;

        if (!llTargetValid ||
            validCount <= 3) {
            stopMove();
            Robot.swerveDrive.setAutoMove(false);
            return(false);
        }  

        double dist = llTargetArea/4;

        if (dist < 10) { 
            stopMove();
            Robot.swerveDrive.setAutoMove(false);
            return(true);
        }    

        tolerance = getToleranceNew(dist);
        leftRightSpeed = getLeftRightSpeed(dist);
        moveSpeed = getMoveSpeed(dist); 

        ////////////////////////////////////////////////////////////////
        // We found a valid vision target.
         if ( llTargetX < (tolerance * -1) ) {
            leftRight=leftRightSpeed * -1;
        } else if ( llTargetX > tolerance ) {
            leftRight=leftRightSpeed;
        } else if (dist > targetDistance) {
            forwardBack=moveSpeed;
        } 

        // We are backing up, then we need to reverse the direction
        forwardBack*=-1;
        leftRight*=-1;

        SmartDashboard.putNumber("limenewleftRight", leftRight);
        SmartDashboard.putNumber("limenewforwardBack", forwardBack);

        ////////////////////////////////////////////////////////////////

        doMove(forwardBack, leftRight, 0);
        
        return(false);
    }
     
}