//===================================================================
// LoadPartition.java
// 	Description:
// 		Loads the partition information from the specified
// 		file.
//===================================================================

package com.socialvagrancy.spectraxml.commands.partition;

import com.socialvagrancy.spectraxml.structures.EEType;
import com.socialvagrancy.spectraxml.structures.ExporterType;
import com.socialvagrancy.spectraxml.structures.Partition;
import com.socialvagrancy.spectraxml.structures.PartitionType;
import com.socialvagrancy.spectraxml.structures.QuickScanSetting;
import com.socialvagrancy.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadPartition
{
	public static ArrayList<Partition> fromFile(String file_path, Logger log)
	{
		ArrayList<Partition> partition_list = new ArrayList<Partition>();
		Partition par = null;
		File file = new File(file_path);

		if(file.exists())
		{
			String line = null;
			
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(file));

				while((line = br.readLine()) != null)
				{
					if(line.length() > 2 && line.substring(0,1).equals("["))
					{
						if(par != null)
						{
							partition_list.add(par);
						}	

						par = new Partition();
						par.name = line.substring(1, line.length()-1);			
					}
					else
					{
						par = parseLine(par, line, log);
					}
				}

				// Add the last partition.
				partition_list.add(par);
			}
			catch(IOException e)
			{
				log.ERR(e.getMessage());
				System.err.println(e.getMessage());
			}
		}
		else
		{
			log.ERR("File " + file_path + " does not exist.");
			System.err.println("File " + file_path + " does not exist.");
		}

		return partition_list;
	}

	private static Partition parseLine(Partition par, String line, Logger log)
	{
		String[] key_value = line.split("=");

		switch(key_value[0].trim())
		{
			case "barcode_checksum":
				par.barcode_checksum = parseBoolean(key_value[1].trim(), log);
				break;
			case "barcode_checksum_calculated":
				par.barcode_checksum_calculated = parseBoolean(key_value[1].trim(), log);
				break;
			case "barcode_length":
				par.barcode_length = Integer.valueOf(key_value[1].trim());
				break;
			case "barcode_reporting":
				par.barcode_reporting = key_value[1].trim();
				break;
			case "cleaning_partition":
				par.cleaning_partition = key_value[1].trim();
				break;
			case "drive":
			case "drives":
				par.drives = key_value[1].trim();
				break;
			case "ee_slots":
			case "ee":
				par.ee_slots = Integer.valueOf(key_value[1].trim());
				break;
			case "ee_type":
				par.ee_type = key_value[1].trim();
				break;
			case "exporter_type":
				par.exporter_type = key_value[1].trim();
				break;
			case "exporter":
			case "exporters":
				par.exporters = key_value[1].trim();
				break;
			case "fullscan":
				par.enable_fullscan = parseBoolean(key_value[1].trim(), log);
				break;
			case "global_spare":
			case "global_spares":
			case "global_spare_drives":
				par.global_spares = key_value[1].trim();
				break;
			case "include_generation":
				par.includeGenerationInRes = parseBoolean(key_value[1].trim(), log);
				break;
			case "mediaZoning":
			case "media_zoning":
			case "zoning":
				par.enableMediaZoning = parseBoolean(key_value[1].trim(), log);
				break;
			case "quickscan":
				par.quickscan = key_value[1].trim();
				break;
			case "save_config":
				par.save_config = key_value[1].trim();
				break;
			case "scan_after":
				par.scan_after = key_value[1].trim();
				break;
			case "SlotIQ":
			case "slotiq":
			case "slot_iq":
				par.enableSlotIQ = parseBoolean(key_value[1].trim(), log);
				break;
			case "SoftLoad":
			case "softload":
			case "soft_load":
				par.enableSoftLoad = parseBoolean(key_value[1].trim(), log);
				break;
			case "storage_slots":
			case "storage":
				par.storage_slots = Integer.valueOf(key_value[1].trim());
				break;
			case "type":
				par.partition_type = key_value[1].trim();
				break;
			
		}

		return par;
	}

	private static boolean parseBoolean(String type, Logger log)
	{
		switch(type)
		{
			case "True":
			case "true":
			case "Yes":
			case "yes":
			case "y":
				return true;
			case "False":
			case "false":
			case "No":
			case "n":
				return false;
			default:
				log.WARN("FullScan setting [" + type + "] is INVALID.");
				return false;
		}
	}
}
