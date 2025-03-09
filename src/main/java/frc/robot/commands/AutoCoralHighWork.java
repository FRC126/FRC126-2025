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

            new DriveWork(.15,0,10,70),

            new LimeLightWork(Robot.heightTargets.LFour, Robot.leftRight.Left,400),

            new ElevatorWork(Robot.heightTargets.LFour, Robot.shootAction.Shoot, 100),

            new CoralShooterWork(-.25, 20),

            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),

            new DriveWork(-.15,0,10,30),

            new ParallelCommandGroup(
                new DriveWork(-.15,0,10,30),
                new ElevatorWork(Robot.heightTargets.LOne, Robot.shootAction.NoShoot,100 )
            )
        );        
    }
}