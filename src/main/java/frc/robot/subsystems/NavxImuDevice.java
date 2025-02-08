package frc.robot.subsystems;

import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;

import edu.wpi.first.wpilibj.DriverStation;

public class NavxImuDevice implements ImuDevice {
    private AHRS navxMXP;
    public NavxImuDevice() {
        try {
            this.navxMXP = new AHRS(NavXComType.kMXP_SPI);
        } catch (RuntimeException ex) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
    }

    @Override
    public void zeroYaw() {
        this.navxMXP.zeroYaw();
    }

    @Override
    public void setAngleAdjustment(double angle) {
        this.navxMXP.setAngleAdjustment(angle);
    }

    @Override
    public double getYaw() {
        return this.navxMXP.getYaw();
    }
}
