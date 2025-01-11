/**********************************
	   _      ___      ____
	 /' \   /'___`\   /'___\
	/\_, \ /\_\ \\ \ /\ \__/
	\/_/\ \\/_/// /__\ \  _``\
	   \ \ \  // /_\ \\ \ \L\ \
	    \ \_\/\______/ \ \____/
		 \/_/\/______/   \/____/

    Team 126 2024 Code       
	    Go get em gaels!

***********************************/


package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.JoystickWrapper;
import frc.robot.Robot;

/**********************************************************************************
 **********************************************************************************/

 public class LEDSubsystem extends SubsystemBase {
    AddressableLED m_led;
    AddressableLEDBuffer m_ledBuffer;
    final int LENGTH=150;
    int m_rainbowFirstPixelHue,
        rotateLED=0,
        delay=0;
    JoystickWrapper driveJoystick;

    public static enum LEDModes{None,BrakeMode,DriveMode,SlowMode,AimingRed,AimingBlue,
                                ShootingSpeaker,ShootingAmp,Climbing,AutoDrive,
                                AutoTurn,RunPickup, Rainbow, GaelForce, HaveNote, HaveNoteHigh};
    LEDModes LEDMode = LEDModes.GaelForce;

    /**********************************************************************************
     **********************************************************************************/

    public LEDSubsystem() {
        m_led = new AddressableLED(9);

        // LED's go into PWM #9
        m_ledBuffer = new AddressableLEDBuffer(LENGTH);

        m_led.setLength(m_ledBuffer.getLength());
    
        // Set the data
        m_led.setData(m_ledBuffer);
        m_led.start();

		driveJoystick = new JoystickWrapper(Robot.oi.driveController, 0.15);

    }  

    /**********************************************************************************
     **********************************************************************************/

    public void forceMode(LEDModes mode) {
            LEDMode=mode;
    }

    public void setMode(LEDModes mode) {

        if (LEDMode == LEDModes.None) {
            LEDMode=mode;
        } else if (LEDMode == LEDModes.BrakeMode ||
                   LEDMode == LEDModes.DriveMode || 
                   LEDMode == LEDModes.GaelForce || 
                   LEDMode == LEDModes.SlowMode ) {
            // Allow other modes to override the drive mode
            LEDMode=mode;
        }    
    }

    /**********************************************************************************
     **********************************************************************************/

    public LEDModes getMode() {
        return(LEDMode);
    }

    /**********************************************************************************
     **********************************************************************************/

    public void doLights() {
        if (--delay < 0) { delay = 0; }

        switch (LEDMode) {
            case None:              { setColorSliding(0,0,0); break; }
            case BrakeMode:         { setColorSliding(64,0,0,125,0,0); break; }
            case DriveMode:         {  
                                        double foo = Math.abs(driveJoystick.getLeftStickY()) + Math.abs(driveJoystick.getLeftStickX());
                                        if (foo>1) foo=1;
                                        double rot = Math.abs(driveJoystick.getRightStickX());            
                                        setColorSliding(0,(int)(100*foo),(int)(100*rot),
                                                        0,(int)(200*foo),(int)(200*rot));
                                        break; 
                                    }
            case SlowMode:          { setColorSliding(64,64,0,125,125,0); break; }
            case AimingRed:         { setColorSliding(0,125,0,125,0,0); break; }
            case AimingBlue:        { setColorSliding(0,125,0,0,0,125); break; }
            case ShootingSpeaker:   { setColorSliding(64,0,64,0,0,125); break; }
            case ShootingAmp:       { setColorSliding(64,0,64,125,0,0); break; }
            case Climbing:          { rainbow(); break; } //setColorSliding(64,64,64,64,0,0); break; }
            case AutoDrive:         { setColorSliding(125,75,100,0,0,125); break; }
            case AutoTurn:          { setColorSliding(125,75,100,125,0,0); break; }
            case RunPickup:         { setColorSliding(0,64,0,100,100,100); break; }
            case HaveNote:          { setColorSliding(237,145,15,120,70,15); break; }
            case HaveNoteHigh:      { setColorSliding(0,150,150,50,0,50); break; }
            case Rainbow:           { rainbow(); break; }
            case GaelForce:         { setColorSliding(0, 64, 0, 128, 90, 0); break; }
        }
    }

    /**********************************************************************************
     **********************************************************************************/

     @Override
    public void periodic() {
    }

    /**********************************************************************************
     **********************************************************************************/
    
    public void setColorSliding(int red, int green, int blue) {
        setColorSliding(red, green, blue, red, green, blue);
    }

    /**********************************************************************************
     **********************************************************************************/
    
    public void setColorSliding(int red, int green, int blue, 
                                int redSlide, int greenSlide, int blueSlide) {
        if (delay > 0) { return; }

        for (int i = 0; i < LENGTH; i++) {
            m_ledBuffer.setRGB(i, red, green, blue);
        }

        colorSliders(rotateLED, redSlide, greenSlide, blueSlide);
        updateLights();
    }    

    /**********************************************************************************
     **********************************************************************************/

    public void colorSliders(int start, int red, int green, int blue) {
        colorSlider(rotateLED, red, green, blue);
        colorSlider((rotateLED+(LENGTH/4))%LENGTH, red, green, blue);
        colorSlider((rotateLED+(LENGTH/2))%LENGTH, red, green, blue);
        colorSlider((rotateLED+(LENGTH/2)+(LENGTH/4))%LENGTH, red, green, blue);
    }

    /**********************************************************************************
     * 1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19 
     * .9  .8  .7  .6  .5  .4  .3  .2  .1  0  .1  .2  .3  .4  .5  .6  .7  .8  .9
     **********************************************************************************/

    public void colorSlider(int start, int red, int green, int blue) {
        double step=1.0;
        for ( int i=start; i <= start+9; i++, step-=.09) {
            m_ledBuffer.setRGB(((start+i)%LENGTH), (int)(red*step), (int)(green*step), (int)(blue*step));
        }
        for ( int i=start+10; i <= start+18; i++, step+=.09) {
            m_ledBuffer.setRGB(((start+i)%LENGTH), (int)(red*step), (int)(green*step), (int)(blue*step));
        }
    }

    /**********************************************************************************
     **********************************************************************************/

    public void rainbow() {
        for (var i = 0; i < m_ledBuffer.getLength(); i++) {
            final var hue = (m_rainbowFirstPixelHue + (i * 180 / m_ledBuffer.getLength())) % 180;
            m_ledBuffer.setHSV(i, hue, 255, 128);
        }
        m_rainbowFirstPixelHue += 3;
        m_rainbowFirstPixelHue %= 180;
        m_led.setData(m_ledBuffer);
    }

    /**********************************************************************************
     **********************************************************************************/

    public void updateLights() {
        rotateLED = (rotateLED + 1) % LENGTH;
        delay=2;

        m_led.setData(m_ledBuffer);
    }
}