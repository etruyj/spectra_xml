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
			par.barcode_reporting("right");
		}
		else if(par.barcode_reporting.equalsIgnoreCase("right"))
		{
			par.barcode_reporting("left");
		}
		else
		{
			log.ERR("Invalid barcode reporting direction (" + par.barcode_reporting + ").");
			System.err.println("Invalid barcode reporting direction (" + par.barcode_reporting + ").");
			System.err.println("Accepted options are left and right.");
			
			is_valid = false;
		}
	}

	public static void checkExporter(Partition par, Logger log)
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

	public static void checkPartitionType(Partition par, Logger log)
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

	public static void formatQIPExporters(partition par, Logger log)
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

			if(rim_configs.length >= 3)
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
				log.ERR("Invalid RIM configuration specified. Cannot parse " + rim[i] + ".");
				System.err.println("Invalid RIM configuration specified. Cannot parse " + rim[i] + ".");
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

		checkPartitionType(par, log); 
		checkExporter(par, log);
		checkBarcodeLength(par, log);
		checkBarcodeReporting(par, log);

		return is_valid;
	}

}
