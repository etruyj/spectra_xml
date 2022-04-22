//===================================================================
// Drives.java
// 	Description:
// 		Holds commands specific to drives.
//
// 	Functions
// 		- parseDrives(XMLResult[] drives) :: converts XMLResult[]
// 			to ArrayList<Drive>
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.structures.Drive;
import com.socialvagrancy.spectraxml.structures.XMLResult;

import java.util.ArrayList;

public class Drives
{
	public static ArrayList<Drive> parseXMLResult(XMLResult[] drive_data)
	{
		ArrayList<Drive> drive_list = new ArrayList<Drive>();
		Drive drive = null;
		boolean new_drive = true;

		for(int i=0; i < drive_data.length; i++)
		{
			if(new_drive)
			{
				if(drive != null)
				{
					drive_list.add(drive);
				}

				drive = new Drive();
				new_drive = false;
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>ID"))
			{
				drive.id = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>driveStatus"))
			{
				drive.status = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>partition"))
			{
				drive.partition = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>partitionDriveNumber"))
			{
				drive.partitionDriveNumber = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>driveType"))
			{
				drive.type = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>connection>connectionStatus"))
			{
				drive.connection_status = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>connection>hostID"))
			{
				drive.connection_host_id = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>connection>portID"))
			{
				drive.connection_port_id = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>serialNumber"))
			{
				drive.serial_number = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>manufaturerSerialNumber"))
			{
				drive.manufacturer_sn = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>driveFirmware"))
			{
				drive.drive_firmware = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>dcmFirmware"))
			{
				drive.dcm_firmware = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>wwn"))
			{
				drive.wwn = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>fibreAddress"))
			{
				drive.fibre_address = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>loopNumber"))
			{
				drive.loop_number = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>sparedWith"))
			{
				drive.spared_with = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>sparedFor"))
			{
				drive.spare_for = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>sparePotential"))
			{
				drive.spare_potential = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>health"))
			{
				drive.health = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>firmwareStaging>firmware"))
			{
				drive.firmware_staging = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>firmwareStaging>complete"))
			{
				drive.firmware_staging_complete = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>firmwareStaging>percentStaged"))
			{
				drive.firmware_staging_percent = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive>firmwareStaging>committing"))
			{
				drive.firmware_staging_committed = drive_data[i].value.trim();
			}

			if(drive_data[i].headerTag.equalsIgnoreCase("drive"))
			{
				new_drive = true;
			}
		}

		drive_list.add(drive);

		return drive_list;
	}
}
