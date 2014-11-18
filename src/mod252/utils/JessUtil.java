package mod252.utils;

import jade.util.leap.Collection;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import mod252.agents.RoverAgent;
import mod252.enviroment.GridCell;
import mod252.enviroment.World;
import mod252.utils.Enums.Contents;
import jess.JessException;
import jess.Rete;



public class JessUtil {

    // class variables
    private Rete jess; // holds the pointer to jess
    private RoverAgent myAgent; // holds the pointer to this agent

    /**
     * Starts the jess file
     * @param agent the agent that adds the behaviour
     * @param jessFile the name of the Jess file to be executed
     */
    public JessUtil(RoverAgent agent, String jessFile) {
        jess = new Rete();
        myAgent = agent;
        try {
            jess.batch(jessFile);
            String carrying = "(bind ?carrying false)";
            String came = "(bind ?from down)";
            String signal = "(bind ?signal 0)";
            jess.executeCommand(carrying);
            jess.executeCommand(came);
            jess.executeCommand(signal);
            jess.run();
        } catch (JessException ex) {

        }
        
    }


    /**
     * makeasserts a fact representing an ACLMessage in Jess. It is called after
     * the arrival of a message.
     */
    public void makeassert(String fact) {
        try {
            jess.executeCommand(fact);
        } catch (JessException re) {
            re.printStackTrace(System.err);
        }
    }
    
    /**
     * Makes the next direction available to the rover, so that it can move.
     * @return
     * @throws JessException 
     */
    public String getDirection() throws JessException{
        String dir = "";
        if(jess.fetch("dir") != null)
            dir = (String)jess.fetch("dir").externalAddressValue(null);
        else
            dir = "false";
        
		if(dir.contains("\"")){
			dir = dir.subSequence(1, dir.length()-1).toString();
		}
        
        jess.clearStorage();
        
        return dir; 
    }
    
    public void updateSignal(GridCell current) throws JessException{
    	String signal = "(bind ?signal " + current.getSignalStrength() + ")";
    	jess.executeCommand(signal);
    	jess.run();
    }
    
    /**
     * Checks if the rover should pickup a rock or not
     * @param gf
     * @param rover
     * @throws JessException 
     */
    public void performAction(RoverAgent rover) throws JessException{

        String act = "", dir = "";
        if(jess.fetch("act") != null && jess.fetch("dir") != null){
        	act = (String)jess.fetch("act").externalAddressValue(null);
        	dir = (String)jess.fetch("dir").externalAddressValue(null);
        	String cameFrom = "(bind ?from " + dir + ")";

        	jess.executeCommand(cameFrom);
        }    
        else{
            act = "false";
   
        }
        
        System.out.println("Action: " + act + "         Direction: " + dir);
        
        if(dir.equals("here"))
        {
        	String carry = null;
        	if(act.equals("gather")){
            	carry = "(bind ?carrying true)";
        		
            	Point p = rover.getPosition();
            	World.GetGridCell(p).removeContent(Contents.rock);
            	
                rover.setHasSample(true);
            }
            else if(act.equals("drop")){
            	carry = "(bind ?carrying false)";
            	rover.setHasSample(false);
            }
        	if(carry != null)
        		jess.executeCommand(carry);
        }
        else
        {
        	if(act.equals("follow_signal") && rover.hasSample()){
        		World.GetGridCell(rover.getPosition()).setNumberOfCrumbs(2);
        	}
            else if(act.equals("follow_grain")){
            	World.GetGridCell(rover.getPosition()).deductNumberOfCrumbs();
            }
        }
        jess.run();
    }
    
    /**
     * Methods to update the rover in jess
     * @param message
     * @throws JessException 
     */
    public void modifyRover(String message) throws JessException{
        jess.executeCommand(message);
        jess.run();
    }

    /**
     * Main method, that makes the rover moves. it just asserts 5 gridfields.
     * @param left
     * @param right
     * @param top
     * @param bottom
     * @param current
     * @throws JessException 
     */
    public void search(GridCell left, GridCell right, GridCell top, GridCell bottom, GridCell current) throws JessException{
        
        String left_assert = "(assert (environment "
        		+ "(signal "+left.getSignalStrength()+")"                    
        		+ "(obstacle "+left.isObstacle()+") "
        		+ "(sample "+left.hasRocks()+")"
                + "(crumb "+left.hasCrumbs()+")"
                + "(spaceship "+left.isSpaceship()+")"
                + "(direction left) ) )";
        
        String right_assert = "(assert (environment "
        		+ "(signal "+right.getSignalStrength()+")"                    
        		+ "(obstacle "+right.isObstacle()+") "
        		+ "(sample "+right.hasRocks()+")"
                + "(crumb "+right.hasCrumbs()+")"
                + "(spaceship "+right.isSpaceship()+")"
                + "(direction right) ) )";
        
        String top_assert = "(assert (environment "
        		+ "(signal "+top.getSignalStrength()+")"                    
        		+ "(obstacle "+top.isObstacle()+") "
        		+ "(sample "+top.hasRocks()+")"
                + "(crumb "+top.hasCrumbs()+")"
                + "(spaceship "+top.isSpaceship()+")"
                + "(direction up) ) )";
        
        String bottom_assert = "(assert (environment "
        		+ "(signal "+bottom.getSignalStrength()+")"                    
        		+ "(obstacle "+bottom.isObstacle()+") "
        		+ "(sample "+bottom.hasRocks()+")"
                + "(crumb "+bottom.hasCrumbs()+")"
                + "(spaceship "+bottom.isSpaceship()+")"
                + "(direction down) ) )";
        
        String current_assert = "(assert (environment "
        		+ "(signal "+current.getSignalStrength()+")"                    
        		+ "(obstacle "+current.isObstacle()+") "
        		+ "(sample "+current.hasRocks()+")"
                + "(crumb "+current.hasCrumbs()+")"
                + "(spaceship "+current.isSpaceship()+")"
                + "(direction here) ) )";
        
        String[] ar = {left_assert, right_assert, top_assert, bottom_assert, current_assert };
        
        for(int i = 0; i < ar.length; i++)
        {
        	this.makeassert(ar[i]);
        }
       
        jess.run();
    }
    
    
}
