import org.nlogo.agent.World;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.core.WorldDimensions;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.nvm.Workspace;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.List;

/**
 * Load world from CSV file.
 */
public class Load implements Command {

    // USAGE: load  STRING
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
//            ImportErrorHandler handler = new ImportErrorHandler() {
//                @Override
//                public void showError(String title, String message, String defaultAction) {
//                    throw new ExtensionException("Error in load");
//                }
//            };
//            StringReader reader = new StringReader(new String());
//            BufferedReader buffreader = new BufferedReader(new Reader());
//
//            w.importWorld(handler,user,reader,buffreader);
        } catch (IOException iex) {
            throw new ExtensionException("File not opened."  + iex.getMessage());
        }


    }



} // Save class

