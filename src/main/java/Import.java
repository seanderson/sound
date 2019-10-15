package org.nlogo.extensions.sound;


import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.agent.World;

import org.nlogo.nvm.Workspace;

import java.net.MalformedURLException;
import org.nlogo.nvm.RuntimePrimitiveException;

/**
 * Call _import_world to reload data.
 */
public class Import implements Command {

    // USAGE: save  STRING
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.StringType()
        }
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        ExtensionContext ec = (ExtensionContext) context;
        // create a NetLogo list for the result
        String fname;

        Workspace ws = ec.workspace();
        World w = ws.world();
        String filePath;
        try {
            filePath = context.attachCurrentDirectory(args[0].getString());
        }
        catch (MalformedURLException ex) {
            throw new ExtensionException(ex.getMessage());
        }

        Init.stashPatches(w);
        // Workspace.waitFor() switches to the event thread if we're
        // running with a GUI.
        ws.waitFor
                (new org.nlogo.api.CommandRunnable() {
                    public void run()  throws RuntimeException {
                        try {
                            ws.importWorld(filePath);
                        } catch (java.io.IOException ex) {
                            throw new RuntimeException(ex.getMessage());
                        }
                    }
                });



        Init.restorePatches(w);
        Init.attachAgents(context,w);

    }






} // Save class

