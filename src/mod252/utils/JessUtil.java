package mod252.utils;

import java.awt.Point;

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
            jess.store("active_cluster", "false");
            String rover = "(bind ?rover (assert (rover (name "+agent.getLocalName()+") (carrying " + agent.hasSample()+"))))";
            this.makeassert(rover);
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
        if(jess.fetch("direction") != null)
            dir = (String)jess.fetch("direction").externalAddressValue(null);
        else
            dir = "false";
        
        return dir;
        
    }
    /**
     * Checks if the rover should pickup a rock or not
     * @param gf
     * @param rover
     * @throws JessException 
     */
    public void pickup(RoverAgent rover) throws JessException{
        
        String pickup = "";
        if(jess.fetch("pickup") != null)
            pickup = (String)jess.fetch("pickup").externalAddressValue(null);
        else
            pickup = "false";
        
        if(pickup.equals("true")){
        	
        	Point p = rover.getPosition();
        	World.GetGridCell(p).removeContent(Contents.rock);
        	
            rover.setHasSample(true);
            jess.store("pickup", "false");
        }    
    }
    
    /**
     * if rover is at spaceship it should drop the rocks it is carrying
     * @param rover
     * @throws JessException 
     */
    public void drop(RoverAgent rover) throws JessException{
        String drop = "";
        if(jess.fetch("drop") != null)
            drop = (String)jess.fetch("drop").externalAddressValue(null);
        else
            drop = "false";
        
        if(drop.equals("true")){
            jess.store("drop", "false");
            rover.setHasSample(false);
        }    
    }
    
    /**
     * Checks if the rover should pick up grain in its path
     * @param gf
     * @throws JessException 
     */
    public void pickUpGrain(RoverAgent rover) throws JessException{
         String grain = "";
        if(jess.fetch("pickup_grain") != null)
            grain = (String)jess.fetch("pickup_grain").externalAddressValue(null);
        else
            grain = "false";
        
         if(grain.equals("true")){
        	 World.GetGridCell(rover.getPosition()).deductNumberOfCrumbs();
           jess.store("pickup_grain", "false");
         }
         
       
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
     * Checks if the rover should drop grain
     * @param gf
     * @throws JessException 
     */
    public void dropGrain(RoverAgent rover) throws JessException{
        String grain = "";
        
        if(jess.fetch("drop_grain") != null)
            grain = (String)jess.fetch("drop_grain").externalAddressValue(null);
        else
            grain = "false";
        
        if(grain.equals("true")){
        	World.GetGridCell(rover.getPosition()).setNumberOfCrumbs(2);
            jess.store("drop_grain", "false");
        }
        
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
        
        String left_assert = "(assert (gridbox (direction left) (signal "+left.getSignalStrength()+")"
                                    + " (obstacle "+left.isObstacle()+") (rocks "+left.hasRocks()+")"
                                        + " (grain "+left.deductNumberOfCrumbs()+") (is_spaceship "+left.isSpaceship()+")))";
        String right_assert = "(assert (gridbox (direction left) (signal "+right.getSignalStrength()+")"
                + " (obstacle "+right.isObstacle()+") (rocks "+right.hasRocks()+")"
                + " (grain "+right.deductNumberOfCrumbs()+") (is_spaceship "+right.isSpaceship()+")))";
        String top_assert = "(assert (gridbox (direction left) (signal "+top.getSignalStrength()+")"
                + " (obstacle "+top.isObstacle()+") (rocks "+top.hasRocks()+")"
                + " (grain "+top.deductNumberOfCrumbs()+") (is_spaceship "+top.isSpaceship()+")))";

        String bottom_assert = "(assert (gridbox (direction left) (signal "+bottom.getSignalStrength()+")"
                + " (obstacle "+bottom.isObstacle()+") (rocks "+bottom.hasRocks()+")"
                + " (grain "+bottom.deductNumberOfCrumbs()+") (is_spaceship "+bottom.isSpaceship()+")))";
         String current_assert = "(assert (gridbox (direction left) (signal "+current.getSignalStrength()+")"
                 + " (obstacle "+current.isObstacle()+") (rocks "+current.hasRocks()+")"
                 + " (grain "+current.deductNumberOfCrumbs()+") (is_spaceship "+current.isSpaceship()+")))";
        
        this.makeassert(left_assert);
        this.makeassert(top_assert);
        this.makeassert(right_assert);
        this.makeassert(bottom_assert);
        this.makeassert(current_assert);
        jess.run();
    }
    
    
}
