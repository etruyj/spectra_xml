//===================================================================
// ValidatePartition.java
// 	Description:
// 		Validates the inputs for the create partition script
// 		to ensure they requirements are met.
//===================================================================

package com.socialvagrancy.spectraxml.commands.partition;

import com.socialvagrancy.spectraxml.structures.Partition;
import com.socialvagrancy.utils.Logger;

public class ValidatePartition
{
	private static boolean is_valid;

	public static void checkBarcodeLength(Partition par, Logger log)
	{
		if(par.barcode_length <= 0 || par.barcode_length > 16)
		{
			log.ERR("Invalid barcode length (" + par.barcode_length + ") specified.");
			System.err.println("Invalid barcode length (" + par.barcode_length + ") specified.");
			System.err.println("Barcode length must be between 1 and 16 characters.");
		
			is_valid = false;
		}
		else if(par.barcode_length < 8)
		{
			log.WARN("Short barcode length (" + par.barcode_length + ") specified. This could affect the library's ability to differentiate different tapes.");
			System.err.println("Short barcode length (" + par.barcode_length + ") specified. This could affect the library's ability to differentiate different tapes.");
		}

	}

	public static void checkBarcodeReporting(Partition par, Logger log)
	{
		// This one is complicated. The BlueScale UI is barcode reporting
		// direction, which asks which directions will the barcode be read
		// from. This XML command is barcode truncation direction, which is
		// from which side will the barcode be truncated if the barcode
		// length is too short - literally the opposite concept.
		// To keep things consistent, the value will be switched for the
		// XML output. This will allow users to input expected values.

		if(par.barcode_reporting.equalsIgnoreCase("left"))
		{
			par.barcode_reporting = "right";
		}
		else if(par.barcode_reporting.equalsIgnoreCase("right"))
		{
			par.barcode_reporting = "left";
		}
		else
		{
			log.ERR("Invalid barcode reporting direction (" + par.barcode_reporting + ").");
			System.err.println("Invalid barcode reporting direction (" + par.barcode_reporting + ").");
			System.err.println("Accepted options are left and right.");
			
			is_valid = false;
		}
	}

	public static void checkEntryExitType(Partition par, Logger log)
	{
		if(!(par.ee_type.equalsIgnoreCase("standard") || par.ee_type.equalsIgnoreCase("queued") 
			|| par.ee_type.equalsIgnoreCase("shared")))
		{
			log.ERR("Invalid EE type specified (" + par.ee_type + ").");
			System.err.println("Invalid EE type specified (" + par.ee_type + ").");
			System.err.println("Accepted values are standard, queued, and shared. Queued and shared are only valid for T120 libraries.");
		}
	}

	public static void checkEntryExitSlots(Partition par, Logger log)
	{
		if(par.ee_slots < 0)
		{
			log.ERR("Invalid number of entry/exit slots (" + par.ee_slots + ") specified.");
			System.err.println("Invalid number of entry/exit slots (" + par.ee_slots + ") specified.");
			System.err.println("Value must be 0 or greater.");
		
			is_valid = false;
		}

		int slots_per_chamber = 10;

		if(par.partition_type.equalsIgnoreCase("LTO"))
		{
			slots_per_chamber = 10;
		}
		else if(par.partition_type.equalsIgnoreCase("TS11x0"))
		{
			slots_per_chamber = 9;
		}

		if(par.ee_slots % slots_per_chamber != 0)
		{
			int overage = par.ee_slots % slots_per_chamber;
			int lower = par.ee_slots - overage;
			int upper = par.ee_slots + slots_per_chamber - overage;

			log.ERR("Incorrect number of entry/exit slots (" + par.ee_slots + ") specified. Must be in multiples of " + slots_per_chamber + ".");
			System.err.println("Incorrect number of entry/exit slots (" + par.ee_slots + ") specified. Must be in multiples of " + slots_per_chamber + ".");
			System.err.println("Did you mean to select " + lower + " or " + upper + " slots?");

			is_valid = false;
		}
	}

	public static void checkExporter(Partition par, Logger log)
	{
		if(par.exporter_type != null)
		{
			switch(par.exporter_type)
			{
				case "QIP":
				case "qip":
				case "RIM":
				case "rim":
				case "RIM2":
				case "rim2":
					par.exporter_type = "QIP";
					break;
				case "DRIVE":
				case "drive":
					par.exporter_type = "DRIVE";
					break;
				default:
					log.ERR("Invalid exporter type selected." + par.exporter_type + " is UNKNOWN");
					System.err.println("Invalid exporter type selected." + par.exporter_type + " is UNKNOWN");
					System.err.println("Valid exporter types are QIP, RIM, RIM2, and DRIVE");
					is_valid = false;
			}
		}
		else
		{
			if(!(par.partition_type.length() > 8 && par.partition_type.substring(par.partition_type.length()-8, par.partition_type.length()).equalsIgnoreCase("cleaning")))
			{
				log.ERR("No exporter type specified.");
				System.err.println("ERROR: No exporter type specified.");
				System.err.println("exporter_type QIP, RIM, RIM2, or DRIVE required for partition type " + par.partition_type);

				is_valid = false;
			}
		}
	}

	public static void checkPartitionType(Partition par, Logger log)
	{
		if(par.partition_type != null)
		{
			switch(par.partition_type)
			{
				case "LTO":
				case "lto":
					par.partition_type = "LTO";
					break;
				case "LTO CLEANING":
				case "LTO Cleaning":
				case "lto cleaning":
				case "LTO_CLEANING":
				case "LTO_Cleaning":
				case "lto_cleaning":
					par.partition_type = "LTO Cleaning";
					break;
				case "TS":
				case "ts":
				case "TS11x0":
				case "TS11X0":
				case "ts11x0":
					par.partition_type = "TS11x0";
					break;
				case "TS Cleaning":
				case "ts Cleaning":
				case "TS11x0 Cleaning":
				case "TS11X0 Cleaning":
				case "ts11x0 Cleaning":
				case "TS cleaning":
				case "ts cleaning":
				case "TS11x0 cleaning":
				case "TS11X0 cleaning":
				case "ts11x0 cleaning":
				case "TS_Cleaning":
				case "ts_Cleaning":
				case "TS11x0_Cleaning":
				case "TS11X0_Cleaning":
				case "ts11x0_Cleaning":
				case "TS_cleaning":
				case "ts_cleaning":
				case "TS11x0_cleaning":
				case "TS11X0_cleaning":
				case "ts11x0_cleaning":
					par.partition_type = "TS11x0 Cleaning";
					break;
				case "T10K":
				case "T10k":
				case "t10k":
					par.partition_type = "T10K";
					break;
				default:
					log.ERR("Invalid partition type. " + par.partition_type + " is UNKNOWN");
					System.err.println("Invalid partition type. " + par.partition_type + " is UNKNOWN");
					System.err.println("Valid partition types are LTO, LTO_Cleaning, TS11x0, TS11x0_Cleaning, and T10K");
					is_valid = false;
			}
		}
		else
		{
			log.ERR("No partition type specified.");	
			System.out.println("ERROR: No partition type specified.");	
		
			is_valid = false;
		}
	}

	public static void checkQuickscan(Partition par, Logger log)
	{
		if(par.quickscan != null)
		{
			if(par.quickscan.equalsIgnoreCase("inline") || par.quickscan.equalsIgnoreCase("inlineDrives"))
			{
				par.quickscan = "inlineDrives";
			}
			else if(par.quickscan.equalsIgnoreCase("globalSpare") || par.quickscan.equalsIgnoreCase("global_spare"))
			{
				par.quickscan = "globalSpareDrives";
			}
			else
			{
				log.ERR("Invalid quickscan option selected [" + par.quickscan + "].");
				System.err.println("Invalid quickscan option selected [" + par.quickscan + "].");
				System.err.println("Valid options are inline or global_spare");
			
				is_valid = false;
			}
		}
	}

	public static void checkScanAfter(Partition par, Logger log)
	{
		if(par.scan_after != null)
		{
			String[] options = par.scan_after.split(",");
			String formatted_scan_after = "";

			if(!(par.enable_fullscan || par.quickscan != null))
			{
				// Scan After is only required for partitions 
				// configured with fullscan or quickscan enabled.

				for(int i=0; i<options.length; i++)
				{
					if(options[i].equalsIgnoreCase("write"))
					{
						formatted_scan_after = "write";
					}
					else if(options[i].equalsIgnoreCase("read"))
					{
						formatted_scan_after = "read";
					}
					else if(options[i].substring(0, 5).equalsIgnoreCase("time:"))
					{
						formatted_scan_after = options[i];
					}
					else
					{
				
						log.ERR("Invalid scan after setting [" + options[i] + "] specified."); 
						System.err.println("Invalid scan after setting [" + options[i] + "] specified."); 
						System.err.println("Valid options are comma-separated and are read, write, time:n where n is the number of minutes until the scan.");

						is_valid = false;
					}
				}
			}
		}
	}

	public static void checkStorageSlots(Partition par, Logger log)
	{
		if(par.storage_slots < 0)
		{
			log.ERR("Invalid number of storage slots (" + par.storage_slots + ") specified.");
			System.err.println("Invalid number of storage slots (" + par.storage_slots + ") specified.");
			System.err.println("Value must be 0 or greater.");
		
			is_valid = false;
		}

		int slots_per_chamber = 10;

		if(par.partition_type.equalsIgnoreCase("LTO"))
		{
			slots_per_chamber = 10;
		}
		else if(par.partition_type.equalsIgnoreCase("TS11x0"))
		{
			slots_per_chamber = 9;
		}

		if(par.storage_slots % slots_per_chamber != 0)
		{
			int overage = par.storage_slots % slots_per_chamber;
			int lower = par.storage_slots - overage;
			int upper = par.storage_slots + slots_per_chamber - overage;

			log.ERR("Incorrect number of storage slots (" + par.storage_slots + ") specified. Must be in multiples of " + slots_per_chamber + ".");
			System.err.println("Incorrect number of storage slots (" + par.storage_slots + ") specified. Must be in multiples of " + slots_per_chamber + ".");
			System.err.println("Did you mean to select " + lower + " or " + upper + " slots?");

			is_valid = false;
		}
	}

	public static void formatQIPExporters(Partition par, Logger log)
	{
		// QIPs and RIMs are interchangable terms.
		// Mostly as RIMs have replaced QIPs, but the code hasn't
		// been updated to reflect the change.
		String[] rims = par.exporters.split(",");
		String[] rim_config;
		String formatted_rims = "";

		for(int i=0; i < rims.length; i++)
		{
			rim_config = rims[i].split(":");

			if(rim_config.length >= 3)
			{
				formatted_rims += "FR" + rim_config[0] + "/DBA" + rim_config[1] + "/F-QIP"
					+ rim_config[2];

				// Verify the port is correctly assigned.
				// Port has to be A or B
				if(!(rim_config[2].substring(rim_config[2].length()-1, rim_config[2].length()).equalsIgnoreCase("a") || rim_config[2].substring(rim_config[2].length()-1, rim_config[2].length()).equalsIgnoreCase("b"))) 
				{
					log.ERR("Invalid RIM port specified (" + rim_config[2].substring(rim_config[2].length()-1, rim_config[2].length()) + ").");
					System.err.println("Invalid RIM port specified (" + rim_config[2].substring(rim_config[2].length()-1, rim_config[2].length()) + ").");
				       	System.err.println("Must be either A or B and separated from the RIM by a semicolon.");
					is_valid = false;
				}

				if(rim_config.length > 3)
				{
					for(int j=3; j < rim_config.length; j++)
					{
						formatted_rims += ":" + rim_config[j];
					}
				}

				// Check to see if a comma is needed.
				if((i+1) < rims.length)
				{
					formatted_rims += ",";
				}
			}
			else
			{
				log.ERR("Invalid RIM configuration specified. Cannot parse " + rims[i] + ".");
				System.err.println("Invalid RIM configuration specified. Cannot parse " + rims[i] + ".");
				System.err.println("At a minimum, if the RIM is already configured, frame, DBA, and QIP id must be specified in #:#:# format.");
				is_valid = false;
			}
		}

	}

	public static boolean settings(Partition par, Logger log)
	{
		is_valid = true;

		if(par.name == null)
		{
			is_valid = false;
			log.ERR("Partition name is missing.");
			System.err.println("A partition name is required. Start the partition with the partition name encased in square brackets '[]'");
		}
		else
		{
			log.INFO("Validating settings for " + par.name);
			System.err.println("Validating settings for " + par.name);
		}

		checkPartitionType(par, log); 
		checkExporter(par, log);
		checkStorageSlots(par, log);
		checkEntryExitType(par, log);
		checkEntryExitSlots(par, log);
		checkBarcodeLength(par, log);
		checkBarcodeReporting(par, log);
		checkQuickscan(par, log);
		checkScanAfter(par, log);

		return is_valid;
	}

}
