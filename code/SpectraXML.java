//============================================================================
// SpectraXML.java
// 	Description:
// 		This is the main class in the Spectra_XML Tape Library Manager. 
//============================================================================


public class SpectraXML
{
	public static void main(String[] args)
	{
		ArgParser aparser = new ArgParser();

		if(args.length>0)
		{
			aparser.parseArguments(args);	
		}

		if(aparser.checkValidInput())
		{
			SpectraController conn = new SpectraController(aparser.getIPAddress(), false);
	
			if(conn.login(aparser.getUsername(), aparser.getPassword()))
			{
				performCommand(conn, aparser.getCommand(), aparser.getCmdOption(), aparser.getMaxMoves());
				conn.logout();
			}
			else
			{
				System.out.println("Unable to login to the Spectra Logic tape library at " + aparser.getIPAddress() + " with specified username " + aparser.getUsername() + ".");
			}
		}
		else if(!aparser.getHelpSelected())
		{
			System.out.println("Invalid options selected. Please use -h or --help to determine the commands.");
		}
	}

	public static void performCommand(SpectraController conn, String command, String option, int moves)
	{
		switch(command)
		{
			case "check-progress":
				conn.checkProgress(option, true);
				break;
			case "download-asl":
				conn.downloadASL(option, true);
				break;
			case "eject-empty-terapacks":
				conn.ejectEmpty(option, true);
				break;
			case "generate-asl":
				conn.generateASL(true);
				break;
			case "list-asls":
				conn.listASLs(true);
				break;
			case "list-inventory":
				conn.listInventory(option, true);
				break;
			case "list-partitions":
				conn.listPartitions(true);
				break;
			case "magazine-compaction":
				conn.magazineCompaction(option, moves, true);
				break;
			case "magazine-contents":
				conn.magazineContents(option, true);
				break;
			case "magazine-utilization":
				conn.magazineCapacity(option, true);
				break;
			case "partition-details":
				conn.listPartitionDetails(option, true);
				break;
			case "physical-inventory":
				conn.physicalInventory(option, true);
				break;
		}
	}
}

