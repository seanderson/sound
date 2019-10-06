package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import org.nlogo.api.Context;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.agent.World;

import java.util.ArrayList;


/**
 * Set certain parameters.
 */
public class SetParams implements Command {
    /*
      Set param to a value
    */
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{
                Syntax.StringType(),
                Syntax.NumberType()}
        );
    }

    public void perform(Argument args[], Context context)
            throws ExtensionException {
        String param = args[0].getString();
        if (param.equals("BPM")) {
            P.setBPM(args[1].getIntValue());
        } else if (param.equals("PATCHSIZE")) {

            ExtensionContext ec = (ExtensionContext) context;
            org.nlogo.nvm.Workspace ws = ec.workspace();
            World w = ws.world();
            Init.stashPatches(w);
            P.setPatchSize(args[1].getIntValue());
            Init.resizeWorld(context);
            Init.initDrawing(context);
            Init.attachAgents(w);
            Init.restorePatches(w);
        } else {
            throw new ExtensionException("Error in SetParams ");
        }
    }


}
    
