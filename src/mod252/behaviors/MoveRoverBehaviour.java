package mod252.behaviors;

import mod252.agents.RoverAgent;
import mod252.enviroment.GridCell;
import mod252.enviroment.World;
import mod252.utils.JessUtil;
import mod252.utils.RandomGenerator;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jess.JessException;

public class MoveRoverBehaviour extends CyclicBehaviour {

	private RoverAgent myAgent;
	private String clp;
	private JessUtil jessUtil; 
	
	
	public MoveRoverBehaviour(RoverAgent a, String clp){
		this.myAgent = a;
		this.clp = clp;
		this.jessUtil = new JessUtil(myAgent, clp);
	}
	
	@Override
	public void action() {
		
		GridCell left = World.LookLeft(myAgent.getPosition());
		GridCell right = World.LookLeft(myAgent.getPosition());
		GridCell top = World.LookLeft(myAgent.getPosition());
		GridCell bottom = World.LookLeft(myAgent.getPosition());
		GridCell current = World.LookLeft(myAgent.getPosition());
		
		try {
			jessUtil.search(left, right, top, bottom, current);
			jessUtil.performAction(myAgent);	
			jessUtil.BindRover(myAgent);
			String dir = jessUtil.getDirection();
			
			if(dir.equals("left"))
				myAgent.setPosition(World.MoveLeft(myAgent));
			else if(dir.equals("right"))
				myAgent.setPosition(World.MoveRight(myAgent));
			else if(dir.equals("up"))
				myAgent.setPosition(World.MoveUp(myAgent));
			else if(dir.equals("down"))
				myAgent.setPosition(World.MoveDown(myAgent));
			
		} catch (JessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
