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

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotMap;

public class AutoAmp extends SequentialCommandGroup {
    public AutoAmp() {
        // Move thrower arm to specific position
        // Eject the note
        addCommands(
            new PickupWork(50,true),
            new ThrowerAngle(RobotMap.ampAngle, 200),
            new ThrowerWork(RobotMap.ampSpeed, 200),
            new ThrowerAngle(60, 200),
            new FinishAuto()
        );
    }
}
