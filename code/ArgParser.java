//============================================================================
// ArgParser.java
// 	Description:
// 		This class handles parsing the flags and arguements passed
// 		during command line function calls. It also stores the values
// 		for those flags. 
//============================================================================

public class ArgParser
{
	private String ip_address;
	private String username;
	private String password;
	private String command;
	private String cmd_option;

	//===================================================================
	// Constructor
	//===================================================================

	public ArgParser()
	{
		ip_address = "none";
		username = "none";
		password = "";
		command = "none";
		cmd_option = "none";
	}

	//===================================================================
	// Gettors 
	//===================================================================

	public String getIPAddress() { return ip_address; }
	public String getUsername() { return username; }
	public String getPassword() { return password; }
	public String getCommand() { return command; }
	public String getCmdOption() { return cmd_option; }

	//===================================================================
	// functions 
	//===================================================================

	public boolean checkValidInput()
	{
		// Verify if enough arguments were entered to query the library.
		if(ip_address.equals("none") || username.equals("none") || command.equals("none"))
		{
			return false;
		}
		else
		{
			return true;
		}

	}

	public void parseArguments(String[] args)
	{
		for(int i=0; i<args.length; i++)
		{
			switch (args[i])
			{
				case "-c":
				case "--command":
					if((i+1)<args.length)
					{
						// Check to see if the user requested help
						if(args[i+1].equals("-h") || args[i+1].equals("--help"))
						{
							System.out.println("Add extra help");
						}
						else
						{
							command = args[i+1];
						}
						i++;
					}
					break;
				case "-e":
				case "--endpoint":
					if((i+1)<args.length)
					{
						ip_address = args[i+1];
						i++;
					}
					break;
				case "-h":
				case "--help":
					printHelp();
					break;
				case "-o":
				case "--option":
					if((i+1)<args.length)
					{
						cmd_option = args[i+1];
						i++;
					}
					break;
				case "-p":
				case "--pass":
				case "--password":
					if((i+1)<args.length)
					{
						password = args[i+1];
						i++;
					}
					break;
				case "-u":
				case "--user":
				case "--username":
					if((i+1)<args.length)
					{
						username = args[i+1];
						i++;
					}
					break;
			}
		}
	}

	public void printHelp()
	{
		System.out.println("Spectra XML Command Parser");
		System.out.println("\tThis application is designed to be used with the XML interface of Spectra Logic tape libraries.");
		System.out.println("\nAvailable Commands:");
		System.out.println("\t-c,--command\tEnd the command you would like to execute. Examples list-partitions. Use -h after this flag for a detailed list.");
		System.out.println("\t-e,--endpoint\tThe IP address of the Spectra Logic tape library.");
		System.out.println("\t-o,--option\tSpecify options for the specific command, such as partition name.");
		System.out.println("\t-p,--password\tSpecify the user password. Leave blank if the user does not have a password assigned.");
		System.out.println("\t-u,--username\tSpecify the username to use for the connection.");
	}
}
