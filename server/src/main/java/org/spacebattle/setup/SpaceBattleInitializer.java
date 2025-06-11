package org.spacebattle.setup;

import org.spacebattle.commands.Command;
import org.spacebattle.dsl.DslLoader;
import org.spacebattle.dsl.ObjectDefinitionLoader;
import org.spacebattle.entity.Angle;
import org.spacebattle.entity.Point;
import org.spacebattle.entity.Vector;
import org.spacebattle.exceptions.DefaultExceptionHandler;
import org.spacebattle.exceptions.ExceptionHandler;
import org.spacebattle.execution.EventLoop;
import org.spacebattle.factory.DefaultObjectFactory;
import org.spacebattle.ioc.IoCContainer;
import org.spacebattle.middleware.MiddlewareRegistry;
import org.spacebattle.plugin.*;
import org.spacebattle.plugins.PluginComponent;
import org.spacebattle.plugins.components.*;
import org.spacebattle.repository.InMemoryObjectRepository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Утилита инициализации сервера SpaceBattle.
 * Выполняет настройку IoC, регистрацию обработчиков, загрузку плагинов и запуск слежения.
 */
public class SpaceBattleInitializer {

    public static void initialize(IoCContainer ioc) throws Exception {
        PluginLoader defaultLoader = createDefaultPluginLoader(ioc);
        initialize(ioc, defaultLoader);
    }

    public static Thread initialize(IoCContainer ioc, PluginLoader loader) throws Exception {
        System.out.println("SpaceBattle Plugin System Starting...");

        // Регистрация IoC.Register
        ioc.register("IoC.Register", params -> {
            String regKey = (String) params[0];
            @SuppressWarnings("unchecked")
            Function<Object[], Object> supplier = (Function<Object[], Object>) params[1];
            ioc.register(regKey, supplier);
            System.out.println("IoC.Register called: key = " + regKey);
            return null;
        });

        // ExceptionHandler
        ioc.register("exception-handler", args -> new DefaultExceptionHandler(args));

        // EventLoop
        ioc.register("event-loop", args -> new EventLoop(
                4,
                (ExceptionHandler) ioc.resolve("exception-handler")
        ));
        ((EventLoop) ioc.resolve("event-loop")).start(); // Запуск

        // IoC сам в себе
        ioc.register("ioc", args -> ioc);

        // Объектный репозиторий и фабрика
        InMemoryObjectRepository repository = new InMemoryObjectRepository();
        ioc.register("object-repository", objects -> repository);
        ioc.register("object-factory", objects -> new DefaultObjectFactory(ioc, repository));

        // Регистрация DSL загрузчика для объектов
        ioc.register("dsl-object-loader", args -> (DslLoader<Void>) source -> {
            new ObjectDefinitionLoader(ioc).load(source);
            return null;
        });

        // IoC-настройки
        IoCEventLoopSetup.setup(ioc);
        IoCRepositorySetup.setup(ioc);
        IoCExceptionSetup.setup(ioc);

        // Middleware и диспетчер
        MiddlewareRegistry middleware = new MiddlewareRegistry();
        ioc.register("command-dispatcher", disp -> (Function<Command, Void>) command -> {
            try {
                middleware.wrap(command).execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });

        // Нормализаторы свойств из YAML
        ioc.resolve("IoC.Register", "dsl:normalize:location", (Function<Object[], Object>) args -> (Function<Object[], Object>) normArgs -> {
            Map<String, Object> map = (Map<String, Object>) normArgs[0];
            return new Point((int) map.get("x"), (int) map.get("y"));
        });

        ioc.resolve("IoC.Register", "dsl:normalize:velocity", (Function<Object[], Object>) args -> (Function<Object[], Object>) normArgs -> {
            Map<String, Object> map = (Map<String, Object>) normArgs[0];
            return new Vector((int) map.get("dx"), (int) map.get("dy"));
        });

        ioc.resolve("IoC.Register", "dsl:normalize:angle", (Function<Object[], Object>) args ->
                (Function<Object[], Object>) normArgs -> {
                    Map<String, Object> map = (Map<String, Object>) normArgs[0];
                    int angle = (int) map.getOrDefault("angle", 0);
                    int max = (int) map.getOrDefault("max", 360);
                    return new Angle(angle, max);
                });

        // Загрузка всех плагинов
        loader.loadAllFrom(new File("plugins"));

        // Запуск наблюдателя за плагинами
        Thread pluginWatchThread = new Thread(new PluginWatcher(new File("plugins"), loader));
        pluginWatchThread.setDaemon(true);
        pluginWatchThread.setName("PluginWatcher");
        pluginWatchThread.start();

        return pluginWatchThread;
    }

    private static PluginLoader createDefaultPluginLoader(IoCContainer ioc) {

        MiddlewareRegistry middleware = new MiddlewareRegistry();
        List<PluginComponent> components = List.of(
                new MiddlewareComponent(middleware),
                new CommandComponent(ioc),
                new ExceptionHandlerComponent(ioc),
                new BootstrapComponent(ioc)
        );

        PluginScanner scanner = new DefaultPluginScanner();
        ClassLoaderFactory classLoaderFactory = new CachingClassLoaderFactory();
        PluginProcessor processor = new DefaultPluginProcessor(components, classLoaderFactory);

        return new DefaultPluginLoader(scanner, processor, components);
    }
}
