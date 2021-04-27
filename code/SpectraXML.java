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
			SpectraController conn = new SpectraController(aparser.getIPAddress(), aparser.getSecureHTTPS());
	
			if(conn.login(aparser.getUsername(), aparser.getPassword()))
			{
				performCommand(conn, aparser.getCommand(), aparser.getCmdOption(), aparser.getCmdOption2(), aparser.getCmdOption3(), aparser.getMaxMoves());
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

	public static void performCommand(SpectraController conn, String command, String option, String option2, String option3, int moves)
	{
		switch(command)
		{
			case "add-key":
				conn.getXMLStatusMessage("add-key", option, option2, option3, true);
				break;
			case "audit-inventory":
				conn.getXMLStatusMessage("audit-inventory", option, option2, option3, true);
				break;
			case "audit-inventory-result":
				conn.getXMLStatusMessage("audit-inventory-result", option, option2, option3, true);
				break;
			case "check-progress":
				conn.checkProgress(option, true);
				break;
			case "create-partition-auto":
				conn.getXMLStatusMessage("create-partition-auto", option, option2, option3, true);
				break;
			case "delete-partition":
				conn.getXMLStatusMessage("delete-partition", option, option2, option3, true);
				break;
			case "disable-controller":
				conn.getXMLStatusMessage("controller-disable", option, option2, option3, true);
				break;
			case "download-asl":
				conn.downloadASL(option, true);
				break;
			case "drive-load-count":
				conn.driveLoadCount(option, true);
				break;
			case "eject-empty-terapacks":
				conn.ejectEmpty(option, true);
				break;
			case "empty-bulk":
			case "empty-bulk-tap":
				conn.getXMLStatusMessage("empty-bulk", option, option2, option3, true);
				break;
			case "enable-controller":
			case "enable-controller-failover":
				conn.getXMLStatusMessage("controller-enable", option, option2, option3, true);
				break;
			case "etherlib-status":
			case "etherLib-status":
				conn.etherLibStatus(true);
				break;
			case "firmware":
			case "firmware-versions":
				conn.listPackageFirmware(true);
				break;
			case "generate-asl":
				conn.getXMLStatusMessage("generate-asl", option, option2, option3, true);
				break;
			case "generate-drive-trace":
				conn.getXMLStatusMessage("generate-drive-trace", option, option2, option3, true);
				break;
			case "get-drive-trace":
				if(option.equals("download"))
				{
					conn.downloadDriveTrace();
				}
				else
				{
					conn.getXMLStatusMessage("download-drive-trace", option, option2, option3, true);
				}
				break;
			case "library-status":
				conn.libraryStatus(true);
				break;
			case "list-asl":
			case "list-asls":
				conn.listASLs(true);
				break;
			case "list-controller":
			case "list-controllers":
				conn.listControllers(true);
				break;
			case "list-drives":
				conn.listDrives(true);
				break;
			case "list-hhm-data":
				conn.listHHMData(true);
				break;
			case "list-inventory":
				conn.listInventory(option, true);
				break;
			case "list-mlm":
				conn.listMLMSettings(true);
				break;
			case "list-keys":
				conn.listOptionKeys(true);
				break;
			case "list-packages":
				conn.listPackages(true);
				break;
			case "list-partitions":
				conn.listPartitions(true);
				break;
			case "list-settings":
				conn.listSettings(true);
				break;
			case "package-details":
				conn.listPackageDetails(option, true);
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
			case "move-details":
				conn.libraryMoveDetails(true);
				break;
			case "move-status":
				conn.getXMLStatusMessage("move-result", option, option2, option3, true);
				break;
			case "partition-details":
				conn.listPartitionDetails(option, true);
				break;
			case "physical-inventory":
				conn.physicalInventory(option, true);
				break;
			case "rcm-status":
				conn.libraryRCMStatus(option, true);
				break;
			case "refresh-ec":
			case "refresh-ec-info":
				conn.getXMLStatusMessage("refresh-ec-info", option, option2, option3, true);
				break;
			case "refresh-env":
			case "refresh-environ":
			case "refresh-environment":
				conn.getXMLStatusMessage("refresh-environment", option, option2, option3, true);
				break;
			case "refresh-etherlib":
			case "refresh-etherLib":
				conn.getXMLStatusMessage("refresh-etherlib", option, option2, option3, true);
				break;
			case "replace-drive":
				conn.getXMLStatusMessage("replace-drive", option, option2, option3, true);
				break;
			case "reset-drive":
				conn.getXMLStatusMessage("reset-drive", option, option2, option3, true);
				break;
			case "reset-hhm-counter":
				conn.resetHHMCounter(option, option2, option3, true);
				break;
			case "resize-partition":
				conn.getXMLStatusMessage("resize-partition", option, option2, option3, true);
				break;
			case "set-hhm-threshold":
				conn.setHHMThreshold(option, option2, option3, true);
				break;
			case "set-mlm":
				conn.getXMLStatusMessage("set-mlm", option, option2, option3, true);
				break;
			case "stage-package":
				conn.getXMLStatusMessage("stage-package", option, option2, option3, true);
				break;
			case "update-package":
				conn.getXMLStatusMessage("update-package", option, option2, option3, true);
				break;
			case "update-results":
				conn.getPackageResults(true);
				break;
			case "update-setting":
				conn.getXMLStatusMessage("update-setting", option, option2, option3, true);
				break;
			case "upload-package":
				conn.uploadPackageUpdate(option3, true);
				break;
			case "tap-status":
				// Main needs to be executed twice to get both drawers.
				// Main tap is broken down into mainTop and mainBottom
				// with a drawer value of 1.
				if(option2.equals("main"))
				{
					conn.getTapState("mainTop", "1", true);
					conn.getTapState("mainBottom", "1", true);
				}
				else
				{
					conn.getTapState(option2, option3, true);
				}
				break;

		}
	}
}

