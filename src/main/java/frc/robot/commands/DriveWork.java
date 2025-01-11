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

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;

public class DriveWork extends Command {
    double driveFb;
    double driveLr;
    double startAngle;
    double rotate;
    double distance;
    int iters;
    static int distanceReached=0;
    boolean driveWorkDebug=true;

	/**********************************************************************************
	 **********************************************************************************/
	
    public DriveWork(double fb, double lr, double r, double dis, int itersIn) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        driveFb = fb;
        driveLr = lr;
        rotate = r;
        distance = dis;
        iters = itersIn;
        distanceReached=0;
        Robot.swerveDrive.resetEncoders();
        startAngle = Robot.swerveDrive.getYaw();
        Robot.swerveDrive.brakesOn();
    }

	/**********************************************************************************
     * Called just before this Command runs the first time
	 **********************************************************************************/

    @Override
    public void initialize() {
        startAngle = Robot.swerveDrive.getYaw();
        Robot.swerveDrive.brakesOn();
        Robot.swerveDrive.resetEncoders();
        distanceReached=0;

    }    

	/**********************************************************************************
     * Called repeatedly when this Command is scheduled to run
	 **********************************************************************************/
	
	@Override
    public void execute() {
        double FB = driveFb,
               LR = driveLr;
        iters--;

        double tmp = Robot.swerveDrive.getDistanceInches();

        if (tmp + 6 > distance) {
            // Slow down as we get close to the distance
            FB=driveFb*.5;
            LR=driveLr*.5;
        } 
        if (tmp + 4 > distance) {
            // Slow down as we get close to the distance
            FB=driveFb*.25;
            LR=driveLr*.25;
        } 
        if (tmp + .5 > distance) {
            // Within one inch of the target
            Robot.swerveDrive.brakesOn();
            FB=0;
            LR=0;
            distanceReached++;
        }

        if (FB > 0 && FB < 0.08) { FB=.08; }
        if (FB < 0 && FB > -0.08) { FB=-.08; }
        if (LR > 0 && LR < 0.08) { LR=.08; }
        if (LR < 0 && LR > -0.08) { LR=-.08; }


        if (driveWorkDebug) { 
            SmartDashboard.putNumber("Distance Inches", tmp);
            SmartDashboard.putNumber("Distance", distance);
            SmartDashboard.putNumber("Distance Reached", distanceReached);
        }    

        Robot.swerveDrive.Drive(FB, LR, 0, true, startAngle);            
     }

	/**********************************************************************************
     * Make this return true when this Command no longer needs to run execute()
	 **********************************************************************************/
	
    // Make this return true when this Command no longer needs to run execute()
	@Override
    public boolean isFinished() {       
        if (iters == 0 || distanceReached > 1 || !Robot.checkAutoCommand()) {
            Robot.swerveDrive.Drive(0, 0, 0);
            return true;
        }
        return false;
    }

	/**********************************************************************************
     * Called once after isFinished returns true
	 **********************************************************************************/
	
	@Override
    public void end(boolean isInteruppted) {
        Robot.swerveDrive.Drive(0, 0, 0);
    }
}
