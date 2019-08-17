package org.nlogo.extensions.sound;

import org.nlogo.agent.AgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;

public class Voice {
    Double color; // color of voice's trail
    Turtle agent; // a turtle
    // turtle
    /* */
    //  Create a new voice (really a turtle).
    public Voice (World w,Double color, int x, int y, int size )  {
	AgentSet breed = w.turtles();
	agent = w.createTurtle(breed,1,0); //color.intValue(),0);

	try {
	    agent.xandycor(x,y);
	    agent.heading(0.0);
	    agent.shape("line");
	    agent.size(P.PATCHESPERVOICE);
	} catch (org.nlogo.api.AgentException ex) {
	    
	}
	  
    }
    /*
      Move voice some patches to the east.
      @param step num patches to move.
    */
    public void fd(int step) {
	try {
	    agent.xcor( step + agent.xcor() );
	} catch (org.nlogo.api.AgentException ex) { }
    }
    

}
