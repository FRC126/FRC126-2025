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
// import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;
//import frc.robot.RobotMap;

public class AutoShootSpeakerFarSideAndBackup extends SequentialCommandGroup {
    public AutoShootSpeakerFarSideAndBackup(Robot.targetTypes targetType) {
        // Move thrower arm to specific position
        // Eject the note
        Robot.targetType=targetType;
        int direction = Robot.getDirection(targetType);

        addCommands(
            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),
            new InstantCommand(Robot.swerveDrive::brakesOn, Robot.swerveDrive),

            new AutoFirstNote(),           
            
            new DriveWork(0.30,0,0,65,250),  
            
            new TurnDegreesWorkFixed(47 * direction,150),
            
            new InstantCommand(Robot.swerveDrive::resetYaw, Robot.swerveDrive),

            new ParallelCommandGroup(
                new DriveWork(.4,0,0,95,250),
                new PickupWork(250,false)                   
            ),
            
            new FinishAuto()
        );
    }
}
