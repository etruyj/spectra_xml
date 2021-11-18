//============================================================================
// SpectraXML.java
// 	Description:
// 		This is the main class in the Spectra_XML Tape Library Manager. 
//============================================================================

package com.socialvagrancy.spectraxml.ui;

import com.socialvagrancy.spectraxml.utils.SpectraController;
import com.socialvagrancy.spectraxml.structures.XMLResult;

public class SpectraXML
{
	private Output output;
	private SpectraController conn;

	public SpectraXML(String ipaddress, boolean isSecure, boolean ignoreSSL)
	{
		conn = new SpectraController(ipaddress, isSecure, ignoreSSL);
		output = new Output();
	}

	public static void main(String[] args)
	{
		ArgParser aparser = new ArgParser();

		if(args.length>0)
		{
			aparser.parseArguments(args);	
		}

		if(aparser.checkValidInput())
		{
			SpectraXML ui = new SpectraXML(aparser.getIPAddress(), aparser.getSecureHTTPS(), aparser.getIgnoreSSL());	

			if(ui.login(aparser.getUsername(), aparser.getPassword()))
			{
				ui.performCommand(aparser.getCommand(), aparser.getCmdOption(), aparser.getCmdOption2(), aparser.getCmdOption3(), aparser.getCmdOption4(), aparser.getCmdOption5(), aparser.getMaxMoves(), aparser.getOutputFormat());
				ui.logout();
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

	public boolean login(String username, String password)
	{
		return conn.login(username, password);
	}

	public boolean logout()
	{
		return conn.logout();
	}

	public void performCommand(String command, String option, String option2, String option3, String option4, String option5, int moves, String output_format)
	{
		boolean printOutput = true; // by default call print output after each function call.
		boolean includeHeaders = false;
		XMLResult[] result = new XMLResult[1];

		switch(command)
		{
			case "abort-audit":
				// Abort security audit.
				result = conn.getXMLStatusMessage("abort-audit", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "add-key":
				result = conn.getXMLStatusMessage("add-key", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "audit-inventory":
				result = conn.getXMLStatusMessage("audit-inventory", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "audit-inventory-result":
				result = conn.getXMLStatusMessage("audit-inventory-result", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "audit-status":
				// Security audit status.
				result = conn.getXMLStatusMessage("audit-status", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "calibrate-drives":
				conn.calibrateDrives(option, output_format, true);
				printOutput = false;
				break;
			case "check-progress":
				result = conn.checkProgress(option);
				includeHeaders = true;
				break;
			case "create-partition-auto":
				result = conn.getXMLStatusMessage("create-partition-auto", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "delete-partition":
				result = conn.getXMLStatusMessage("delete-partition", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "disable-controller":
				result = conn.getXMLStatusMessage("controller-disable", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "display-barcode-settings":
				result = conn.displayBarcodeReporting();
				includeHeaders = true;
				break;
			case "display-tape-verification":
				result = conn.displayBarcodeVerification();
				includeHeaders = true;
				break;
			case "download-asl":
				result = conn.downloadASL(option3);
				break;
			case "download-trace":
				result = conn.downloadTrace(option, option3);
				break;
			case "drive-load-count":
				result = conn.driveLoadCount(option);
				includeHeaders = true;
				break;
			case "eject-empty":
			case "eject-empty-terapacks":
				conn.ejectEmpty(option, true);
				printOutput = false;
				break;
			case "eject-list":
			case "eject-listed":
			case "eject-listed-tapes":
				conn.ejectListed(option, option3, true);
				printOutput = false;
				break;
			case "eject-terapack":
				conn.ejectTeraPack(option, option2, option3, true);
				printOutput = false;
				break;
			case "empty-bulk":
			case "empty-bulk-tap":
				result = conn.getXMLStatusMessage("empty-bulk", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "enable-controller":
			case "enable-controller-failover":
				result = conn.getXMLStatusMessage("controller-enable", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "etherlib-status":
			case "etherLib-status":
				result = conn.etherLibStatus();
				break;
			case "firmware":
			case "firmware-versions":
				result = conn.listPackageFirmware();
				includeHeaders = true;
				break;
			case "gather-trace":
				result = conn.getXMLStatusMessage("gather-trace", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "generate-asl":
				result = conn.getXMLStatusMessage("generate-asl", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "generate-drive-trace":
				result = conn.getXMLStatusMessage("generate-drive-trace", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "get-drive-trace":
				if(option.equals("download"))
				{
					result = conn.downloadDriveTrace();
				}
				else
				{
					result = conn.getXMLStatusMessage("download-drive-trace", option, option2, option3, option4);
					includeHeaders = true;
				}
				break;
			case "get-trace-info":
				result = conn.getTraceType(option, option2);
				break;
			case "group-listed":
			case "group-listed-tapes":
				conn.groupListedTapes(option, option3, moves, output_format, true);
				printOutput = false;
				break;
			case "library-profile":
				conn.libraryProfile(true);
				printOutput = false;
				break;
			case "library-status":
				result = conn.libraryStatus();
				includeHeaders = true;
				break;
			case "library-type":
				conn.getLibraryType(true);
				break;
			case "list-asl":
			case "list-asls":
				result = conn.listASLs();
				break;
			case "list-controller":
			case "list-controllers":
				result = conn.listControllers();
				includeHeaders=true;
				break;
			case "list-drives":
				result = conn.listDrives();
				break;
			case "list-hhm-data":
				result = conn.listHHMData();
				includeHeaders = true;
				break;
			case "list-inventory":
				result = conn.listInventory(option);
				includeHeaders = true;
				break;
			case "list-mlm":
				result = conn.listMLMSettings();
				includeHeaders = true;
				break;
			case "list-keys":
				result = conn.listOptionKeys();
				includeHeaders = true;
				break;
			case "list-packages":
				result = conn.listPackages();
				includeHeaders = true;
				break;
			case "list-partitions":
				result = conn.listPartitions();
				printOutput = true;
				break;
			case "list-settings":
				result = conn.listSettings();
				includeHeaders = true;
				break;
			case "list-tasks":
				result = conn.listTasks();
				includeHeaders = true;
				break;
			case "list-traces":
				result = conn.listTraceNames(option);
				
				if(option.substring(0, 8).equals("security") || option.equals("motion"))
				{
					includeHeaders = true;
				}
				break;
			case "lock-tension-rods":
				result = conn.getXMLStatusMessage("lock-tension-rods", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "magazine-compaction":
				conn.magazineCompaction(option, moves, output_format, true);
				printOutput = false;
				break;
			case "magazine-contents":
				conn.magazineContents(option, true);
				printOutput = false;
				break;
			case "magazine-utilization":
				conn.magazineCapacity(option, true);
				printOutput = false;
				break;
			case "maintenance-hhm-reset":
				conn.maintenanceHHMReset(true);
				printOutput = false;
				break;
			case "media-exchange":
				result = conn.mediaExchange(option, option2, option3, option4, option5);
				break;
			case "modify-barcode-reporting":
				result = conn.getXMLStatusMessage("modify-barcode-reporting", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "modify-tape-verification":
				result = conn.getXMLStatusMessage("modify-tape-verification", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "move-details":
				result = conn.libraryMoveDetails();
				includeHeaders = true;
				break;
			case "move-status":
				result = conn.getXMLStatusMessage("move-result", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "move-tape":
				result = conn.moveTape(option, option4, option4, option5, option3);
				break;
			case "package-details":
				result = conn.listPackageDetails(option);
				includeHeaders = true;
				break;
			case "partition-details":
				result = conn.listPartitionDetails(option);
				includeHeaders = true;
				break;
			case "physical-inventory":
				result = conn.physicalInventory(option);
				includeHeaders = true;
				break;
			case "prepare-slotiq":
				conn.prepareSlotIQ(option, moves, output_format, true);
				break;
			case "power-off":
				result = conn.getXMLStatusMessage("power-off", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "rcm-status":
				result = conn.libraryRCMStatus(option);
				includeHeaders = true;
				break;
			case "refresh-ec":
			case "refresh-ec-info":
				result = conn.getXMLStatusMessage("refresh-ec-info", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "refresh-env":
			case "refresh-environ":
			case "refresh-environment":
				result = conn.getXMLStatusMessage("refresh-environment", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "refresh-etherlib":
			case "refresh-etherLib":
				result = conn.getXMLStatusMessage("refresh-etherlib", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "remove-all-partitions":
				result = conn.getXMLStatusMessage("remove-all-partitions", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "replace-drive":
				result = conn.getXMLStatusMessage("replace-drive", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "reset-controller":
				result = conn.getXMLStatusMessage("reset-controller", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "reset-drive":
				result = conn.getXMLStatusMessage("reset-drive", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "reset-hhm-counter":
				result = conn.resetHHMCounter(option, option2, option3);
				break;
			case "reset-inventory":
				result = conn.getXMLStatusMessage("reset-inventory", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "reset-lcm":
			case "reset-LCM":
				result = conn.getXMLStatusMessage("reset-lcm", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "reset-robot":
				result = conn.getXMLStatusMessage("reset-robot", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "reset-robot-calibration":
				result = conn.getXMLStatusMessage("reset-robot-calibration", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "resize-partition":
				result = conn.getXMLStatusMessage("resize-partition", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "return-from-service":
				result = conn.getXMLStatusMessage("return-from-service", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "robot-utilization":
				result = conn.robotUtilization();
				break;
			case "save-robot-state":
				result = conn.getXMLStatusMessage("save-robot-state", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "selective-snowplow":
				result = conn.getXMLStatusMessage("selective-snowplow", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "send-to-service":
				result = conn.getXMLStatusMessage("send-to-service", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "set-hhm-threshold":
				result = conn.setHHMThreshold(option, option2, option3);
				break;
			case "set-mlm":
				result = conn.getXMLStatusMessage("set-mlm", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "stage-package":
				result = conn.getXMLStatusMessage("stage-package", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "start-audit":
			case "security-audit":
				result = conn.getXMLStatusMessage("start-audit", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "system-messages":
				result = conn.getSystemMessages();
				includeHeaders = true;
				break;
			case "tap-status":
				// Main needs to be executed twice to get both drawers.
				// Main tap is broken down into mainTop and mainBottom
				// with a drawer value of 1.
				if(option4.equals("main"))
				{
					result = conn.getTapState("mainTop", "1");
					result = conn.getTapState("mainBottom", "1");
				}
				else
				{
					result = conn.getTapState(option4, option3);
				}
				includeHeaders = true;
				break;
			case "update-package":
				result = conn.getXMLStatusMessage("update-package", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "update-results":
				result = conn.getPackageResults();
				includeHeaders = true;
				break;
			case "update-setting":
				result = conn.getXMLStatusMessage("update-setting", option, option2, option3, option4);
				includeHeaders = true;
				break;
			case "upload-package":
				result = conn.uploadPackageUpdate(option3);
				break;
			case "verify-magazine-barcodes":
				result = conn.getXMLStatusMessage("verify-magazine-barcodes", option, option2, option3, option4);
				includeHeaders = true;
				break;
			default:
				System.out.println("Invalid command use -c help to see a list of valid commands.");
				printOutput = false;
				break;

		}

		if(printOutput)
		{
			output.print(result, output_format, includeHeaders);
		}
	}
}

