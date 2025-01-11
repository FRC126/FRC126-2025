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

import edu.wpi.first.wpilibj.Joystick;

public class JoystickWrapper {
    final Joystick joystick;
    final double driftOffset;
    public JoystickWrapper(Joystick joystick, double driftOffset) {
        this.joystick = joystick;
        this.driftOffset = driftOffset;
    }

    ////////////////////////////////////////////////////
    // JoyStick Interfaces
    ////////////////////////////////////////////////////

    public double getLeftStickY() {
        return getRawAxis(RobotMap.lStickY) * -1;
    }
    public double getLeftStickX() {
        return getRawAxis(RobotMap.lStickX);
    }
    public double getRightStickY() {
        return getRawAxis(RobotMap.rStickY) * -1;
    }
    public double getRightStickX() {
        return getRawAxis(RobotMap.rStickX);
    }
    public boolean isLStickPressButton() {
        return getRawButton(RobotMap.xboxLStick);
    }
    public boolean isRStickPressButton() {
        return getRawButton(RobotMap.xboxRStick);
    }
    private double getRawAxis(int axis) {
        double value = joystick.getRawAxis(axis);
        if(Math.abs(value) < driftOffset) { // Prevent control drifting (driver controller)
		    value = 0;
        }
        return value;
    }

    ////////////////////////////////////////////////////
    // Trigger Interfaces
    ////////////////////////////////////////////////////

    public boolean leftTriggerPressed() {
        if (getRawAxis(RobotMap.Ltrigger) > .15) {
            return(true);
        }
        return(false);    
    }
    public double getLeftTrigger() {
        return getRawAxis(RobotMap.Ltrigger);
    }

    public boolean rightTriggerPressed() {
        if (getRawAxis(RobotMap.Rtrigger) > .15) {
            return(true);
        }
        return(false);    
    }
    public double getRightTrigger() {
        return getRawAxis(RobotMap.Rtrigger);
    }
    public double getTriggers() {
        double tr = getRightTrigger();
        double tl = getLeftTrigger();
        double trigs;
		if(tr > 0) {
			trigs = tr;
		} else {
			trigs = tl * -1;
        }
        return trigs;
    }
    
    ////////////////////////////////////////////////////
    // Button Interfaces
    ////////////////////////////////////////////////////

    public boolean isAButton() {
        return getRawButton(RobotMap.xboxA);
    }
    public boolean isBButton() {
        return getRawButton(RobotMap.xboxB);
    }
    public boolean isXButton() {
        return getRawButton(RobotMap.xboxX);
    }
    public boolean isYButton() {
        return getRawButton(RobotMap.xboxY);
    }
    public boolean isBackButton() {
        return getRawButton(RobotMap.xboxBack);
    }
    public boolean isStartButton() {
        return getRawButton(RobotMap.xboxStart);
    }
    public boolean isLShoulderButton() {
        return getRawButton(RobotMap.xboxLTrig);
    }
    public boolean isRShoulderButton() {
        return getRawButton(RobotMap.xboxRTrig);
    }

    private boolean getRawButton(int button) {
        return joystick.getRawButton(button);
    }

    ////////////////////////////////////////////////////
    // POV Interfaces
    ////////////////////////////////////////////////////

    public int getPov() {
        return joystick.getPOV();
    }   
    public boolean getPovUp() {
        int tmp=joystick.getPOV();
        if (tmp == 0) {
            return true;
        }
        return false;
    }   
    public boolean getPovDown() {
        int tmp=joystick.getPOV();
        if (tmp == 180) {
            return true;
        }
        return false;
    }   
    public boolean getPovRight() {
        int tmp=joystick.getPOV();
        if (tmp == 90) {
            return true;
        }
        return false;
    }   
    public boolean getPovLeft() {
        int tmp=joystick.getPOV();
        if (tmp == 270) {
            return true;
        }
        return false;
    }   
}

/**************************************************************************************
 *  Current Joystick Mappings:
 * 
 * Drivers Joystick
 *     Drive Base Controls
 *         Left Joystick Y-Axis: Robot drive forward and back
 *         Right Joystick X-Axis: Robot drive left and right
 *         Left Shoulder Button - Shift Down
 *         Right Shoulder Button - Shift Up
 *         X Button - Turn around 180 degrees
 * 
 *     Climber Controls
 *         A Button - Raise Climber Arms - only moves while held
 *         B Button - Lower Climber Arms - only moves while held
 *         Start Button - Reset Encoders
 *         POV Left - Lower Left Climber
 *         POV Right - Lower Right Climber
 * 
 *     Lime Light Controls
 *         POV Up - Turn to lock onto target and then auto throw
 *                  Button must be held the whole time
 * 
 * Operators Joystick
 *     Intake Controls
 *         Left Shoulder Button - Extend Ball Intake
 *         Right Shoulder Button - Retract Ball Inake
 *         Right Joystick Y-Axis - Run Intake Forwards or Backwards
 *         Left Trigger - While held, extend intake and run intake, when 
 *                        released stop intake and retract intake
 *         Right Trigger - While held, extend intake and run intake backwards, when 
 *                        released stop intake and retract intake
 * 
 *     Thrower Control
 *         Spins up thrower and automatically throws ball while button is held
 *             POV Down - Hold to throw short distance
 *             POV Left - Hold to throw middle short distance
 *             POV Up - Hold to throw middle long distance
 *             POV Right - Hold to throw long distance
 *         A Button - Increase Intake Speed 500RPM
 *         B Button - Decrease Intake sPeed 500RPM
 *         Y Button - Set Thrower Speed to 0
 *         X Button - Throw the Ball if using A and B buttons
 * 
 */