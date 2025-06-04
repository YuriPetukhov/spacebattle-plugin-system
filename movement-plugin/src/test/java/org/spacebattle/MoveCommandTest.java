package org.spacebattle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.spacebattle.behavior.MovingObject;
import org.spacebattle.entity.Point;
import org.spacebattle.entity.Vector;

import static org.mockito.Mockito.*;

class MoveCommandTest {

    private MovingObject mockObject;

    @BeforeEach
    void setUp() {
        mockObject = Mockito.mock(MovingObject.class);
    }

    @Test
    void testExecuteMovesObjectCorrectly() {
        Point initialLocation = new Point(10, 20);
        Vector velocity = new Vector(3, 5);
        Point expectedLocation = new Point(13, 25);

        when(mockObject.getLocation()).thenReturn(initialLocation);
        when(mockObject.getVelocity()).thenReturn(velocity);

        MoveCommand moveCommand = new MoveCommand(mockObject);
        moveCommand.execute();

        verify(mockObject).setLocation(expectedLocation);
    }

}
