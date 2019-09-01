package org.nlogo.extensions.sound;

import org.nlogo.agent.World;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.nvm.Workspace;

import java.io.IOException;

/**
 * Reinitialize AFTER haveing imported the world.
 */
public class Reinit implements Command {

    // USAGE: load  STRING
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                }
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        ExtensionContext ec = (ExtensionContext) context;

        Workspace ws = ec.workspace();
        World w = ws.world();

        // init the drawing
        //Init.initDrawing(w,ec);
        Init.attachAgents(w);



    }



} // Reinit class

