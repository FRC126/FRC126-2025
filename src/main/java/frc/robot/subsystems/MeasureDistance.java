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

public class MeasureDistance {
    private CANrange measure; 

    public MeasureDistance() {
        measure = new CANrange(RobotMap.canRangeCanID);
        CANrangeConfiguration configs = new CANrangeConfiguration();
        measure.getConfigurator().apply(configs);  
    }

    public double getDistance() {
        StatusSignal<Distance> distance;
        distance=measure.getDistance();
        return distance.getValueAsDouble();
    }
}



