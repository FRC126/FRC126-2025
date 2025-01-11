package frc.robot.util;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Subclass of Smoother used for Network table data (like from the Limelight)
 */
public class NetworkTablesSmoother extends Smoother {
    private String field;
    NetworkTable table;

    public NetworkTablesSmoother(int iters, String networkTable, String field) {
        super(iters);
        this.table = NetworkTableInstance.getDefault().getTable(networkTable);
        this.field = field;
    }

    public double sampleAndGetAverage() {
        return super.sampleAndGetAverage(table.getEntry(field).getDouble(0.0));
    }
}