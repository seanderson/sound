package org.nlogo.extensions.sound;

import org.nlogo.agent.Patch;
import org.nlogo.agent.World;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.nvm.Workspace;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;

/**
 * Initialize world for voices, one lick.
 */
public class Save implements Command {

    // USAGE: save  STRING
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.StringType()}
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        ExtensionContext ec = (ExtensionContext) context;
        // create a NetLogo list for the result
        String fname = null;
        try {
            fname = ec.attachCurrentDirectory(args[0].getString());
        } catch (LogoException e) {
            throw new ExtensionException(e.getMessage());
        } catch (IOException iex) {
            throw new ExtensionException("File not opened."  + iex.getMessage());
        }


        Workspace ws = ec.workspace();

        World w = ws.world();
        try {
            File f = new File(fname);
            PrintWriter pw = new PrintWriter(f);
            w.exportWorld(pw,true);
        } catch (IOException iex) {
            throw new ExtensionException("File not opened."  + iex.getMessage());
        }


    }



} // Save class

