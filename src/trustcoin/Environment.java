package trustcoin;

public class Environment
{
	public void run()
	{
		World w;
		
		while(true)
		{
			w = new World();
			w.run();
		}
	}
}