package mod252.agents;


import java.awt.Point;

import mod252.behaviors.RoverBehavior;
import jess.*;
import jade.core.*;
import jade.domain.DFService;
import jade.domain.FIPAException;

public class RoverAgent extends Agent {

	/*
	 * Rover has one of three states
	 * 1) Search mode - randomly searches for rocks
	 * 2) Go home mode - goes back to mothership following the frequency
	 * 3) Follow crumbs - follows crumbs to rock pile
	 */
	
	private Point position;
	private Rete jess;
	private boolean hasSample = false;

	public Rete getRete() {
		return jess;
	}

	public boolean hasSample() {
		return hasSample;
	}

	public void setHasSample(boolean hasSample) {
		this.hasSample = hasSample;
	}

	/**
	 * adds the JessBehaviour and that's all.
	 **/
	  protected void setup() {
		  
		  World world = new World();
		  position = world.getSpaceShipPosition();
		  
		  try {
			  DFService.register(this, mod252.utils.DFUtils.getDF(getAID(), getLocalName(), "mars-rover"));
		  } catch (FIPAException fe) {
			  fe.printStackTrace();
		  }
		  
	    // add the behaviour
	    // 1 is the number of steps that must be executed at each run of
	    // the Jess engine before giving back the control to the Java code
	    addBehaviour(new RoverBehavior(this,"mod252/rules/roverAgent.clp",1)); 
	  }
}
