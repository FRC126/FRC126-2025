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

public class AutoSideShootBackup extends SequentialCommandGroup {
    public AutoSideShootBackup(Robot.targetTypes targetType) {
        Robot.targetType=targetType;
        int direction = Robot.getDirection(targetType);

        addCommands(
            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),          
            new InstantCommand(Robot.swerveDrive::brakesOn, Robot.swerveDrive),

            new AutoFirstNote(),
           
            new DriveWork(.3,0,0,12,100),
            
            new TurnDegreesWork(-40 * direction,46),
                        
            new InstantCommand(Robot.swerveDrive::resetYaw, Robot.swerveDrive),
            
            new ParallelCommandGroup(
                new DriveWork(.4,0,0,48,200),
                new PickupWork(200, false),
                new ThrowerAngle(34.0,150)
            ),    

            new TurnDegreesWork(30 * direction, 28),
            
            new ThrowerWork(RobotMap.throwerSpeed, 75),    

            new TurnDegreesWork(-35 * direction,37),

            new InstantCommand(Robot.swerveDrive::resetYaw, Robot.swerveDrive),

            new DriveWork(.4,0,0,72,200),

            new FinishAuto()
        );
    }
}        
