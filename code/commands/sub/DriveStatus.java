//===================================================================
// DriveStatus.java
// 	Description: This is more of a library operation script to
// 	print the partition drives in a sequential list that includes
// 	drive number, serial number, drive status (health) and if there
// 	is a barcode
//
// 	Output: drive 1: 106100540F: GREEN: 000007L8
//
// 	Functions:
// 		PUBLIC:
// 			list(BasicXMLCommands, String partition)*
//
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class DriveStatus
{
	private static ArrayList<String> buildStatusString(BasicXMLCommands library, String partition, HashMap<String, String> bcToDrive_map, Logger log)
	{
		ArrayList<String> drive_status = new ArrayList<String>();
		XMLResult[] drive_info = library.listDrives();
		String drive = "";
		String serial_number = "";
		String health = "";
		String drive_type = "";
		String status_string;
		boolean in_partition = false;

		for(int i=0; i<drive_info.length; i++)
		{
			if(drive_info[i].headerTag.equalsIgnoreCase("drive>partition") && drive_info[i].value.equals(partition))
			{
				in_partition = true;
			}

			if(in_partition && drive_info[i].headerTag.equalsIgnoreCase("drive>partitionDriveNumber"))
			{
				drive = drive_info[i].value.trim();
			}

			if(in_partition && drive_info[i].headerTag.equalsIgnoreCase("drive>serialNumber"))
			{
				serial_number = drive_info[i].value.trim();
			}

			if(in_partition && drive_info[i].headerTag.equalsIgnoreCase("drive>driveType"))
			{
				drive_type = findDriveType(drive_info[i].value);

				if(drive_type.equals("UNKNOWN"))
				{
					log.WARN("Drive type [" + drive_info[i].value + "] is UNKNOWN");
				}
			}

			if(in_partition && drive_info[i].headerTag.equalsIgnoreCase("drive>health"))
			{
				health = drive_info[i].value.trim();
			
				// Close the loop.
				in_partition = false;
				status_string = "drive " + drive + ": " + drive_type + ": " + serial_number + ": " + health + ": ";

				if(bcToDrive_map.get(drive) != null)
				{
					status_string = status_string + bcToDrive_map.get(drive);
				}

				drive_status.add(status_string);
			}

		}

		return drive_status;
	}

	public static String findDriveType(String drive_type)
	{
		switch(drive_type)
		{
			case "IBM Ultrium-TD3 Fibre":
				return "LTO-3";
			case "IBM Ultrium-TD4 Fibre":
				return "LTO-4";
			case "IBM Ultrium-TD5 Fibre":
				return "LTO-5";
			case "IBM Ultrium-TD6 Fibre":
				return "LTO-6";
			case "IBM Ultrium-TD7 Fibre":
				return "LTO-7";
			case "IBM Ultrium-TD8 Fibre":
				return "LTO-8";
			case "IBM Ultrium-TD9 Fibre":
				return "LTO-9";
			default:
				return "UNKNOWN";
		}
	}

	public static XMLResult[] list(BasicXMLCommands library, String partition, Logger log)
	{
		log.INFO("Calling mapBarcodeToDrive(library, " + partition + ")...");
		HashMap<String, String> bcToDrive_map = mapBarcodeToDrive(library, partition);

		log.INFO("Calling buildStatusString(library, " + partition + ", barcode_map)...");
	       	ArrayList<String> drive_list = buildStatusString(library, partition, bcToDrive_map, log);

		XMLResult[] messages = new XMLResult[drive_list.size()];

		for(int i=0; i<drive_list.size(); i++)
		{
			messages[i] = new XMLResult();
			messages[i].headerTag = "driveStatus";
			messages[i].value = drive_list.get(i);
		}

		return messages;
	}

	public static HashMap<String, String> mapBarcodeToDrive(BasicXMLCommands library, String partition)
	{
		HashMap<String, String> bcToDrive_map = new HashMap<String,String>();
		String drive = "";
		String barcode = "";

		XMLResult[] inventory = library.listInventory(partition);

		for(int i=0; i<inventory.length; i++)
		{
			if(inventory[i].headerTag.equalsIgnoreCase("partition>drive>offset"))
			{
				drive = inventory[i].value.trim();
			}

			if(inventory[i].headerTag.equalsIgnoreCase("partition>drive>barcode"))
			{
				barcode = inventory[i].value.trim();

				bcToDrive_map.put(drive, barcode);
			}
		}

		return bcToDrive_map;
	}
}
