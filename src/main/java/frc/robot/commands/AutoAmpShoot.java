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
import frc.robot.Robot;

public class AutoAmpShoot extends SequentialCommandGroup {
    public AutoAmpShoot(int allianceColor) {
        // Move thrower arm to specific position
        // Eject the note

        if (allianceColor == Robot.redAlliance) {
            addCommands(
                new DriveWork(.25, -.125, 0, 18, 200) 
            );
        } else {
            addCommands(
                new DriveWork(.25, .125, 0, 18, 200) 
            );
        }    

        addCommands(
            new ThrowerAngle(RobotMap.ampAngle, 250),
            new ThrowerWork(RobotMap.ampSpeed, 250),
            new ThrowerAngle(30, 250)
        );

        if (allianceColor == Robot.redAlliance) {
            addCommands(
                new DriveWork(-.25, 0, 0, 36, 200) 
            );
        } else {
            addCommands(
                new DriveWork(.25, 0, 0, 36, 200) 
            );
        }    
        
        addCommands(
            new FinishAuto()
        );
    }
}
