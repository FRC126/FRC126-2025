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

/**********************************************************************************
 **********************************************************************************/

public class AutoTest extends SequentialCommandGroup {
    public AutoTest() {
        /**********************************************************************************
         **********************************************************************************/
        
        addCommands(
            new InstantCommand(Robot.swerveDrive::resetEncoders, Robot.swerveDrive),
          
            // TODO Aim at speaker
            new TargetAimWork(Robot.targetTypes.TargetRed, 250, false),
            
            // Throw the Note
            new ThrowerWork(3000, 150),
            
            new ParallelCommandGroup(
                // Run the Pickup
                new PickupWork(200, false),
                // Drive over the next note
                new DriveWork(0.25,0,0,12,200)
            ),
        
            //new TurnDegreesWork(-20,250),

            // TODO Aim at speaker
            new TargetAimWork(Robot.targetTypes.TargetRed, 250,false),
            // Throw note in speaker
            new ThrowerWork(3000, 150),

            new ParallelCommandGroup(
                // Run the Pickup
                new PickupWork(200, false),
                // Drive over the next note
                new DriveWork(0.25,0,0,12,200)
            ),
        
            //new TurnDegreesWork(-20,250),

            // TODO Aim at speaker
            new TargetAimWork(Robot.targetTypes.TargetRed, 250, false),

            // Throw note in speaker
            new ThrowerWork(3000, 150),

            /*
            new TurnDegreesWork(-70,250),

            new ParallelCommandGroup(
                // Run the Pickup
                new PickupWork(250),
                // Drive over the next note
                new DriveWork(-0.3,0,0,24,250)
            ),

            new TurnDegreesWork(60,250),

            // TODO Aim at speaker

            // Throw note in speaker
            new ThrowerWork(3000, 45, 250),
            */

            new FinishAuto()
        );
    }
}
