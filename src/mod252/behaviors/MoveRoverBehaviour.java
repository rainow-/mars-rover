package mod252.behaviors;

import mod252.agents.RoverAgent;
import mod252.enviroment.World;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class MoveRoverBehaviour extends CyclicBehaviour {

	private RoverAgent myAgent;
	private String clp;
	
	
	public MoveRoverBehaviour(RoverAgent a, String clp){
		this.myAgent = a;
		this.clp = clp;
	}
	
	@Override
	public void action() {
		
		//World w = getWorld();
		//find were we are
		//location = myAgent.getLocation();
		
		//search for new path
		//assert(location);
		//Jess.run();
		
		
		
		//inform agents
		//ACL messages 
		ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
		//inform.addReceiver(myAgent.getSpaceShipAgent());
		inform.setConversationId("rover-" + myAgent.getName());
//		inform.setContent(updatePackage);
		
		
		
		
		
		//update information
		//myAgent.setLocation();
		
		
	}
	
	
	
}
