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
            new LimeLightWork(Robot.heightTargets.LFour, Robot.leftRight.Left,400),

            new ElevatorWork(Robot.heightTargets.LFour, Robot.shootAction.Shoot, 100),

            new CoralShooterWork(40),

            new DriveWork(-.1,0,10,30),

            new ElevatorWork(Robot.heightTargets.LOne, Robot.shootAction.Shoot, 100)
            );        
    }
}