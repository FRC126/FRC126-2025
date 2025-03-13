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

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;

public class AutoCoralHigh extends SequentialCommandGroup {
    public AutoCoralHigh() {

        addCommands(         
            new InstantCommand(Robot.swerveDrive::brakesOn, Robot.swerveDrive),

            new DriveWork(.2,0,6,70),

			new AutoCoralHighWork(),

            new DriveWork(-.15,0,10,200),
 		
			new FinishAuto()
        );        
    }
}