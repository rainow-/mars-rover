package mod252.enviroment;

import java.util.ArrayList;

import mod252.agents.RoverAgent;
import mod252.utils.Enums.Contents;;

public class GridCell {
	private ArrayList<Contents> contents;
	private ArrayList<RoverAgent> agents;
	
	private double signalStrength = 0;
	
	public GridCell(Contents contents)
	{
		this.contents = new ArrayList<Contents>();
		this.contents.add(contents);
	}
	
	public GridCell()
	{
		this.contents = new ArrayList<Contents>();
		agents = new ArrayList<RoverAgent>();
	}
	
	public ArrayList<RoverAgent> getAgents() {
		return agents;
	}

	public void addAgent(RoverAgent agent) {
		this.agents.add(agent);
	}
	
	public void removeAgents(RoverAgent agent)
	{
		this.agents.remove(agent);
	}

	public ArrayList<Contents> getContents() {
		if(contents.size() == 0)
		{
			return new ArrayList<Contents>() {{ add(Contents.empty);}};
		}
		return contents;
	}

	public void addContents(Contents contents) {
		this.contents.add(contents);
	}
	
	public double getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(double signalStrength) {
		this.signalStrength = signalStrength;
	}
	
	public boolean hasRocks()
	{	
		return contents.contains(Contents.rock);
	}
	
	public boolean hasCrumbs()
	{
		return contents.contains(Contents.crumbs);
	}
	
	public boolean isSpaceship()
	{
		return contents.contains(Contents.spaceship);
	}

	public String toString()
	{
		
		if(agents.size() > 0)
		{
			return agents.get(agents.size() - 1).getLocalName().split("-")[2];
		}
		
		if(contents.size() == 0)
			return " ";

/*		
		if(signalStrength > 0)
			return signalStrength + "";
*/
		switch(contents.get(contents.size() - 1))
		{
		case spaceship:
			return "S";
		case rock:
			return "R";
		case crumbs:
			return "C";
		case radioSignal:
			return "G";
		default:
			return " ";
		}
	}
	
	
	

	
}

