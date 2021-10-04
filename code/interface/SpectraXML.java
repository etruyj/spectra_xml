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

	public SpectraXML(String ipaddress, boolean isSecure)
	{
		conn = new SpectraController(ipaddress, isSecure);
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
			SpectraXML ui = new SpectraXML(aparser.getIPAddress(), aparser.getSecureHTTPS());	

			if(ui.login(aparser.getUsername(), aparser.getPassword()))
			{
				ui.performCommand(aparser.getCommand(), aparser.getCmdOption(), aparser.getCmdOption2(), aparser.getCmdOption3(), aparser.getMaxMoves(), aparser.getOutputFormat());
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

	public void performCommand(String command, String option, String option2, String option3, int moves, String output_format)
	{
		boolean printOutput = true; // by default call print output after each function call.
		boolean includeHeaders = false;
		XMLResult[] result = new XMLResult[1];

		switch(command)
		{
			case "abort-audit":
				// Abort security audit.
				result = conn.getXMLStatusMessage("abort-audit", option, option2, option3);
				break;
			case "add-key":
				result = conn.getXMLStatusMessage("add-key", option, option2, option3);
				break;
			case "audit-inventory":
				result = conn.getXMLStatusMessage("audit-inventory", option, option2, option3);
				break;
			case "audit-inventory-result":
				result = conn.getXMLStatusMessage("audit-inventory-result", option, option2, option3);
				break;
			case "audit-status":
				// Security audit status.
				result = conn.getXMLStatusMessage("audit-status", option, option2, option3);
				break;
			case "check-progress":
				result = conn.checkProgress(option);
				break;
			case "create-partition-auto":
				result = conn.getXMLStatusMessage("create-partition-auto", option, option2, option3);
				break;
			case "delete-partition":
				result = conn.getXMLStatusMessage("delete-partition", option, option2, option3);
				break;
			case "disable-controller":
				result = conn.getXMLStatusMessage("controller-disable", option, option2, option3);
				break;
			case "display-barcode-settings":
				result = conn.displayBarcodeReporting();
				includeHeaders = true;
				break;
			case "display-tape-verification":
				result = conn.displayBarcodeVerification();
				break;
			case "download-asl":
				result = conn.downloadASL(option3);
				break;
			case "download-trace":
				result = conn.downloadTrace(option, option3);
				break;
			case "drive-load-count":
				result = conn.driveLoadCount(option);
				break;
			case "eject-empty-terapacks":
				conn.ejectEmpty(option, true);
				printOutput = false;
				break;
			case "empty-bulk":
			case "empty-bulk-tap":
				result = conn.getXMLStatusMessage("empty-bulk", option, option2, option3);
				break;
			case "enable-controller":
			case "enable-controller-failover":
				result = conn.getXMLStatusMessage("controller-enable", option, option2, option3);
				break;
			case "etherlib-status":
			case "etherLib-status":
				result = conn.etherLibStatus();
				break;
			case "firmware":
			case "firmware-versions":
				result = conn.listPackageFirmware();
				break;
			case "gather-trace":
				result = conn.getXMLStatusMessage("gather-trace", option, option2, option3);
				break;
			case "generate-asl":
				result = conn.getXMLStatusMessage("generate-asl", option, option2, option3);
				break;
			case "generate-drive-trace":
				result = conn.getXMLStatusMessage("generate-drive-trace", option, option2, option3);
				break;
			case "get-drive-trace":
				if(option.equals("download"))
				{
					result = conn.downloadDriveTrace();
				}
				else
				{
					result = conn.getXMLStatusMessage("download-drive-trace", option, option2, option3);
				}
				break;
			case "get-trace-info":
				conn.getTraceType(option);
				break;
			case "library-status":
				result = conn.libraryStatus();
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
				break;
			case "list-keys":
				result = conn.listOptionKeys();
				break;
			case "list-packages":
				result = conn.listPackages();
				break;
			case "list-partitions":
				result = conn.listPartitions();
				printOutput = true;
				break;
			case "list-settings":
				result = conn.listSettings();
				break;
			case "list-tasks":
				result = conn.listTasks();
				break;
			case "list-traces":
				result = conn.listTraceNames(option);
				break;
			case "lock-tension-rods":
				result = conn.getXMLStatusMessage("lock-tension-rods", option, option2, option3);
				break;
			case "magazine-compaction":
				conn.magazineCompaction(option, moves, option3, true);
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
			case "modify-barcode-reporting":
				result = conn.getXMLStatusMessage("modify-barcode-reporting", option, option2, option3);
				break;
			case "modify-tape-verification":
				result = conn.getXMLStatusMessage("modify-tape-verification", option, option2, option3);
				break;
			case "move-details":
				result = conn.libraryMoveDetails();
				break;
			case "move-status":
				result = conn.getXMLStatusMessage("move-result", option, option2, option3);
				break;
			case "package-details":
				result = conn.listPackageDetails(option);
				break;
			case "partition-details":
				result = conn.listPartitionDetails(option);
				break;
			case "physical-inventory":
				result = conn.physicalInventory(option);
				break;
			case "prepare-slotiq":
				conn.prepareSlotIQ(option, moves, option3, true);
				break;
			case "power-off":
				result = conn.getXMLStatusMessage("power-off", option, option2, option3);
				break;
			case "rcm-status":
				result = conn.libraryRCMStatus(option);
				break;
			case "refresh-ec":
			case "refresh-ec-info":
				result = conn.getXMLStatusMessage("refresh-ec-info", option, option2, option3);
				break;
			case "refresh-env":
			case "refresh-environ":
			case "refresh-environment":
				result = conn.getXMLStatusMessage("refresh-environment", option, option2, option3);
				break;
			case "refresh-etherlib":
			case "refresh-etherLib":
				result = conn.getXMLStatusMessage("refresh-etherlib", option, option2, option3);
				break;
			case "remove-all-partitions":
				result = conn.getXMLStatusMessage("remove-all-partitions", option, option2, option3);
				break;
			case "replace-drive":
				result = conn.getXMLStatusMessage("replace-drive", option, option2, option3);
				break;
			case "reset-controller":
				result = conn.getXMLStatusMessage("reset-controller", option, option2, option3);
				break;
			case "reset-drive":
				result = conn.getXMLStatusMessage("reset-drive", option, option2, option3);
				break;
			case "reset-hhm-counter":
				result = conn.resetHHMCounter(option, option2, option3);
				break;
			case "reset-inventory":
				result = conn.getXMLStatusMessage("reset-inventory", option, option2, option3);
				break;
			case "reset-lcm":
			case "reset-LCM":
				result = conn.getXMLStatusMessage("reset-lcm", option, option2, option3);
				break;
			case "reset-robot":
				result = conn.getXMLStatusMessage("reset-robot", option, option2, option3);
				break;
			case "reset-robot-calibration":
				result = conn.getXMLStatusMessage("reset-robot-calibration", option, option2, option3);
				break;
			case "resize-partition":
				result = conn.getXMLStatusMessage("resize-partition", option, option2, option3);
				break;
			case "return-from-service":
				result = conn.getXMLStatusMessage("return-from-service", option, option2, option3);
				break;
			case "robot-utilization":
				result = conn.robotUtilization();
				break;
			case "save-robot-state":
				result = conn.getXMLStatusMessage("save-robot-state", option, option2, option3);
				break;
			case "selective-snowplow":
				result = conn.getXMLStatusMessage("selective-snowplow", option, option2, option3);
				break;
			case "send-to-service":
				result = conn.getXMLStatusMessage("send-to-service", option, option2, option3);
				break;
			case "set-hhm-threshold":
				result = conn.setHHMThreshold(option, option2, option3);
				break;
			case "set-mlm":
				result = conn.getXMLStatusMessage("set-mlm", option, option2, option3);
				break;
			case "stage-package":
				result = conn.getXMLStatusMessage("stage-package", option, option2, option3);
				break;
			case "start-audit":
			case "security-audit":
				result = conn.getXMLStatusMessage("start-audit", option, option2, option3);
				break;
			case "system-messages":
				result = conn.getSystemMessages();
				break;
			case "tap-status":
				// Main needs to be executed twice to get both drawers.
				// Main tap is broken down into mainTop and mainBottom
				// with a drawer value of 1.
				if(option2.equals("main"))
				{
					result = conn.getTapState("mainTop", "1");
					result = conn.getTapState("mainBottom", "1");
				}
				else
				{
					result = conn.getTapState(option2, option3);
				}
				break;
			case "update-package":
				result = conn.getXMLStatusMessage("update-package", option, option2, option3);
				break;
			case "update-results":
				result = conn.getPackageResults();
				break;
			case "update-setting":
				result = conn.getXMLStatusMessage("update-setting", option, option2, option3);
				break;
			case "upload-package":
				result = conn.uploadPackageUpdate(option3);
				break;
			case "verify-magazine-barcodes":
				result = conn.getXMLStatusMessage("verify-magazine-barcodes", option, option2, option3);
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

