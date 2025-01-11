package frc.robot.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SmootherTest {
    @Test
    public void testSimple() {
        Smoother smoother = new Smoother(10);
        assertEquals(1,smoother.sampleAndGetAverage(1));
    }

    @Test
    public void testAverage() {
        Smoother smoother = new Smoother(10);
        smoother.sampleAndGetAverage(1);
        smoother.sampleAndGetAverage(2);
        smoother.sampleAndGetAverage(3);
        assertEquals(2,smoother.getAverage());
    }

    @Test
    public void testAverageWithLots() {
        Smoother smoother = new Smoother(10);

        int count = 5;
        for (int i=0; i<count; i++) {
            smoother.sampleAndGetAverage(1);
        }
        for (int i=0; i<count; i++) {
            smoother.sampleAndGetAverage(2);
        }
        for (int i=0; i<count; i++) {
            smoother.sampleAndGetAverage(3);
        }

        assertEquals(2.5,smoother.getAverage());
    }
}
