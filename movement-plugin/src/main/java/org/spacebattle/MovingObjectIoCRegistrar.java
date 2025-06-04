package org.spacebattle;

import org.spacebattle.entity.Point;
import org.spacebattle.entity.Vector;
import org.spacebattle.ioc.IoC;
import org.spacebattle.uobject.IUObject;

import java.util.function.Function;

public class MovingObjectIoCRegistrar {
    public static void register(IoC ioc) {
        ioc.resolve("IoC.Register", "MovingObject:getLocation",
                (Function<Object[], Object>) args -> {
                    IUObject obj = (IUObject) args[0];
                    Point location = (Point) obj.getProperty("location")
                            .orElseThrow(() -> new IllegalStateException("Location not set"));
                    System.out.println("[IoC] getLocation: " + location);
                    return location;
                });

        ioc.resolve("IoC.Register", "MovingObject:getVelocity",
                (Function<Object[], Object>) args -> {
                    IUObject obj = (IUObject) args[0];
                    Vector velocity = (Vector)obj.getProperty("velocity")
                            .orElseThrow(() -> new IllegalStateException("Velocity not set"));
                    System.out.println("[IoC] getVelocity: " + velocity);
                    return velocity;
                });

        ioc.resolve("IoC.Register", "MovingObject:setLocation",
                (Function<Object[], Object>) args -> {
                    IUObject obj = (IUObject) args[0];
                    Point location = (Point) args[1];
                    obj.setProperty("location", location);
                    System.out.println("[IoC] setLocation: " + location);
                    return null;
                });
    }
}
