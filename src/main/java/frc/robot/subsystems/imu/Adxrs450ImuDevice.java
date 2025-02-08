package frc.robot.subsystems.imu;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.subsystems.ImuDevice;

public class Adxrs450ImuDevice implements ImuDevice {
    private ADXRS450_Gyro gyro;

    public Adxrs450ImuDevice() {
        try {
            gyro = new ADXRS450_Gyro();
        } catch(Exception e) {
            DriverStation.reportError("Error instantiating internal IMU:  " + e.getMessage(), true);
        }
    }

    @Override
    public void zeroYaw() {
        this.gyro.reset();
    }

    @Override
    public void setAngleAdjustment(double angle) {
        // Not supported
    }

    @Override
    public double getYaw() {
        return this.gyro.getAngle();
    }

}
