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

public class AutoCoralHighRight extends SequentialCommandGroup {
    public AutoCoralHighRight() {

        addCommands(         
            new InstantCommand(Robot.swerveDrive::brakesOn, Robot.swerveDrive),

            new DriveWork(.2,0,30,200),

            new TurnDegreesAbsolute(-45, 200),

            new AutoCoralHighWork(),

            new DriveWork(-.2,0,18,100),

            new TurnDegreesAbsolute(30, 200),

            new DriveWork(-.15,0,36,120),

            new AutoPickupWork(),

            new DriveWork(.15,0,10,120),

            new AutoCoralHighWork(),

			new FinishAuto()
        );        
    }
}