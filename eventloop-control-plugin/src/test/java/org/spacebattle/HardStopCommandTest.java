package org.spacebattle;

import org.junit.jupiter.api.Test;
import org.spacebattle.execution.EventLoop;

import static org.mockito.Mockito.*;

public class HardStopCommandTest {

    @Test
    void testExecuteCallsEventLoopStop() {
        EventLoop mockLoop = mock(EventLoop.class);
        HardStopCommand command = new HardStopCommand(mockLoop);

        command.execute();

        verify(mockLoop, times(1)).stop();
    }
}
