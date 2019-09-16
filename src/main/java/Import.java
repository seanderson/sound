package org.nlogo.extensions.sound;


import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.agent.World;

import org.nlogo.nvm.Workspace;

/**
 * Call _import_world to reload data, then reinit to fix up sound extension.
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
        World w;
        Workspace ws;
        try {

            fname = context.attachCurrentDirectory(args[0].getString());
            ws = ec.workspace();
            //ws.importWorld(fname);
            ws.command("import-world " + fname);
        } catch (LogoException e) {
            throw new ExtensionException(e.getMessage());
        } catch (Exception iex) {

            throw new ExtensionException(iex.getMessage());
        }

        Init.attachAgents(ws.world());

    }



} // Save class

