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
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Robot;

public class AutoJustDrive extends SequentialCommandGroup {
    public AutoJustDrive() {

        addCommands(         
            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),
            new InstantCommand(Robot.swerveDrive::brakesOn, Robot.swerveDrive),

            new InstantCommand(Robot.swerveDrive::resetYaw, Robot.swerveDrive),

            new DriveWork(-.25,0,24,120),



            new FinishAuto()
        );        
    }
}