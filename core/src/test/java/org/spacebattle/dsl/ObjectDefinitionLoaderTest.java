package org.spacebattle.dsl;

import org.junit.jupiter.api.Test;
import org.spacebattle.entity.Angle;
import org.spacebattle.entity.Point;
import org.spacebattle.entity.Vector;
import org.spacebattle.ioc.IoC;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.repository.ObjectRepository;
import org.spacebattle.uobject.IUObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ObjectDefinitionLoaderTest {

    Map<String, Object> registry = new HashMap<>();

    @Test
    void testLoadFromDslSourceRegistersNormalizedObject() throws Exception {
        Map<String, Object> registry = new HashMap<>();

        IoC ioc = new IoCContainer() {
            @Override
            public Object resolve(String key, Object... args) {
                if ("IoC.Register".equals(key)) {
                    String regKey = (String) args[0];
                    @SuppressWarnings("unchecked")
                    Function<Object[], Object> supplier = (Function<Object[], Object>) args[1];
                    registry.put(regKey, supplier.apply(null));
                } else if (key.equals("dsl:normalize:location")) {
                    return (Function<Object[], Object>) arr -> {
                        Map<String, Object> map = (Map<String, Object>) arr[0];
                        return new Point((Integer) map.get("x"), (Integer) map.get("y"));
                    };
                } else if (key.equals("dsl:normalize:velocity")) {
                    return (Function<Object[], Object>) arr -> {
                        Map<String, Object> map = (Map<String, Object>) arr[0];
                        return new Vector((Integer) map.get("dx"), (Integer) map.get("dy"));
                    };
                } else if (key.equals("dsl:normalize:angle")) {
                    return (Function<Object[], Object>) arr -> {
                        int deg = (Integer) arr[0];
                        return new Angle(deg, 360);
                    };
                } else if (key.equals("object-repository")) {
                    return new ObjectRepository() {
                        final Map<String, IUObject> store = new HashMap<>();

                        @Override
                        public void save(String id, IUObject object) {
                            store.put(id, object);
                        }

                        @Override
                        public Optional<IUObject> findById(String id) {
                            return Optional.ofNullable(store.get(id));
                        }

                        @Override
                        public boolean exists(String id) {
                            return store.containsKey(id);
                        }

                        @Override
                        public void delete(String id) {
                            store.remove(id);
                        }
                    };

                }

                return null;
            }
        };

        String yaml = """
        objects:
          ship-1:
            properties:
              location: { x: 3, y: 4 }
              velocity: { dx: 1, dy: 2 }
              angle: 120
    """;

        URL url = new URL("data", null, -1, "/test.yaml", new URLStreamHandler() {
            @Override protected URLConnection openConnection(URL u) {
                return new URLConnection(u) {
                    @Override public void connect() {}
                    @Override public InputStream getInputStream() {
                        return new ByteArrayInputStream(yaml.getBytes());
                    }
                };
            }
        });

        DslSource source = () -> url.openStream();

        ObjectDefinitionLoader loader = new ObjectDefinitionLoader(ioc);
        loader.load(source);

        assertTrue(registry.containsKey("object:ship-1"));
        IUObject obj = (IUObject) registry.get("object:ship-1");

        assertInstanceOf(Point.class, obj.getProperty("location").orElse(null));
        assertInstanceOf(Vector.class, obj.getProperty("velocity").orElse(null));
        assertInstanceOf(Angle.class, obj.getProperty("angle").orElse(null));
    }

}
