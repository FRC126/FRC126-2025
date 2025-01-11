package frc.robot.subsystems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import edu.wpi.first.wpilibj.Counter;

public class LidarLiteTest {
    @Test
    public void testMeasureFarAway() {
        Counter counter = mock(Counter.class);
        when(counter.get()).thenReturn(1);
        when(counter.getPeriod()).thenReturn(0.05);

        LidarLite lidarLite = new LidarLite(counter);

        // simulate 1 tick
        lidarLite.periodic();
        assertEquals(1.718472593435943, lidarLite.getTargetAngleDegrees());
    }

    @Test
    public void testMeasureClose() {
        Counter counter = mock(Counter.class);
        when(counter.get()).thenReturn(1);
        when(counter.getPeriod()).thenReturn(0.0005);

        LidarLite lidarLite = new LidarLite(counter);

        // simulate 1 tick
        lidarLite.periodic();
        assertEquals(73.28602486675283, lidarLite.getTargetAngleDegrees());
    }
}
