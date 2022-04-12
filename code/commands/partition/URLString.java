//===================================================================
// URLString.java
// 	Description:
// 		Formats the different new partition options into a 
// 		string to append on the end of the new partition URL.
//===================================================================

package com.socialvagrancy.spectraxml.commands.partition;

import com.socialvagrancy.spectraxml.structures.Partition;

public class URLString
{
	public static String get(Partition par)
	{
		String url;

		url = "partition=" + par.name;
		url += "&type=" + par.partition_type;
		
		// Exporters and drives
		if(par.exporter_type.equals("QIP"))
		{
			url += "&QIPExporter=" + par.exporters;
		}
		else if(par.exporter_type.equals("DRIVE"))
		{
			url += "&driveExporter=" + par.exporters;
		}

		if(par.global_spares!=null)
		{
			url += "&globalSapres=" + par.global_spares;
		}

		if(par.drives != null)
		{
			url += "&drives=" + par.drives;
		}

		// Chamber allocation
		url += "&numStorageSlots=" + String.valueOf(par.storage_slots);
		url += "&numEESlots=" + String.valueOf(par.ee_slots);
		url += "&eeType=" + par.ee_type;

		// Barcode Options
		url += "&barcodeLength=" + String.valueOf(par.barcode_length);
		url += "&barcodeShortenedSide=" + par.barcode_reporting;
		
		if(par.barcode_checksum)
		{
			url += "&barcodeChecksum=yes";
		}
		else
		{
			url += "&barcodeChecksum=no";
		}
		
		if(par.barcode_checksum_calculated)
		{
			url += "&barcodeChecksumCalculated=yes";
		}
		else
		{
			url += "&barcodeChecksumCalculated=no";
		}

		if(par.cleaning_partition!=null)
		{
			url += "&cleaningPartition=" + par.cleaning_partition;
		}

		// Advanced Features
		if(par.includeGenerationInRes)
		{
			url += "&includeDriveAndMediaGenerationInRES";
		}

		if(par.enableSoftLoad)
		{
			url += "&enableSoftLoad";
		}

		if(par.enableSlotIQ)
		{
			url += "&slotIQ";
		}

		if(par.enableMediaZoning)
		{
			url += "&enabledMediaZoning";
		}

		// Media scan options
		if(par.enable_fullscan)
		{
			url += "&enableFullscan";
		}

		if(par.scan_after!=null)
		{
			url += "&scanAfter=" + par.scan_after;
		}

		if(par.quickscan != null)
		{
			url += "&enableQuickScan=" + par.quickscan;
		}
		
		if(par.save_config != null)
		{
			url += "&saveLibraryConfiguration=" + par.save_config;
		}

		return url;
	}
}
