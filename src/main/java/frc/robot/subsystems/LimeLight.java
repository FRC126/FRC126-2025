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

        if (limelightName == null) {
            setDefaultCommand(new LimeLightControl(this));
        } else {
            setDefaultCommand(new LimeLightRearControl(this));
        }

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
            //setllTargetData(false, 0, 0, 0);
		 	//return;
        }
        
        if (limelightName == null) {
            Robot.limeLight.getCameraData();
        } else {
            Robot.limeLightRear.getCameraData();
        }    
        boolean straight=false;
        double ldist=Robot.distance.getLeftDistanceInches();
        double rdist=Robot.distance.getRightDistanceInches();

        if (ldist-rdist < 2 && ldist-rdist> -2) {
            straight=true;
        }
        SmartDashboard.putBoolean("linedup", straight);              

        if (llTargetValid){
            // We found a valid vision target.
            // Keep track of the number of time we seen a valid target
            validCount++;
            missedCount=0;
            SmartDashboard.putNumber("llTargetArea", llTargetArea);              

        } else {
            if ( missedCount < 10 ) {
                // Don't change the old data, so we won't stop on dropping a frame or 10
                missedCount++;
            } else {
                // Initialize all target data
                if (limelightName == null) {
                    Robot.limeLight.setllTargetData(false, 0, 0, 0);
                } else {
                    Robot.limeLightRear.setllTargetData(false, 0, 0, 0);
                }
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
        double ret = (160.0/distance);
        SmartDashboard.putNumber("getToleranceNew", ret);              

        if (ret > 4) { ret=4;}
        if (ret < 2) { ret=2;}
        return ret;
    }

    /************************************************************************
	 ************************************************************************/

    private double getLeftRightSpeed(double distance, double offset) {
        double ret = 0.05;

        if (distance < 30) {
            if (offset > 20) { 
                ret=.20;
            } else if (offset > 15) { 
                ret=.15;
            } else if (offset > 10) { 
                ret=.10;
            } else if (offset > 5) { 
                ret=.075;
            }
        } else {
            if (offset > 12) { 
                ret=.20;
            } else if (offset > 9) { 
                ret=.15;
            } else if (offset > 6) { 
                ret=.10;
            } else if (offset > 4) { 
                ret=.075;
            }
        }

        return ret;
    }

    /************************************************************************
	 ************************************************************************/

     private double getMoveSpeed(double distance) {
        if (distance > 65) { return .3; }
        if (distance > 55) { return .25; }
        if (distance > 45) { return .20; }
        if (distance > 35) { return .175; }
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
            dist=ldist<rdist?rdist:ldist;
        } else {
            dist=(ldist+rdist)/2.0;
        }
        SmartDashboard.putNumber("limenewdist", dist);              

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
                    if (strafeCount < 3) {
                        Robot.swerveDrive.resetEncoders();
                    } else {
                        double driveDistance = Robot.swerveDrive.getDistanceInches();
                        SmartDashboard.putNumber("limenewdriveDistance", driveDistance);              
                        if (driveDistance > 3.20) {
                           stopMove();
                           strafeCount=1000;
                        }    
                        switch (direction) {
                        case Left:
                            doMove(0, 0.1, 0);
                            break;
                        case Right:
                            doMove(0, -0.1, 0);
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

        tolerance = getToleranceNew(dist);
        leftRightSpeed = getLeftRightSpeed(dist, Math.abs( Math.abs(llTargetX)-tolerance));
        moveSpeed = getMoveSpeed(dist); 

        ////////////////////////////////////////////////////////////////
        // We found a valid vision target.
         if ( llTargetX < (tolerance * -1) ) {
            leftRight=leftRightSpeed * -1;
        } else if ( llTargetX > tolerance ) {
            leftRight=leftRightSpeed;
        } 
        if (dist > targetDistance) {
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
        double targetDistance = 2;
   
        double leftRight=0, forwardBack=0;

        SmartDashboard.putBoolean("limerear-llTArgetValid", llTargetValid);
        SmartDashboard.putNumber("limerear-validCount", validCount);

        if (!llTargetValid ||
            validCount <= 3) {
            stopMove();
            Robot.swerveDrive.setAutoMove(false);
            return(false);
        }  

        double dist = 60/llTargetArea;

        SmartDashboard.putNumber("limerear-llTargetArea", llTargetArea);
        SmartDashboard.putNumber("limerear-dist", dist);

        if (dist < targetDistance) { 
            stopMove();
            Robot.swerveDrive.setAutoMove(false);
            return(true);
        }    


        tolerance = getToleranceNew(dist);
        leftRightSpeed = getLeftRightSpeed(dist, Math.abs( Math.abs(llTargetX)-tolerance));
        moveSpeed = getMoveSpeed(dist); 

        ////////////////////////////////////////////////////////////////
        // We found a valid vision target.
         if ( llTargetX < (tolerance * -1) ) {
            leftRight=leftRightSpeed * -1;
        } else if ( llTargetX > tolerance ) {
            leftRight=leftRightSpeed;
        } 
        
        if (dist > targetDistance) {
            forwardBack=moveSpeed;
        } 

        // We are backing up, then we need to reverse the direction
        forwardBack*=-1;
        leftRight*=-1;

        SmartDashboard.putNumber("limerear-leftRight", leftRight);
        SmartDashboard.putNumber("limerear-forwardBack", forwardBack);

        ////////////////////////////////////////////////////////////////

        doMove(forwardBack, leftRight, 0);
        
        return(false);
    }
     
}