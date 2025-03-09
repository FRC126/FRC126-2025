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

import frc.robot.RobotMap;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANrangeConfiguration;
import com.ctre.phoenix6.hardware.CANrange;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MeasureDistance {
    private CANrange measure; 
    private CANrange measure2; 

    public MeasureDistance() {
        measure = new CANrange(RobotMap.canRangeCanID);
        measure2 = new CANrange(RobotMap.canRangeCanID2);

        CANrangeConfiguration configs = new CANrangeConfiguration();
        measure.getConfigurator().apply(configs);  
        measure2.getConfigurator().apply(configs);  
    }

    public double getLeftDistanceInches() {
        StatusSignal<Distance> distance;
        distance=measure.getDistance();

        double dist = distance.getValueAsDouble()*3.28*12;

        SmartDashboard.putNumber("distance measure", dist);

        return dist;
    }

    public double getRightDistanceInches() {
        StatusSignal<Distance> distance;
        distance=measure2.getDistance();

        double dist = distance.getValueAsDouble()*3.28*12;

        SmartDashboard.putNumber("distance measure2", dist);

        return dist;
    }
}



