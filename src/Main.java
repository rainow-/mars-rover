import java.awt.Point;
import java.util.Scanner;
import java.util.Timer;

import mod252.agents.RoverAgent;
import mod252.enviroment.GridCell;
import mod252.enviroment.World;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;

public class Main {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		World.InitializeWorld();

		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();
		// Create a default profile
		Profile profile = new ProfileImpl();
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		ContainerController mainContainer = rt.createMainContainer(profile);

		// create agent
		try {
			AgentController ac1 = mainContainer.createNewAgent("mars-rover",
					"mod252.agents.RoverAgent", new Object[1]);
			
			AgentController ac2 = mainContainer.createNewAgent("mars-rover-2", "mod252.agents.RoverAgent", new Object[1]);
			AgentController ac3 = mainContainer.createNewAgent("mars-rover-3", "mod252.agents.RoverAgent", new Object[1]);
			// start the agent
			ac1.start();
			ac2.start();
			ac3.start();
			
		} catch (jade.wrapper.StaleProxyException e) {
			System.err.println("Error launching agent...");
		}
		
		while(true)
		{
			World.PrintWorld();
			System.out.println("**************************");
			System.out.println("Press enter for next step");
			scanner.nextLine();
		}
	}

}
