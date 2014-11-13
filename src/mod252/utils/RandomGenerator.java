package mod252.utils;
import java.util.Random;

public class RandomGenerator {

	public static int GenerateRandomNumber(int max)
	{
		  Random randomGenerator = new Random();
		  return randomGenerator.nextInt(max);
	}
}
