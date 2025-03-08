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

public class AutoCoralHighWork extends SequentialCommandGroup {
    public AutoCoralHighWork() {

        addCommands(         
            new InstantCommand(Robot.swerveDrive::brakesOn, Robot.swerveDrive),

            new LimeLightWork(Robot.heightTargets.LFour, 400),

            new ElevatorWork(Robot.heightTargets.LFour, Robot.shootAction.Shoot, 200),

            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),

            new ParallelCommandGroup(
                new DriveWork(-.15,0,10,40),
                new ElevatorWork(Robot.heightTargets.LOne, Robot.shootAction.NoShoot, 200)
            )
        );        
    }
}