package io.github.protasm.brainjar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import io.github.protasm.lpc2j.compiler.Compiler;
import io.github.protasm.lpc2j.compiler.GfunsIntfc;
import io.github.protasm.lpc2j.fs.FSBasePath;
import io.github.protasm.lpc2j.fs.FSSourceFile;
import io.github.protasm.lpc2j.parser.ParseException;
import io.github.protasm.lpc2j.parser.Parser;
import io.github.protasm.lpc2j.parser.ast.ASTObject;
import io.github.protasm.lpc2j.scanner.Scanner;
import io.github.protasm.lpc2j.scanner.Tokens;

public class Brainjar {
    private static final GfunsIntfc gfuns = new Gfuns();
    
    private final FSBasePath basePath;
    private final Map<String, Object> objects;

    public static GfunsIntfc gfuns() {
        return gfuns;
    }
    
    public Brainjar(String basePathStr) {
	basePath = new FSBasePath(basePathStr);
	objects = new HashMap<>();
    }
    
    public void loadWorld() {
	FSSourceFile sf = load("glove.lpc");

	if (sf == null)
	    return;

	objects.put(sf.dotName(), sf.lpcObject());

	System.out.println(sf.dotName() + " loaded.");
    }

    private FSSourceFile load(String vPathStr) {
	FSSourceFile sf = compile(vPathStr);

	if (sf == null)
	    return null;

	// Define the class dynamically from the bytecode
	Class<?> clazz = new ClassLoader() {
	    public Class<?> defineClass(byte[] bytecode) {
		return defineClass(null, bytecode, 0, bytecode.length);
	    }
	}.defineClass(sf.bytes());

	// Instantiate the class using reflection
	try {
	    // Assume a no-arg constructor
	    Constructor<?> constructor = clazz.getConstructor();
	    Object instance = constructor.newInstance();

	    sf.setLPCObject(instance);

	    return sf;
	} catch (NoSuchMethodException
		| InvocationTargetException
		| IllegalAccessException
		| InstantiationException e) {
	    System.out.println(e.toString());

	    return null;
	}
    }

    private FSSourceFile compile(String vPathStr) {
	FSSourceFile sf = parse(vPathStr);

	if (sf == null)
	    return null;

	try {
	    Compiler compiler = new Compiler("java/lang/Object", gfuns);
	    byte[] bytes = compiler.compile(sf.astObject());

	    sf.setBytes(bytes);

	    boolean success = basePath.write(sf);

	    if (!success)
		throw new IllegalArgumentException();

	    return sf;
	} catch (IllegalArgumentException e) {
	    System.out.println("Error compiling file: " + vPathStr);

	    return null;
	}
    }

    private FSSourceFile parse(String vPathStr) {
	FSSourceFile sf = scan(vPathStr);

	if (sf == null)
	    return null;

	try {
	    Parser parser = new Parser(gfuns);
	    ASTObject astObject = parser.parse(sf.slashName(), sf.tokens());

	    sf.setASTObject(astObject);

	    return sf;
	} catch (ParseException | IllegalArgumentException e) {
	    System.out.println("Error parsing file: " + vPathStr);
	    System.out.println(e);

	    return null;
	}
    }

    private FSSourceFile scan(String vPathStr) {
	try {
	    Path resolved = basePath.fileAt(vPathStr);

	    if (resolved == null)
		throw new IllegalArgumentException();

	    FSSourceFile sf = new FSSourceFile(resolved);

	    boolean success = basePath.read(sf);

	    if (!success)
		throw new IllegalArgumentException();

	    Scanner scanner = new Scanner();
	    Tokens tokens = scanner.scan(sf.source());

	    sf.setTokens(tokens);

	    return sf;
	} catch (IllegalArgumentException e) {
	    System.out.println("Error scanning file: " + vPathStr);

	    return null;
	}
    }

    public static void main(String... args) {
	if (args.length < 1) {
	    System.out.println("Error: missing base path.");

	    System.exit(-1);
	}
	
	Brainjar brainjar = new Brainjar(args[0]);
	
	brainjar.loadWorld();
    }
}
