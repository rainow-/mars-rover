package mod252.enviroment;

import java.awt.Point;

import mod252.utils.Enums.Contents;
import mod252.utils.RandomGenerator;


public class World {

	private Point spaceShipCoordinates; //Space ship coordinates
	private static int height = 30;
	private static int width = 30;
	private static int signalRange = 5;
	private static int numberOfRocks = 10;
	
	
	private GridCell[][] world;
	
	public World()
	{
		world = new GridCell[width][height];
		PopulateWorld();
		
		spaceShipCoordinates = new Point(RandomGenerator.GenerateRandomNumber(width), RandomGenerator.GenerateRandomNumber(height));
		AddSpaceShip();
		
		AddRocks();
	}
	
	private void AddRocks()
	{
		for(int i = 0; i < numberOfRocks; i++)
		{
			Point point = new Point(RandomGenerator.GenerateRandomNumber(width), RandomGenerator.GenerateRandomNumber(height));
			int rocks = RandomGenerator.GenerateRandomNumber(3);
			
			GenerateRockFormation(point, rocks);
		}
	}
	
	private void AddSpaceShip()
	{
		GenerateSignal(spaceShipCoordinates, signalRange);
		world[spaceShipCoordinates.x][spaceShipCoordinates.y].addContents(Contents.spaceship);	
	}
	
	private void GenerateRockFormation(Point origin, int rocks)
	{	
		int x = origin.x;
		int y = origin.y;
		
		if(rocks < 0)
			return;
		
			try
			{	
				if(x != spaceShipCoordinates.x || y != spaceShipCoordinates.y)
					world[x][y].addContents(Contents.rock);
				
				GenerateRockFormation(new Point(x, y+1), rocks - 1);
				GenerateRockFormation(new Point(x, y-1), rocks - 1);
				GenerateRockFormation(new Point(x-1, y), rocks - 1);
				GenerateRockFormation(new Point(x+1, y), rocks - 1);
			}catch(Exception e)
			{
				//Ignore for now
			}
	}
	
	private void GenerateSignal(Point origin, int range)
	{	
		int x = origin.x;
		int y = origin.y;
		
		if(range < 0)
			return;
		
			try
			{	
				if(x != spaceShipCoordinates.x || y != spaceShipCoordinates.y)
				{
					world[x][y].addContents(Contents.radioSignal);
				}

				
				GenerateSignal(new Point(x, y+1), range - 1);
				GenerateSignal(new Point(x, y-1), range - 1);
				GenerateSignal(new Point(x-1, y), range - 1);
				GenerateSignal(new Point(x+1, y), range - 1);
			}catch(Exception e)
			{
				//Ignore for now
			}
	}
	
	private void PopulateWorld()
	{
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				world[i][j] = new GridCell();
			}
		}
	}
	
	public void PrintWorld()
	{
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				System.out.print(" ["+ world[i][j] + "] ");
			}
			System.out.println();
		}
	}
}
