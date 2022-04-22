//===================================================================
// CreatePartition.java
// 	Description:
// 		This is the entry point to the command/partition/* 
// 		series of commands. This command will make the calls
// 		and update commands/Advanced as necessary.
//
//		While this is a basic xml command. The complexity of the
//		code slotted it for advanced commands.
//===================================================================

package com.socialvagrancy.spectraxml.commands.partition;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.commands.sub.Drives;
import com.socialvagrancy.spectraxml.structures.Drive;
import com.socialvagrancy.spectraxml.structures.Partition;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class CreatePartition
{
	public static boolean fromFile(BasicXMLCommands library, String file_name, boolean printToShell, Logger log)
	{
		log.INFO("Opening partition file...");
		ArrayList<Partition> par_list = LoadPartition.fromFile(file_name, log);
	
		log.INFO("Loaded (" + par_list.size() + ") partitions from file.");

		if(printToShell)
		{
			System.err.println("Loaded (" + par_list.size() + ") partitions from file.");
		}

		int itr = 0;
		boolean valid_drives = true;

		log.INFO("Fetching list of drives from library...");
		XMLResult[] drive_data = library.listDrives();
		ArrayList<Drive> drive_list = Drives.parseXMLResult(drive_data);
			
		log.INFO("There are (" + drive_list.size() + ") drives in the library.");

		while(itr < par_list.size() && ValidatePartition.settings(par_list.get(itr), log))
		{
			if(par_list.get(itr).exporter_type != null && par_list.get(itr).exporter_type.equals("DRIVE"))
			{
				par_list.get(itr).exporters = FormatDriveStrings.parseDriveOptions(drive_list, par_list.get(itr).exporters, log);

				// Integrity check
				if(par_list.get(itr).exporters.substring(par_list.get(itr).exporters.length()-1, par_list.get(itr).exporters.length()).equals(",") || par_list.get(itr).exporters.equals("none") || par_list.get(itr).exporters == null)
				{
					log.ERR("Unable to use specified drives as exporters.");
					valid_drives = false;
				}	

				// Calling this again as the exporters can be reused as other drive types.
				drive_list = Drives.parseXMLResult(drive_data);
			}

			if(par_list.get(itr).drives != null)
			{
				par_list.get(itr).drives = FormatDriveStrings.parseDriveOptions(drive_list, par_list.get(itr).drives, log);
				
				// Integrity check
				if(par_list.get(itr).drives.substring(par_list.get(itr).drives.length()-1, par_list.get(itr).drives.length()).equals(",") || par_list.get(itr).drives.equals("none") || par_list.get(itr).drives == null)
				{
					log.ERR("Unable to use specified drives in partition.");
					valid_drives = false;
				}	
			}

			if(par_list.get(itr).global_spares != null)
			{
				par_list.get(itr).global_spares = FormatDriveStrings.parseDriveOptions(drive_list, par_list.get(itr).global_spares, log);
				
				// Integrity check
				if(par_list.get(itr).global_spares.substring(par_list.get(itr).global_spares.length()-1, par_list.get(itr).global_spares.length()).equals(",") || par_list.get(itr).global_spares.equals("none") || par_list.get(itr).global_spares == null)
				{
					log.ERR("Unable to use specified drives as global spare drives.");
					valid_drives = false;
				}	
			}

			if(valid_drives)
			{
				library.newPartition(URLString.get(par_list.get(itr)));
			}
			else
			{
				System.err.println("ERROR: Unable to create partition due to inability to assign specified drives.");
			}

			itr++;
		}

		return true;
	}
}
