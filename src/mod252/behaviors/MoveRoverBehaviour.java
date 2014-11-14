package mod252.behaviors;

import mod252.agents.RoverAgent;
import mod252.enviroment.World;
import mod252.utils.RandomGenerator;
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
		int randomNumber = RandomGenerator.GenerateRandomNumber(4) + 1;
		
		switch(randomNumber)
		{
		case 1 :
			myAgent.setPosition(World.MoveLeft(myAgent));
			break;
		case 2 :
			myAgent.setPosition(World.MoveRight(myAgent));
			break;
		case 3 :
			myAgent.setPosition(World.MoveDown(myAgent));
			break;
		case 4 :
			myAgent.setPosition(World.MoveUp(myAgent));
			break;
		default :
			//do nothing
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
