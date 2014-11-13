package mod252.enviroment;

import java.util.ArrayList;

import mod252.utils.Enums.Contents;;

public class GridCell {
	private ArrayList<Contents> contents;
	private static double signalStrength = 0;
	
	public GridCell(Contents contents)
	{
		this.contents = new ArrayList<Contents>();
		this.contents.add(contents);
	}
	
	public GridCell()
	{
		this.contents = new ArrayList<Contents>();
	}

	public ArrayList<Contents> investigateCell() {
		if(contents.size() == 0)
		{
			return new ArrayList<Contents>() {{ add(Contents.empty);}};
		}
		return contents;
	}

	public void addContents(Contents contents) {
		this.contents.add(contents);
	}
	
	public static double getSignalStrength() {
		return signalStrength;
	}

	public static void setSignalStrength(double signalStrength) {
		GridCell.signalStrength = signalStrength;
	}

	public String toString()
	{
		if(contents.size() == 0)
			return " ";
		
		if(signalStrength > 0)
			return signalStrength + "";

		switch(contents.get(contents.size() -1))
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

