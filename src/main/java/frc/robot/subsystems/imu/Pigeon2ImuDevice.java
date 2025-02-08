package frc.robot.subsystems.imu;

import com.ctre.phoenix6.hardware.Pigeon2;

import frc.robot.RobotMap;
import frc.robot.subsystems.ImuDevice;

public class Pigeon2ImuDevice implements ImuDevice {
    private Pigeon2 pidgey = new Pigeon2(RobotMap.pigeonCanID, "rio");    

    @Override
    public void zeroYaw() {
        this.pidgey.reset();
    }

    @Override
    public void setAngleAdjustment(double angle) {
        this.pidgey.setYaw(angle);
    }

    @Override
    public double getYaw() {
        return this.pidgey.getYaw().getValueAsDouble();
    }
}
