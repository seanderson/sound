
package org.nlogo.extensions.sound;


import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import org.nlogo.nvm.Workspace;
import org.nlogo.nvm.ExtensionContext;

import org.nlogo.agent.World;
import org.nlogo.agent.Patch;

import java.util.ArrayList;


public class AddVoices implements Command {


    public Syntax getSyntax() {
        /** Add N voices to existing world. */
        return SyntaxJ.commandSyntax(new int[]{Syntax.NumberType()

                }
        );
    }


    public void perform(Argument args[], Context context)
            throws ExtensionException {

        int num;

        try {
            num = args[0].getIntValue();
        } catch (LogoException e) {
            throw new ExtensionException(e.getMessage());
        }

        if (num <= 0 || P.NVOICES + num > P.MAXVOICES)
            throw new ExtensionException("Total number of voices added must be between 1 and 10");

        ExtensionContext ec = (ExtensionContext) context;
        org.nlogo.nvm.Workspace ws = ec.workspace();
        World w = ws.world();

        Init.stashPatches(w); // store all patch info

        P.addvoices(w,num);;

        Init.resizeWorld(ec);

        Init.initDrawing(ec);
        Init.restorePatches(w);  // restore stashedpatch info

    }


}



