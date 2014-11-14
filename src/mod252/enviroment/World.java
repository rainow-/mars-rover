package mod252.enviroment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import mod252.agents.RoverAgent;
import mod252.utils.Enums.Contents;
import mod252.utils.RandomGenerator;

public class World {

	private static Point spaceShipCoordinates; // Space ship coordinates
	private static int height = 30;
	private static int width = 30;
	private static int signalRange = 5;
	private static int numberOfRocks = 10;

	private static GridCell[][] world;
	private static List<RoverAgent> agents;
	
	
	public static void InitializeWorld() {
		world = new GridCell[width][height];
		PopulateWorld();

		spaceShipCoordinates = new Point(
				RandomGenerator.GenerateRandomNumber(width),
				RandomGenerator.GenerateRandomNumber(height));
		AddSpaceShip();
		AddRocks();
		
		agents = new ArrayList<RoverAgent>();
	}
	
	public static void AddAgents(RoverAgent rover)
	{
		agents.add(rover);
		world[rover.getPosition().x][rover.getPosition().y].addAgent(rover);
	}
	

	private static void AddRocks() {
		for (int i = 0; i < numberOfRocks; i++) {
			Point point = new Point(
					RandomGenerator.GenerateRandomNumber(width),
					RandomGenerator.GenerateRandomNumber(height));
			int rocks = RandomGenerator.GenerateRandomNumber(3);

			GenerateRockFormation(point, rocks);
		}
	}

	private static void AddSpaceShip() {
		GenerateSignal(spaceShipCoordinates, signalRange);
		world[spaceShipCoordinates.x][spaceShipCoordinates.y]
				.addContents(Contents.spaceship);
	}

	public static Point getSpaceShipCoordinates() {
		return spaceShipCoordinates;
	}

	public static GridCell getPercept(Point p) {
		return world[p.x][p.y];
	}

	private static void GenerateRockFormation(Point origin, int rocks) {
		int x = origin.x;
		int y = origin.y;

		if (rocks < 0)
			return;

		try {
			if ((x != spaceShipCoordinates.x || y != spaceShipCoordinates.y) && !world[x][y].hasRocks())
				world[x][y].addContents(Contents.rock);

			GenerateRockFormation(new Point(x, y + 1), rocks - 1);
			GenerateRockFormation(new Point(x, y - 1), rocks - 1);
			GenerateRockFormation(new Point(x - 1, y), rocks - 1);
			GenerateRockFormation(new Point(x + 1, y), rocks - 1);
		} catch (Exception e) {
			// Ignore for now
		}
	}

	private static void GenerateSignal(Point origin, int range) {
		int x = origin.x;
		int y = origin.y;

		if (range < 0)
			return;

		try {

			if (world[x][y].getSignalStrength() == 0) {
				world[x][y].addContents(Contents.radioSignal);
				int signalModifier = (signalRange - range) + 1;
				double signalStrength = 100 / signalModifier;
				world[x][y].setSignalStrength(signalStrength);
			}

			GenerateSignal(new Point(x, y + 1), range - 1);
			GenerateSignal(new Point(x, y - 1), range - 1);
			GenerateSignal(new Point(x - 1, y), range - 1);
			GenerateSignal(new Point(x + 1, y), range - 1);
		} catch (Exception e) {
			// Ignore for now
		}
	}

	private static void PopulateWorld() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				world[i][j] = new GridCell();
			}
		}
	}

	public static void PrintWorld() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				System.out.print(" [" + world[i][j] + "] ");
			}
			System.out.println();
		}
	}
	
	//Methods to move agent
	public static void MoveLeft(RoverAgent rover)
	{
		Point newPoint = new Point(rover.getPosition().x - 1 , rover.getPosition().y);
		MoveAgent(rover, newPoint);
	}
	
	public static void MoveUp(RoverAgent rover)
	{
		Point newPoint = new Point(rover.getPosition().x, rover.getPosition().y + 1);
		MoveAgent(rover, newPoint);
	}
	
	public static void MoveRight(RoverAgent rover)
	{
		Point newPoint = new Point(rover.getPosition().x + 1 , rover.getPosition().y);
		MoveAgent(rover, newPoint);
	}
	
	public static void MoveDown(RoverAgent rover)
	{
		Point newPoint = new Point(rover.getPosition().x, rover.getPosition().y - 1);
		MoveAgent(rover, newPoint);
	}
	
	private static void MoveAgent(RoverAgent rover, Point newPoint)
	{
		Point currentPoint = rover.getPosition();
		
		world[currentPoint.x][currentPoint.y].removeAgents();
		world[newPoint.x][newPoint.y].addAgent(rover);
	}

	// Methods to get perception easier
	public static GridCell LookLeft(Point p) {
		return world[p.x - 1][p.y];
	}

	public static GridCell LookRight(Point p) {
		return world[p.x + 1][p.y];
	}

	public static GridCell LookUp(Point p) {
		return world[p.x][p.y];
	}

	public static GridCell LookDown(Point p) {
		return world[p.x][p.y];
	}
}
