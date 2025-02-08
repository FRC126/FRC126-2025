package frc.robot.subsystems;

public interface ImuDevice {
    void zeroYaw();
    void setAngleAdjustment(double angle);
    double getYaw();
}
