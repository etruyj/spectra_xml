//===================================================================
// Partition.java
//	Description: This class holds partition information for the
//	create-partition command.
//===================================================================

package com.socialvagrancy.spectraxml.structures;

public class Partition
{
	public String name=null;
	public String partition_type=null;
	public String exporter_type=null;
	public String exporters=null;
	public int storage_slots=0;
	public int ee_slots=0;
	public String ee_type = "standard";
	public String drives=null;
	public String global_spares=null;
	public String cleaning_partition=null;
	public int barcode_length = 16;
	public String barcode_reporting = "left";
	public boolean barcode_checksum = true;
	public boolean barcode_checksum_calculated = true;
	public boolean includeGenerationInRes = false;
	public boolean enableSoftLoad = false;
	public boolean enableSlotIQ = false;
	public boolean enableMediaZoning = false;
	public boolean enable_fullscan=false;
	public String quickscan=null;
	public String scan_after=null;
	public String save_config=null;
}
