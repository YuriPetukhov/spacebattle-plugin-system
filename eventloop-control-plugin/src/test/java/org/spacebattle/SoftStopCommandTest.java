package org.spacebattle;

import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.spacebattle.commands.Command;
import org.spacebattle.execution.EventLoop;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

public class SoftStopCommandTest {

    private EventLoop eventLoop;

    @BeforeEach
    void setup() {
        eventLoop = mock(EventLoop.class);
    }

    @Test
    void testExecuteSetsNewBehaviourThatStopsIfQueueIsEmpty() {
        BlockingQueue<Command> mockQueue = mock(BlockingQueue.class);
        when(mockQueue.isEmpty()).thenReturn(true);
        when(eventLoop.getQueue()).thenReturn(mockQueue);

        Runnable oldBehaviour = mock(Runnable.class);
        when(eventLoop.getBehaviour()).thenReturn(oldBehaviour);

        SoftStopCommand command = new SoftStopCommand(eventLoop);
        command.execute();

        verify(eventLoop).setBehaviour(any());
        var behaviourCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).setBehaviour(behaviourCaptor.capture());

        Runnable newBehaviour = behaviourCaptor.getValue();
        newBehaviour.run();

        verify(eventLoop).stop();
        verify(oldBehaviour, never()).run();
    }

    @Test
    void testExecuteDelegatesToOldBehaviourIfQueueNotEmpty() {
        BlockingQueue<Command> mockQueue = mock(BlockingQueue.class);
        when(mockQueue.isEmpty()).thenReturn(false);
        when(eventLoop.getQueue()).thenReturn(mockQueue);

        Runnable oldBehaviour = mock(Runnable.class);
        when(eventLoop.getBehaviour()).thenReturn(oldBehaviour);

        SoftStopCommand command = new SoftStopCommand(eventLoop);
        command.execute();

        var behaviourCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(eventLoop).setBehaviour(behaviourCaptor.capture());

        Runnable newBehaviour = behaviourCaptor.getValue();
        newBehaviour.run();

        verify(oldBehaviour).run();
        verify(eventLoop, never()).stop();
    }
}
