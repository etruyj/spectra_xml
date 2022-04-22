//===================================================================
// FormatDriveStrings.java
// 	Description:
// 		For use with the create-partition command.
// 		Takes the inputs that are included in the partition
// 		configuration file and converts them into drive strings.
//
// 	Inputs:
// 		- Specific Drives by position (comma-separated) 1:1:1,1:1:2
// 		- Specific Drives by id (comma-separated) 1,2
// 			Doesn't work for single drive that isn't an exporter.
// 		- Number of drives 5
// 		- Number of drives by type 5LTO-7, 2LTO-8
//===================================================================

package com.socialvagrancy.spectraxml.commands.partition;

import com.socialvagrancy.spectraxml.structures.Drive;
import com.socialvagrancy.spectraxml.structures.Partition;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.lang.StringBuilder;
import java.util.ArrayList;


public class FormatDriveStrings
{
	public static String convertDrive(String drive_type, Logger log)
	{
		if(drive_type.substring(0,3).equalsIgnoreCase("LTO"))
		{
			if(drive_type.substring(0, 4).equalsIgnoreCase("LTO-"))
			{
				return "IBM Ultrium-TD" + drive_type.substring(4, drive_type.length());
			}
			else
			{
				return "IBM Ultrium-TD" + drive_type.substring(3, drive_type.length());
			}
		}
		else
		{
			log.WARN("Drive type [" + drive_type + "] is UNKNOWN.");
			return "none";
		}
	}

	public static String parseDriveOptions(ArrayList<Drive> drive_list, String drives, Logger log)
	{
		if(drives != null)
		{
			// Determine how the drives have been formatted.
			String[] drive_splits = drives.split(",");
			String[] drive_parts = drive_splits[0].split(":");

			// More than 1 drive type is specified.
			// This can be anything but parseByNumber (x drives)
			if(drive_parts.length == 3 || drive_parts.length == 4)
			{
				// Drive position 1:1:1 or 1:1:1:a
				return parseByPosition(drive_list, drives, log);
			}
			else if(drive_parts.length == 1)
			{
				// Only 1 drive specified.
				// This could be either the number of drives or 
				// count of type.
				//
				// driveID won't work as a single instance would
				// require an exporting port 10:a to distinguish
				// it from 10 drives on port a 10a.

				// Number of drives or drive type.
				if(drive_parts[0].length() < 4)
				{
					return parseByNumber(drive_list, drives, log);
				}
				// Count of drive type
				// i.e. 5 LTO-7, 2 LTO-6
				else
				{
					return parseByTypeCount(drive_list, drives, log);
				}	
			}
			else
			{
				log.ERR("Unable to parse drive string  [" + drives + "].");
			}
		}
		else
		{
			log.WARN("No drives specified.");
		}

		return null;
	}	
	
	// Returns a string based on the number of drives specified.
	// 3 = the first 10 drives.
	public static String parseByNumber(ArrayList<Drive> drive_list, String drives, Logger log)
	{
		StringBuilder formatted_drives = new StringBuilder();
		String[] drive_parts = drives.split(":");
		int assigned = 0;

		try
		{
			int drive_count = Integer.valueOf(drive_parts[0]);

			while(assigned < drive_count && drive_list.size() > 0)
			{
				// COMMENTED OUT FOR TESTING
				// if(drive_list.get(0).partition_name != null)
				// {
				 	formatted_drives.append(drive_list.get(0).id);
					
					if(drive_parts.length == 2)
					{
						formatted_drives.append(":" + drive_parts[1]);
					}

					assigned++;

					if(assigned < drive_count)
					{
						formatted_drives.append(",");
					}
				// }

				drive_list.remove(0);
			}
		}
		catch(Exception e)
		{
			log.ERR(e.getMessage());
		}
		
		return formatted_drives.toString();
	}

	// Returns a string based on the position of drives specified.
	public static String parseByPosition(ArrayList<Drive> drive_list, String drives, Logger log)
	{
		StringBuilder formatted_drives = new StringBuilder();
		String[] split_drives = drives.split(",");
		String[] drive_parts;
		String[] drive_position;
		int assigned_drives = 0;
		int itr;
		boolean drive_found;
		
		for(int i=0; i < split_drives.length; i++)
		{
			drive_parts = split_drives[i].split(":");
			itr = 0;
			drive_found = false;

			if(drive_parts.length < 3)
			{
				log.ERR("Unable to parse drive string [" + split_drives[i] + "]");
			}
			else
			{
				while(assigned_drives < split_drives.length && drive_list.size() > 0
						&& itr < drive_list.size() && !drive_found)
				{
					drive_position = drive_list.get(itr).id.split("/");
					
					for(int j=0; j< drive_position.length; j++)
					{
						drive_position[j] = drive_position[j].substring(drive_position[j].length()-drive_parts[j].length(), drive_position[j].length());
					}
					
					// Check for positions to match
					// Each of the three addresses must match.
					if(drive_parts[0].equals(drive_position[0]) && drive_parts[1].equals(drive_position[1]) && drive_parts[2].equals(drive_position[2])) 
					{
						if(drive_list.get(itr).partition == null || true)
						{
							formatted_drives.append(drive_list.get(itr).id);
							assigned_drives++;
							drive_found = true;

							if(drive_parts.length > 3)
							{
								formatted_drives.append(":" + drive_parts[3]);
							}
						
							if(assigned_drives < split_drives.length)
							{
								formatted_drives.append(",");
							}

						}
						else
						{
							log.ERR("Drive " + drive_list.get(itr).id + " is already assigned to partition " + drive_list.get(itr).partition);
						}
						// Next drive by removal from list.
						drive_list.remove(itr);
					}
					else
					{
						// Next drive by incrementation
						itr++;
					}

				}

			}
		}
		
		return formatted_drives.toString();	
	}

	// Return string based on n LTO-# strings.
	public static String parseByTypeCount(ArrayList<Drive> drive_list, String drives, Logger log) 
	{
		StringBuilder formatted_drives = new StringBuilder();
		String[] drive_parts = drives.split(",");
		String[] drive_count;
		int itr;
		int assigned_drives = 0;

		// Track whether each sections of drives was found.
		// The assigned drives only catches 'n LTO-#' not
		// subsequent number if specified.
		// Drives found will catch that.
		boolean drives_found = false;

		for(int i=0; i<drive_parts.length; i++)
		{
			itr = 0;
			drive_count = drive_parts[i].split(" ");
			drives_found = false;

			drive_count[1] = convertDrive(drive_count[1], log);

			while(assigned_drives < Integer.valueOf(drive_count[0]) && itr < drive_list.size() && drive_list.size() > 0)
			{
				if(drive_list.get(itr).type.substring(0, drive_count[1].length()).equals(drive_count[1]))
				{
					formatted_drives.append(drive_list.get(itr).id);
					
					assigned_drives++;
					drives_found = true;

					if(assigned_drives < Integer.valueOf(drive_count[0]))
					{
						formatted_drives.append(",");
					}
					
					
					//Increment by removal
					drive_list.remove(itr);
				}
				else
				{
					// Increment by incrementing incrementor.
					itr++;
				}
			}
		}	
	
		if(!drives_found)
		{
			formatted_drives.append(",");
		}

		return formatted_drives.toString();
	}
}
