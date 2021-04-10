//============================================================================
// ArgParser.java
// 	Description:
// 		This class handles parsing the flags and arguements passed
// 		during command line function calls. It also stores the values
// 		for those flags. 
//============================================================================

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ArgParser
{
	private String ip_address;
	private String username;
	private String password;
	private String command;
	private String cmd_option;
	private int maxMoves;
	private boolean helpSelected;

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
		maxMoves = 10;
		helpSelected = false;
	}

	//===================================================================
	// Gettors 
	//===================================================================

	public boolean getHelpSelected() { return helpSelected; }
	public String getIPAddress() { return ip_address; }
	public String getUsername() { return username; }
	public String getPassword() { return password; }
	public String getCommand() { return command; }
	public String getCmdOption() { return cmd_option; }
	public int getMaxMoves() { return maxMoves; }

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
						if(args[i+1].equals("help"))
						{
							helpSelected = true;
							printFile("command-help-basic.txt");
							printFile("command-help-advanced.txt");
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
					helpSelected = true;
					printFile("help.txt");
					break;
				case "-o":
				case "--option":
					// Allow multi-word options, specifically
					// for partition names.
					while(((i+1)<args.length) && !args[i+1].substring(0,1).equals("-"))
					//if((i+1)<args.length)
					{
						if(cmd_option.equals("none"))
						{
							cmd_option = args[i+1];
						}
						else
						{
							// The %20 is required for the
							// library to properly parse
							// the text.
							cmd_option += "%20" + args[i+1];
						}
						i++;
					}
					break;
				case "-m":
				case "--max":
				case "--moves":
				case "--max-moves":
					if((i+1)<args.length)
					{
						maxMoves = Integer.parseInt(args[i+1]);
						i++;
					}
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
				default:
					i++;
					break;
			}
		}
	}

	private void printFile(String fileName)
	{
		try
		{
			File inFile = new File("../lib/" + fileName);
			Scanner reader = new Scanner(inFile);
			String textLine;

			while(reader.hasNextLine())
			{
				textLine = reader.nextLine();
				System.out.println(textLine);
			}
		}
		catch(FileNotFoundException e)
		{
			printHelp();
			e.printStackTrace();
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
