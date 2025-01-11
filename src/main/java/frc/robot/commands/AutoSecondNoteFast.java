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

public class AutoSecondNoteFast extends SequentialCommandGroup {
    public AutoSecondNoteFast() {
        addCommands(
            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),

            new ParallelCommandGroup(
                // Run the Pickup
                new PickupWorkFast(125),
                // Drive over the next note
                new ThrowerAngle(42.5,100),
                new DriveWorkFast(.35,0,32,125)
            ),

            new DriveWorkFast(-0.30,0,16,150),     
            // Throw note in speaker
            new ThrowerWorkFast(RobotMap.throwerSpeed, 75)       
        );
    }
}
