package io.github.protasm.brainjar;

import io.github.protasm.lpc2j.LPC2J;
import io.github.protasm.lpc2j.SourceFile;

public class Brainjar {
    public static void main(String[] args) throws Exception {
        LPC2J compiler = new LPC2J();

        // Paths to LPC source files
        String roomPath = "/Users/jonathan/git/brainjar/src/io/github/protasm/brainjar/lpc/room.lpc";
        String swordPath = "/Users/jonathan/git/brainjar/src/io/github/protasm/brainjar/lpc/sword.lpc";
        
    	SourceFile roomSource = new SourceFile(roomPath);
    	SourceFile swordSource = new SourceFile(swordPath);

        // Compile LPC files
        byte[] roomBytes = compiler.compile(roomSource);
        byte[] swordBytes = compiler.compile(swordSource);

        // Use custom ClassLoader to load classes
        Class<?> roomClass = new InMemoryClassLoader().defineClass("room", roomBytes);
        Class<?> swordClass = new InMemoryClassLoader().defineClass("sword", swordBytes);

        // Create instances and call methods
        Object roomObj = roomClass.getDeclaredConstructor().newInstance();
        Object swordObj = swordClass.getDeclaredConstructor().newInstance();

        // Call foo on lpcObject
        int result = (int) roomClass
        		.getMethod("foo", Object.class, int.class)
        		.invoke(roomObj, swordObj, 77);

        System.out.println("Result: " + result);
    }

    // Custom ClassLoader for loading bytecode
    static class InMemoryClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] bytecode) {
            return super.defineClass(name, bytecode, 0, bytecode.length);
        }
    }
}
