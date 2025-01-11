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

import frc.robot.RobotMap;
import frc.robot.commands.DistanceMeasure;
import frc.robot.util.Smoother;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**********************************************************************************
 **********************************************************************************/

public class LidarLite extends SubsystemBase {
    /*
    * Adjust the Calibration Offset to compensate for differences in each unit.
    * You can also use the offset to zero out the distance between the sensor and edge of the robot.
    */
    private static final int CALIBRATION_OFFSET = -5;

    /**
     * Distance of target from the floor.
     */
    private static final int HEIGHT_IN = 83;

    /**
     * Height of lidar light from the floor.
     */
    private static final int SUBHEIGHT_IN = 24;
    private static final double CM_PER_INCH = 0.39370079;
    private final Counter counter;
    private Smoother smoother = new Smoother(10);

	/**********************************************************************************
     * Create an object for a LIDAR-Lite attached to some digital input on the roboRIO
     * 
     * @param source The DigitalInput or DigitalSource where the LIDAR-Lite is attached (ex: new DigitalInput(9))
	 **********************************************************************************/
    public LidarLite() {
        this(new Counter(new DigitalInput(RobotMap.LidarChannel)));
    }

	/**********************************************************************************
     * Override for unit test
     */
    public LidarLite(Counter counter) {
        super();
        this.counter = counter;
        setDefaultCommand(new DistanceMeasure(this));
        counter.setMaxPeriod(1.0);
        
        // Configure for measuring rising to falling pulses
        counter.setSemiPeriodMode(true);
        counter.reset();
    }

	/**********************************************************************************
     * Gets the average distance (cm) over several ticks.
	 **********************************************************************************/
    public double getDistanceAvg() {
        return smoother.getAverage();
    } 

	/**********************************************************************************
     * Get the target angle based on distance from the speaker.
	 **********************************************************************************/
    public double getTargetAngleDegrees() {
        double distanceInch = getDistanceAvg() * CM_PER_INCH;
        double throwerAngleDegrees = calcAngle(distanceInch, HEIGHT_IN,SUBHEIGHT_IN);
        SmartDashboard.putNumber("Target Angle (degrees)", throwerAngleDegrees);
        SmartDashboard.putNumber("LidarLite Distance (in)", distanceInch);
        return throwerAngleDegrees;
    }

    /**********************************************************************************
	 **********************************************************************************/

    @Override
    public void periodic() {
        measureDistance();  
    }

	/**********************************************************************************
     * Take a measurement and return the distance in cm
     * 
     * @return Distance in cm
	 **********************************************************************************/
    public double measureDistance() {
        /* If we haven't seen the first rising to falling pulse, then we have no measurement.
        * This happens when there is no LIDAR-Lite plugged in, btw.
        */
        if (counter.get() < 1) {
            return 0;
        }
        /* getPeriod returns time in seconds. The hardware resolution is microseconds.
        * The LIDAR-Lite unit sends a high signal for 10 microseconds per cm of distance.
        */
        double cm = (counter.getPeriod() * 1000000.0 / 10.0) + CALIBRATION_OFFSET;
        return smoother.sampleAndGetAverage(cm);
    }

	/**********************************************************************************
     * Use math to calculate the angle we should shoot from.
	 **********************************************************************************/
    public double calcAngle(double x, double heightInches, double subHeightInchdes) {    
        return Math.toDegrees(Math.atan((heightInches - subHeightInchdes)/(x)));
    }
}
