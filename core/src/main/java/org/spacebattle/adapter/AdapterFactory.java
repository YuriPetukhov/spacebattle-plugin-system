package org.spacebattle.adapter;

import org.spacebattle.ioc.IoC;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Универсальная фабрика адаптеров, создающая прокси-объекты для интерфейсов,
 * делегирующих вызовы в IoC-контейнер.
 * <p>
 * Пример: если есть интерфейс Moveable с методом move(),
 * то вызов adapter.move() приведёт к вызову IoC.resolve("Moveable:move", obj, ...).
 */
public class AdapterFactory {
    private final IoC container;

    /**
     * Конструктор, принимающий IoC-контейнер (обычно ScopedContainer).
     *
     * @param container IoC-контейнер, используемый для разрешения вызовов
     */
    public AdapterFactory(IoC container) {
        this.container = container;
    }

    /**
     * Создаёт прокси-объект интерфейса, делегирующий вызовы в IoC.
     *
     * @param iface интерфейс, для которого создаётся адаптер
     * @param obj   объект, передаваемый как context в IoC
     * @param <T>   тип интерфейса
     * @return прокси-реализация интерфейса
     */
    @SuppressWarnings("unchecked")
    public <T> T createAdapter(Class<T> iface, Object obj) {
        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class<?>[]{iface},
                new IoCInvocationHandler(iface, obj)
        );
    }

    /**
     * InvocationHandler, реализующий делегацию вызовов через IoC.
     */
    private class IoCInvocationHandler implements InvocationHandler {
        private final Class<?> iface;
        private final Object obj;

        public IoCInvocationHandler(Class<?> iface, Object obj) {
            this.iface = iface;
            this.obj = obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            String key = iface.getSimpleName() + ":" + methodName;

            if (method.getReturnType() == Void.TYPE) {
                container.resolve(key, obj, args != null ? args[0] : null);
                return null;
            }

            return container.resolve(key, obj, args != null ? args : new Object[0]);
        }
    }
}
