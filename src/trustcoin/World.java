package trustcoin;

import java.math.BigInteger;
import java.util.Scanner;

import Jama.*;


public class World
{
	Person[] people;
	String [] messages;
	
	String input;
	
	
	public class Person
	{
		public String name;
		public int number;
		double money;
		
		public String [] relationships;
		public double [] trust;
		public double [] unsharedtrust;
		
		Person()
		{
			this.relationships = new String[0];
			this.trust = new double[0];
			this.unsharedtrust = new double[0];
			this.number = 0;
			this.money = 1;
		}
		
		Person(String n)
		{
			this();
			
			this.name = n;
		}
		
		Person(String s, int n)
		{
			this(s);
			
			this.number = n;
		}
		
		public void update(int n)
		{
			if(n == 0)
			{
				relationships = new String[people.length];
				trust = new double[people.length];
				unsharedtrust = new double[people.length];
				
				for(int i = 0; i < people.length; i++)
				{
					relationships[i] = people[i].name;
					trust[i] = 0;
					unsharedtrust[i] = 0;
				}
			}
			
			String [] message = new String [4];
			
			if(messages.length > n)
			{
				message = messages[n].split(" ");
			}
			else
			{
				System.out.println("nonexistent message" + " " + n + " " + "was attempted to be accessed");
				return;
			}
			
			if (message[0].equals(this.name))
			{
				for(int i = 0; i < relationships.length; i++)
				{
					if(relationships[i].equals(message[2]))
					{
						if(message[3].equals("r"))
						{
							people[i].money += Double.parseDouble(message[1]);
							
							for(int j = 0; j < people.length; j++)
							{
								people[j].trust[i] += Double.parseDouble(message[1]) * this.trust[i] / this.money;
								people[j].trust[this.number] -= Double.parseDouble(message[1]) * this.trust[i] /this.money;
							}
							
							this.money -= Double.parseDouble(message[1]);
						}
						
						if(message[3].equals("t"))
						{
							this.unsharedtrust[i] += Double.parseDouble(message[1]);//add new trust
							//process unshared trust
							if(this.number < people[i].unsharedtrust.length)//if it's safe to access i's unsharedtrust array
							{
								if(this.unsharedtrust[i] > 0 && people[i].unsharedtrust[this.number] > 0)//if both people have unshared trust
								{
									double trusttoshare;
									//find if there is a difference between their unshared trust
									if(this.unsharedtrust[i] - people[i].unsharedtrust[this.number] == 0)
									{
										trusttoshare = this.unsharedtrust[i];
									}
									else if(this.unsharedtrust[i] - people[i].unsharedtrust[this.number] > 0)//find the positive difference between their unshared trust and store in trusttoshare
									{
										trusttoshare = this.unsharedtrust[i] - people[i].unsharedtrust[this.number];
									}
									else
									{
										trusttoshare = people[i].unsharedtrust[this.number] - this.unsharedtrust[i];
									}
									//add unshared trust to trust
									this.trust[i] += trusttoshare;
									people[i].trust[this.number] += trusttoshare;
									//removed now shared trust from unsharedtrust
									this.unsharedtrust[i] -= trusttoshare;
									people[i].unsharedtrust[this.number] -= trusttoshare;
								}
							}
						}
					}
				}
			}
			//process everyone's negative unshared trust
			for(int i = 0; i < people.length; i++)
			{
				for(int j = 0; j < people.length; j++)
				{
					if(people.length > i && people.length > j)//if it's safe to access people array
					{
						if(people[i].unsharedtrust.length > j && people[i].trust.length > j && people[j].trust.length > i)
						{
							if(people[i].unsharedtrust[j] < 0)//if unshared trust is negative
							{//TODO add tracking of regained trust
								people[i].trust[j] += people[i].unsharedtrust[j];//remove trust of j from i
								people[j].trust[i] += people[j].unsharedtrust[i];//remove trust of i from j
								people[i].unsharedtrust[j] -= people[i].unsharedtrust[j];//zero unshared trust
							}
						}
					}
				}
			}
		}
	}
	
	
	World()
	{
		people = new Person[0];
		messages = new String[0];
	}
	
	public void run()
	{
		Scanner s = new Scanner(System.in);
		
		people = new Person[0];
		messages = new String[0];
		
		while(true)
		{
			System.out.println();
			System.out.println();
			
			System.out.println("input \"p\" to create a new person");
			System.out.println("input \"t\" to create a transaction");
			System.out.println("input \"m\" to readout all messages");
			System.out.println("input \"r\" to readout the status of all current people");
			
			System.out.println();
			
			
			input = s.nextLine();
			
			
			System.out.println();
			System.out.println();
			
			
			if (input.equals("p"))
			{
				System.out.println("enter the name of the new person");
				
				String newname = s.nextLine();
				
				
				Person [] peoples;
				peoples = new Person[people.length + 1];
				
				int i = 0;
				
				while(i < people.length)
				{
					peoples[i] = people[i];
					
					i++;
				}
				
				people = peoples;
				
				people[i] = new Person(newname, i);
			}
			
			if(input.equals("t"))
			{
				double amount;
				String sender;
				String receiver;
				String realmoney;
				
				System.out.println("enter the sender of the funds");
				
				sender = s.nextLine();
				
				System.out.println("enter the receiver");
				
				receiver = s.nextLine();
				
				System.out.println("is it real money or trust? [r/t]");
				realmoney = s.nextLine();
				
				System.out.println("enter the amount sent");
				
				amount = s.nextDouble();
				s.nextLine();
				
				
				String [] newmessages;
				newmessages = new String[messages.length + 1];
				
				int i = 0;
				
				while(i < messages.length)
				{
					newmessages[i] = messages[i];
					
					i++;
				}
				
				messages = newmessages;
				
				messages[i] = sender.concat(" ".concat(Double.toString(amount).concat(" ".concat(receiver.concat(" ".concat(realmoney))))));
			}
			
			if(input.equals("m"))
			{
				for(int i = 0; i < messages.length; i++)
				{
					System.out.println(messages[i]);
				}
			}
			
			if(input.equals("r"))
			{
				if(people.length > 0)
				{
					/*System.out.println("from whose perspective do you want to see trust?");
					
					String n = s.nextLine();/**/
					
					
					double [][] z = A();
					double [][] w = new double[z.length][1];
					
					for(int i = 0; i < w.length; i++)
					{
						w[i][0] = 1;
					}
					
					
					Matrix a = new Matrix(z);
					Matrix x = new Matrix(w);
					
					EigenvalueDecomposition e = new EigenvalueDecomposition(a);
					
					Matrix d = e.getD();
					
					double [][] k = d.getArray();
					
					double largest = 0;
					
					for(int i = 0; i < k.length * 2; i++)
					{
						for(int j = 0; j < k.length; j++)
						{
							for(int l = 0; l < k[j].length; l++)
							{
								k[j][l] = Math.abs(k[j][l]);
								
								if(k[j][l] < largest)
								{
									k[j][l] = 0;
								}
								else
								{
									largest = k[j][l];
								}
							}
						}
					}
					
					d = new Matrix(k);
					
					Matrix c = e.getV().solve(x);
					Matrix yc = d.times(c);
					
					x = e.getV().times(yc);
					double[][] y = x.getArray();
					
					
					for(int i = 0; i < messages.length; i++)
					{
						for(int j = 0; j < people.length; j++)
						{
							people[j].update(i);
						}
					}
					
					
					System.out.println("money:");
					System.out.println("");
					for(int i = 0; i < people.length; i++)
					{
						System.out.println(people[i].name + " " + people[i].money);
					}
					
					System.out.println();
					
					/*boolean found = false;
					
					for(int i = 0; i < people.length; i++)
					{
						if(people[i].name.equals(n))
						{
							found = true;
							
							System.out.println("trust (as seen by" + " " + n + "):");
							System.out.println("");
							
							for(int j = 0; j < people[i].relationships.length; j++)
							{
								System.out.println(people[i].relationships[j] + " " + people[i].trust[j]);
							}
						}
					}
					
					if(!found)
					{
						System.out.println(n + " " + "was not found");
					}/**/
					
					System.out.println("trust:");
					System.out.println();
					
					for(int i = 0; i < y.length; i++)
					{
						System.out.println(people[i].name + " " + y[i][0]);
					}
				}
				else
				{
					System.out.println("there are too few people!");
				}
			}
		}
	}
	
	public double [][] A()
	{
		for(int i = 0; i < people.length; i++)
		{
			people[i].update(0);
		}
		
		for(int i = 0; i < people.length; i++)
		{
			for(int j = 0; j < messages.length; j++)
			{
				people[i].update(j);
			}
		}
		
		double [][] a = new double [people.length][people.length];
		
		for(int i = 0; i < a.length; i++)
		{
			for(int j = 0; j< a[i].length; j++)
			{
				a[i][j] = 0;
			}
		}
		
		for(int i = 0; i < people.length; i++)
		{
			double total = 0;
			
			for(int j = 0; j < people[i].trust.length; j++)
			{
				total += people[i].trust[j];
			}
			
			while(total == 0)
			{
				//people[i].trust[i] += 0.00000000000001;
				//not so good
				//people[i].trust[i] += 7.105427357601002E-15;
				//good
				//people[i].trust[i] += 8.881784197001253E-16;
				//better
				people[i].trust[i] += 1;
				
				for(int j = 0; j < people[i].trust.length; j++)
				{
					total += people[i].trust[j];
				}
			}
			
			for(int j = 0; j < people.length; j++)
			{
				a[j][i] = people[i].trust[j]/total;
			}
		}
		
		
		return a;
	}
}