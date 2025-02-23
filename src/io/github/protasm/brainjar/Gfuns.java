package io.github.protasm.brainjar;

import java.lang.reflect.Method;
import java.util.*;

import io.github.protasm.lpc2j.compiler.GfunsIntfc;

public class Gfuns implements GfunsIntfc {
    private final Map<String, List<Method>> gfunMap = new HashMap<>();

    @Override
    public List<Method> getMethods(String name) {
        return gfunMap.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public void register(String name, Object instance, Method method) {
        gfunMap.computeIfAbsent(name, _ -> new ArrayList<>()).add(method);
    }

    @Override
    public void register(Object instance, Method method) {
        register(method.getName(), instance, method);
    }

    // Example global functions
    public void write(String message) {
        System.out.println("write: " + message);
    }

    public Object clone_object(String path) {
        return new Object();  // Placeholder
    }
}
