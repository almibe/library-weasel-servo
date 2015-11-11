/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.libraryweasel.servo;

import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Provides a single method that is used to register Component classes with
 * Apache Felix Dependency Manager convention-based runtime annotations.
 * <p>
 * Annotation processing works as followed.
 * <p>
 * When a class is marked with @Component it is registered under every interface
 * or class passed to the annotation via a class array argument.
 * <p>
 * For each field marked with @Service it is treated as a required service.
 * <p>
 * To track multiple instances of a service use the @Callback annotation on the
 * class you are writing.  The argument to this annotation is the class of the
 * service you are tracking.  By convention you also need to a two methods called
 * addX and removeX where X is the class name and it accepts an instance of X
 * as a parameter.
 */
public class LibraryWeaselComponentRegistrar {
    private final DependencyManager dm;

    public LibraryWeaselComponentRegistrar(DependencyManager dm) {
        this.dm = dm;
    }

    public void register(Object component) {
        Class<?> clazz;
        Object object;
        if (component instanceof Class<?>) {
            clazz = (Class<?>)component;
            object = null;
        } else {
            clazz = component.getClass();
            object = component;
        }

        if (checkIfComponent(clazz)) {
            makeDMCall(clazz, object);
        } else {
            throw new RuntimeException(component + " is not a component.");
        }
    }

    private void makeDMCall(Class<?> component, Object implementation) {
        List<Class<?>> interfaces = getInterfaces(component);
        List<Class<?>> services = getServices(component);
        Set<Class<?>> callbacks = getCallbacks(component);

        org.apache.felix.dm.Component dmComponent = dm.createComponent();
        if (implementation == null) {
            dmComponent.setImplementation(component);
        } else {
            dmComponent.setImplementation(implementation);
        }
        dmComponent.setInterface(getInterfaceNames(interfaces), null);

        services.forEach(service -> {
            ServiceDependency serviceDependency = dm.createServiceDependency();
            serviceDependency.setService(service);
            serviceDependency.setRequired(true);
            dmComponent.add(serviceDependency);
        });

        callbacks.forEach(callback -> {
            ServiceDependency serviceDependency = dm.createServiceDependency();
            serviceDependency.setService(callback);
            serviceDependency.setCallbacks("add" + callback.getSimpleName(), "remove" + callback.getSimpleName());
            serviceDependency.setRequired(false);
            dmComponent.add(serviceDependency);
        });

        dm.add(dmComponent);
    }

    private String[] getInterfaceNames(List<Class<?>> interfaces) {
        List<String> names = new ArrayList<>();
        interfaces.forEach(service -> names.add(service.getName()));
        return names.toArray(new String[names.size()]);
    }

    private List<Class<?>> getInterfaces(Class<?> component) {
        Component componentAnnotation = component.getAnnotation(Component.class);
        if (componentAnnotation == null) {
            return new ArrayList<>();
        }
        Class<?>[] interfaces = componentAnnotation.value();
        if (interfaces.length == 0) {
            return Arrays.asList(component);
        } else {
            return Arrays.asList(interfaces);
        }
    }

    private List<Class<?>> getServices(Class<?> component) {
        List<Class<?>> services = new ArrayList<>();
        for (Field field : component.getDeclaredFields()) {
            if (field.isAnnotationPresent(Service.class)) {
                services.add(field.getType());
            }
        }
        return services;
    }

    private Set<Class<?>> getCallbacks(Class<?> component) {
        Set<Class<?>> callbacks = new HashSet<>();
        if (component.isAnnotationPresent(Callback.class)) {
            callbacks.add(component.getAnnotation(Callback.class).value());
        }
        if (component.isAnnotationPresent(Callbacks.class)) {
            Callbacks callbacksAnnotation = component.getAnnotation(Callbacks.class);
            for (Callback callback : callbacksAnnotation.value()) {
                callbacks.add(callback.value());
            }
        }
        return callbacks;
    }

    private boolean checkIfComponent(Class<?> component) {
        return component.isAnnotationPresent(Component.class);
    }
}
