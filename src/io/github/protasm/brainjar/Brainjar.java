package io.github.protasm.brainjar;

import io.github.protasm.lpc2j.compiler.Compiler;
import io.github.protasm.lpc2j.compiler.GfunsIntfc;

public class Brainjar {
    private static final GfunsIntfc gfuns = new Gfuns();
 
    public static GfunsIntfc gfuns() {
        return gfuns;
    }
     
    public static void main(String... args) {
	if (args.length < 1) {
	    System.out.println("Error: missing base path.");

	    System.exit(-1);
	}

	Brainjar brainjar = new Brainjar();
	Compiler compiler = new Compiler("java/lang/Object");
    }
}
