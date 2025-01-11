package frc.robot.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Used to get averages across multiple iterations.
 */
public class Smoother {
    int iters;
    Queue<Double> values = new LinkedList<>();

    public Smoother(int iters) {
        this.iters = iters;    
    }
    public double sampleAndGetAverage(double num) {
        values.offer(num);
        if (values.size() > this.iters) {
            values.remove();
        }
        return getAverage();
    }
    public double getAverage() {
        double sum = values.stream().mapToDouble(Double::doubleValue).sum();
        return sum / values.size();
    }
}
