//============================================================================
// ArgParser.java
// 	Description:
// 		This class handles parsing the flags and arguements passed
// 		during command line function calls. It also stores the values
// 		for those flags. 
//============================================================================

package com.socialvagrancy.spectraxml.ui;

import com.socialvagrancy.spectraxml.structures.Configuration;
import com.socialvagrancy.spectraxml.structures.LibraryProfile;
import com.socialvagrancy.spectraxml.utils.Load;
import com.socialvagrancy.spectraxml.utils.ProfileManager;

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
	private String cmd_option2;
	private String cmd_option3;
	private String cmd_option4;
	private String cmd_option5;
	private String output_format;
	private int maxMoves;
	private int library_maximum;
	private boolean helpSelected;
	private boolean secure_https;
	private boolean ignore_ssl;

	// Logging vars
	private String log_path;
	private int log_level;
	private int log_count;
	private int log_size;

	// These are for tracking the assignment of flags.
	// for ease of coding, multiple flags map to the same
	// variables depending on the function being called.
	// If they aren't valid, isValid is set to false.
	private boolean boolean_flag; // generic boolean variable that can be set to true with flags.
	private boolean isValid; // Is the entered input valid
	private boolean option_set;
	private boolean option2_set;
	private boolean option3_set;
	private boolean option4_set;
	private boolean option5_set;

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
		cmd_option2 = "none";
		cmd_option3 = "none";
		cmd_option4 = "none";
		cmd_option5 = "none";
		output_format = "shell";
		maxMoves = 10;
		library_maximum = 100;
		helpSelected = false;
		secure_https = true; // https connection
		ignore_ssl = false; // ignore ssl certificates.
	
		// Logging
		log_path = "../logs/slxml-main.log";
		log_count = 3;
		log_level = 1;
		log_size = 10240;
		
		// isValid
		// 	Used to verify and validate the entered command.
		// 	Defaults to ture as this is used to check the		
		// 	option values. Options may not be
		// 	required for specific commands.
		isValid = true;  
		boolean_flag = false;
		option_set = false;
		option2_set = false;
		option3_set = false;
		option4_set = false;
		option5_set = false;
	}

	public ArgParser(String config_path)
	{
		Configuration config = Load.config(config_path);

		ip_address = "none";
		username = "none";
		password = "";
		command = "none";
		cmd_option = "none";
		cmd_option2 = "none";
		cmd_option3 = "none";
		cmd_option4 = "none";
		cmd_option5 = "none";
		output_format = "shell";
		maxMoves = 10;
		library_maximum = 100;
		helpSelected = false;
		secure_https = true; // https connection
		ignore_ssl = false; // ignore ssl certificates.
	
		// Logging
		log_path = config.log_path;
		log_count = config.log_count;
		log_level = config.getLogLevel();
		log_size = config.log_size;

		// isValid
		// 	Used to verify and validate the entered command.
		// 	Defaults to ture as this is used to check the		
		// 	option values. Options may not be
		// 	required for specific commands.
		isValid = true;  
		option_set = false;
		option2_set = false;
		option3_set = false;
		option4_set = false;
		option5_set = false;
	
	}

	//===================================================================
	// Gettors 
	//===================================================================

	public boolean getBooleanFlag() { return boolean_flag; }
	public boolean getHelpSelected() { return helpSelected; }
	public boolean getIgnoreSSL() { return ignore_ssl; }
	public boolean getSecureHTTPS() { return secure_https; }
	public String getIPAddress() { return ip_address; }
	public String getUsername() { return username; }
	public String getPassword() { return password; }
	public String getCommand() { return command; }
	public String getCmdOption() { return cmd_option; }
	public String getCmdOption2() { return cmd_option2; }
	public String getCmdOption3() { return cmd_option3; }
	public String getCmdOption4() { return cmd_option4; }
	public String getCmdOption5() { return cmd_option5; }
	public String getLogPath() { return log_path; }
	public int getLogCount() { return log_count; }
	public int getLogLevel() { return log_level; }
	public int getLogSize() { return log_size; }
	public String getOutputFormat() { return output_format; }
	public int getMaxMoves() { return maxMoves; }

	//===================================================================
	// Settors 
	//===================================================================

	public void setCmdOption(String option)
	{
		// Option hasn't been set already.
		if(!option_set)
		{
			cmd_option = option;
			option_set = true;
		}
		else
		{
			isValid = false;
		}
	}

	public void setCmdOption2(String option2)
	{
		// Option2 hasn't been set already.
		if(!option2_set)
		{
			cmd_option2 = option2;
			option2_set = true;
		}
		else
		{
			isValid = false;
		}
	}

	public void setCmdOption3(String option3)
	{
		// Option3 hasn't been set already.
		if(!option3_set)
		{
			cmd_option3 = option3;
			option3_set = true;
		}
		else
		{
			isValid = false;
		}
	}

	public void setCmdOption4(String option4)
	{
		// Option3 hasn't been set already.
		if(!option4_set)
		{
			cmd_option4 = option4;
			option4_set = true;
		}
		else
		{
			isValid = false;
		}
	}

	public void setCmdOption5(String option5)
	{
		// Option3 hasn't been set already.
		if(!option5_set)
		{
			cmd_option5 = option5;
			option5_set = true;
		}
		else
		{
			isValid = false;
		}
	}

	//===================================================================
	// functions 
	//===================================================================

	private void checkMaxMoves()
	{
		if(output_format.equals("move-queue") && maxMoves > library_maximum)
		{
			System.err.println("Invalid number of moves specified. The maximum accepted moves for a move queue file is " + library_maximum + ". " + maxMoves + " moves were specified. Setting maximum moves to library maximum.");
			maxMoves = library_maximum;

		}
	}

	public boolean checkValidInput()
	{
		// Get password if required by profile
		if(password.equals("required"))
		{
			password = ProfileManager.getPassword();
		}

		// Verify the input hasn't already failed the test with the entered command options.
		if(isValid)
		{
			// Verify if enough arguments were entered to query the library.
			// ip_address, username, and command are all required.
			if(ip_address.equals("none") || username.equals("none") || command.equals("none"))
			{
				isValid = false;
			}
		}

		return isValid;
	}

	public String formatController(String controller)
	{
		String[] location = controller.split(":");

		if(location.length==3)
		{
			return "FR" + location[0] + "/DBA" + location[1] + "/F-QIP" + location[2];
		}
		else
		{
			// Invalid controller submitted.
			System.out.println("Unable to format controller. Please use #:#:# format when describring.");
			isValid = false;
			return "none";
		}
	}

	public void loadProfile(String profile_name)
	{
		LibraryProfile profile = Load.profile(profile_name, "../profiles");

		ip_address = profile.url;
		username = profile.username;

		if(profile.password_required)
		{
			password = "required";
		}

		if(profile.http_connection)
		{
			secure_https = false;
		}	
		else if(profile.ignore_ssl_certificate)
		{
			ignore_ssl = true;
		}
	}

	public void parseArguments(String[] args)
	{
		String option;

		for(int i=0; i<args.length; i++)
		{
			switch (args[i])
			{
				case "--boolean-flag":
				case "--verify-moves":
					boolean_flag = true;
					break;
				case "-c":
				case "--command":
					if((i+1)<args.length)
					{
						// Check to see if the user requested help
						if(args[i+1].equals("help-basic"))
						{
							helpSelected = true;
							printFile("help/command-help-basic.txt");
						}
						else if(args[i+1].equals("help-advanced"))
						{
							helpSelected = true;
							printFile("help/command-help-advanced.txt");
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
					printFile("help/help.txt");
					break;
				case "--http":
				case "--insecure":
					secure_https = false;
					break;
				case "--ignore-ssl":
					ignore_ssl = true;
					break;
				case "--keep-default": // Keep default HHM values (set-hhm-threshold)
					setCmdOption2("true");
					break;
				case "-m":
				case "--max":
				case "--moves":
				case "--max-moves":
					if((i+1)<args.length)
					{
						maxMoves = Integer.parseInt(args[i+1]);
						i++;
					
						checkMaxMoves();
					}
					break;
				case "-o":
				case "--option": // generic option command (original input flag).
				case "--checksum":
				case "--drive": // specify the drive
				case "--event": // spcify HHM event type (set-hhm-threshold)
				case "--key": // option key.
				case "--package": // package name.
				case "--partition": // specify library partition.
				case "--rcm":
				case "--setting": // Specify the setting to update with update-setting
				case "--type": // specify HHM counter type. (reset-hhm-counter)
					option = "none";
					// Allow multi-word options, specifically
					// for partition names.
					while(((i+1)<args.length) && !args[i+1].substring(0,1).equals("-"))
					//if((i+1)<args.length)
					{
						if(option.equals("none"))
						{
							option = args[i+1];
						}
						else
						{
							// The %20 is required for the
							// library to properly parse
							// the text.
							option += " " + args[i+1];
						}
						i++;
					}
					setCmdOption(option);
					break;
				case "--option2": // Heading is more to categorize and organize.
				case "--barcode":
				case "--controller":
				case "--direction":
				case "--element": // Storage or EE
				case "--element-type":
				case "--email":
				case "--email-address":
				case "--qip":
				case "--save-to": // for autocreate-partition this value can be an email address or USB, so saving it to the same variable as email.
				case "--source":
				case "--spare":
				case "--subtype":
				case "--tape":
					option = "none";
					// Allow multi-word options, specifically
					// for partition names.
					while(((i+1)<args.length) && !args[i+1].substring(0,1).equals("-"))
					//if((i+1)<args.length)
					{
						if(option.equals("none"))
						{

							option = args[i+1];
							
							// Format the qip string to be the required format.
							if(args[i].equals("--controller") || args[i].equals("--qip"))
							{
								option = formatController(option);
							}
						}
						else
						{
							// The %20 is required for the
							// library to properly parse
							// the text.
							option += " " + args[i+1];
						}
						i++;
					}
					setCmdOption2(option);
					break;
				case "--option3": // Heading is more to categorize and organize.
				case "--drawer": // tap drawer to check with tap-status.
				case "--file":
				case "--file-name":
				case "--number":
				case "--number-characters":
				case "--offset": // Terapack offset.
				case "--reboot-in": // delay before restart.
				case "--robot": // Specify the TFIN robot to use.
				case "--target":
				case "--terapack":
				case "--terapacks":
				case "--value":
					option = "none";
					// Allow multi-word options, specifically
					// for partition names.
					while(((i+1)<args.length) && !args[i+1].substring(0,1).equals("-"))
					//if((i+1)<args.length)
					{
						if(option.equals("none"))
						{
							option = args[i+1];
						}
						else
						{
							// The %20 is required for the
							// library to properly parse
							// the text.
							option += " " + args[i+1];
						}
						i++;
					}
					setCmdOption3(option);
					break;
				case "--option4":
				case "--source-type":
				case "--tap":
					option = "none";
					// Allow multi-word options, specifically
					// for partition names.
					while(((i+1)<args.length) && !args[i+1].substring(0,1).equals("-"))
					//if((i+1)<args.length)
					{
						if(option.equals("none"))
						{
							option = args[i+1];
						}
						else
						{
							// The %20 is required for the
							// library to properly parse
							// the text.
							option += " " + args[i+1];
						}
						i++;
					}
					setCmdOption4(option);
					break;
				case "--option5":
				case "--delay": // delay before restart or for the orchestrator..
				case "--target-type":
				case "--timeout": // Add to help.txt when you remember what it is
					option = "none";
					// Allow multi-word options, specifically
					// for partition names.
					while(((i+1)<args.length) && !args[i+1].substring(0,1).equals("-"))
					//if((i+1)<args.length)
					{
						if(option.equals("none"))
						{
							option = args[i+1];
						}
						else
						{
							// The %20 is required for the
							// library to properly parse
							// the text.
							option += " " + args[i+1];
						}
						i++;
					}
					setCmdOption5(option);
					break;
				case "--output-format": // Change the output format.
					if((i+1)<args.length)
					{
						output_format = args[i+1];
						i++;

						checkMaxMoves();
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
				case "--profile":
					if((i+1)<args.length)
					{
						username = args[i+1];
						i++;

						loadProfile(username);
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
				case "--version":
					helpSelected = true; // No need to process further
					printFile("help/version.txt");
					break;
				case "create-profile":
					ProfileManager.createProfile("../profiles");
					helpSelected = true; // No need to process further
					break;
				case "delete-profile":
					ProfileManager.deleteProfile("../profiles");
					helpSelected = true; // No need to process further
					break;
				case "update-profile":
					ProfileManager.updateProfile("../profiles");
					helpSelected = true; // No need to process further
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
