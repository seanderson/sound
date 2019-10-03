package org.nlogo.extensions.sound;

import org.nlogo.api.*;
import org.nlogo.api.Command;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import org.nlogo.api.Context;


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
            P.setPatchSize(args[1].getIntValue());
            Init.resizeWorld(context);
            Init.initDrawing(context);
        } else {
            throw new ExtensionException("Error in SetParams ");
        }
    }


}
    
