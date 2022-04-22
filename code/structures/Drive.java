//===================================================================
// Drives.java
// 	Description:
// 		Holds the info from driveList.xml n a class form.
//===================================================================

package com.socialvagrancy.spectraxml.structures;

public class Drive
{
	public String id;
	public String status;
	public String partition = null; // could be absent.
	public String partitionDriveNumber = null; // could be absent
	public String type;
	public String connection_status;
	public String connection_host_id;
	public String connection_port_id;
	public String serial_number;
	public String manufacturer_sn;
	public String drive_firmware;
	public String dcm_firmware;
	public String wwn;
	public String fibre_address;
	public String loop_number;
	public String spared_with;
	public String spare_for;
	public String spare_potential;
	public String health;
	public String firmware_staging;
	public String firmware_staging_complete;
	public String firmware_staging_percent;
	public String firmware_staging_committed;
}
