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

public class Log {
	static String[] mutedSystems = new String[]{}; // MUTED SYSTEMS
	public void initDefaultCommand() {
	}
	public static void print(int category, String header, String content) {
		/*
		* (0) INFO - General logging, should not be spammed, eg. [INFO]
		* (1) WARN - Messages that should be looked at after a match (eg. Gyro inaccuracies, low voltage, etc.)
		* (2) CRIT - For problems that could affect robot operation (eg. high amperage, sensor failures, etc.)
		* (3) EMER - For problems that could damage the robot (eg. mechanical limit overruns, range extremes, etc.)
		*/
		String catheader;
		if(category == 0) {
			catheader = "  [INFO] ";
		} else if(category == 1) {
			catheader = " >[WARN] ";
		} else {
			catheader = ">>[CRIT] ";
		}
		System.out.println(catheader + header + ": " + content);
	}

}
