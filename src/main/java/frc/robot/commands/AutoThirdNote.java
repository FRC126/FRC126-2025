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

public class AutoThirdNote extends SequentialCommandGroup {
    public AutoThirdNote(Robot.targetTypes targetType) {

        int direction = Robot.getDirection(targetType);

        addCommands(         
            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),
            new InstantCommand(Robot.swerveDrive::brakesOn, Robot.swerveDrive),

            new TurnDegreesWorkFixed(110 * direction, 100),

            new InstantCommand(Robot.swerveDrive::resetYaw, Robot.swerveDrive),

            new ParallelCommandGroup(
                new DriveWorkFast(.4,0, 30,125),     
                // Run the Pickup
                new PickupWorkFast(125)
            ),    

            new ParallelCommandGroup(
                new TurnDegreesWorkFixed(-72 * direction, 100),
                new ThrowerAngle(36,150)
            ),


            new ThrowerWorkFast(RobotMap.throwerSpeed+500, 75)

            //new TurnDegreesWorkFixed(120 * direction, 150)

        );        
    }
}
