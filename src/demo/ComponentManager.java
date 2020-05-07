package demo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Seyed Sahil
 */
public class ComponentManager {

    private static ComponentManager componentManager;

    public static ComponentManager getInstance(String basePackage, ClassLoader classLoader, boolean reload) {
        if (componentManager == null || reload) {
            componentManager = new ComponentManager(basePackage, classLoader);
        }
        return componentManager;
    }

    private String relativePath;

    private ClassLoader classLoader;

    private List<File> systemResourceCache;

    private List<Class<?>> classesCache;

    private Map<String, Class<?>> componentClassesMap;

    private Map<String, Class<?>> serviceClassesMap;

    private Map<String, Object> singletonInstanceMap;

    private ComponentManager(String basePackage, ClassLoader classLoader) {
        super();
        validate(basePackage, classLoader);
        relativePath = basePackage.replace(".", File.separator);
        this.classLoader = classLoader;
        systemResourceCache = getSystemResources();
        componentClassesMap = new LinkedHashMap<>();
        serviceClassesMap = new LinkedHashMap<>();
        singletonInstanceMap = new LinkedHashMap<>();
        classesCache = identifyClasses(basePackage);
    }

    public <T> T getInstance(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        if (clazz.isAnnotationPresent(Component.class)) {
            return newInstance(clazz, clazz.getAnnotation(Component.class).strategy());
        } else if (clazz.isAnnotationPresent(Service.class)) {
            return newInstance(clazz, clazz.getAnnotation(Service.class).strategy());
        }
        return null;
    }

    private <T> T newInstance(Class<?> clazz, Strategy strategy) {
        try {
            if (strategy.equals(Strategy.SINGLETON)) {
                return (T) singletonInstanceMap.get(clazz.getName());
            } else {
                return (T) clazz.newInstance();
            }
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException("Instance creation failed");
        }
    }

    private List<Class<?>> identifyClasses(String basePackage) {
        List<Class<?>> classResources = new LinkedList<>();
        for (File systemResource : systemResourceCache) {
            classResources.addAll(lookupForClass(systemResource, basePackage));
        }
        return classResources;
    }

    private List<Class<?>> lookupForClass(File systemResource, String basePackage) {
        List<Class<?>> classResources = new LinkedList<>();
        if (!systemResource.exists()) {
            throw new RuntimeException("Internal error, system resource cannot be located.");
        }
        File[] childResources = systemResource.listFiles();
        Arrays.stream(childResources).forEach(childResource -> {
            if (childResource.isDirectory()) {
                classResources.addAll(lookupForClass(childResource, basePackage + "." + childResource.getName()));
            } else {
                try {
                    Class<?> clazz = Class.forName(basePackage + "." + childResource.getName().substring(0, childResource.getName().indexOf('.')));
                    classResources.add(clazz);
                    registerClassResource(clazz);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException("Internal error, system resource cannot be located.");
                }
            }
        });
        return classResources;
    }

    private void registerClassResource(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Component.class)) {
            componentClassesMap.put(clazz.getName(), clazz);
            if (clazz.getAnnotation(Component.class).strategy() == Strategy.SINGLETON) {
                updateSingletonInstanceMap(clazz);
            }
            System.out.println("Registered class " + clazz.getName() + ".");
        } else if (clazz.isAnnotationPresent(Service.class)) {
            serviceClassesMap.put(clazz.getName(), clazz);
            if (clazz.getAnnotation(Service.class).strategy() == Strategy.SINGLETON) {
                updateSingletonInstanceMap(clazz);
            }
            System.out.println("Registered class " + clazz.getName() + ".");
        }
    }

    private void updateSingletonInstanceMap(Class<?> clazz) {
        try {
            Object instance = clazz.newInstance();
            singletonInstanceMap.put(clazz.getName(), instance);
            System.out.println(clazz.getName() + " instance registered as singleton.");
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Instance registration failed.");
        }
    }

    private List<File> getSystemResources() {
        List<File> systemResourceList = new LinkedList<>();
        try {
            Enumeration<URL> systemResourceEnum = classLoader.getResources(relativePath);
            while (systemResourceEnum.hasMoreElements()) {
                systemResourceList.add(new File(systemResourceEnum.nextElement().getFile()));
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get resources from specified path.");
        }
        return systemResourceList;
    }

    private void validate(String basePackage, ClassLoader classLoader) {
        if (basePackage == null || basePackage.trim().length() == 0) {
            throw new RuntimeException("Please provide a base package name to start the scan with");
        }
        if (classLoader == null) {
            throw new RuntimeException("Class loader is required from the base package itself.");
        }
    }

}