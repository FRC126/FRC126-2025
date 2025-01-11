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

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class AutoSecondNote extends SequentialCommandGroup {
    public AutoSecondNote() {
        addCommands(
            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),

            new ParallelCommandGroup(
                // Run the Pickup
                new PickupWork(125, false),
                // Drive over the next note
                new ThrowerAngle(42.5,125),
                new DriveWork(.35,0,0,32,125)
            ),

            new DriveWork(-0.30,0,0,16,150),     
            // Throw note in speaker
            new ThrowerWork(RobotMap.throwerSpeed, 75)       
        );
    }
}
