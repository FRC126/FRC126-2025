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

public class AutoShootThreeSpeaker extends SequentialCommandGroup {
    public AutoShootThreeSpeaker(Robot.targetTypes targetType) {
        // Move thrower arm to specific position
        // Eject the note
        Robot.targetType=targetType;
        int direction = Robot.getDirection(targetType);

        addCommands(
            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),
            new InstantCommand(Robot.swerveDrive::brakesOn, Robot.swerveDrive),

            new AutoFirstNote(),

            new ParallelCommandGroup(
                // Run the Pickup
                new PickupWorkFast(150),
                // Drive over the next note
                new ThrowerAngle(39,80),
                new DriveWorkFast(.40,0,32,125)
            ),

            // Throw note in speaker
            new ThrowerWorkFast(RobotMap.throwerSpeed, 80),      

            new AutoThirdNote(targetType),                      
            new FinishAuto()
        );
    }
}