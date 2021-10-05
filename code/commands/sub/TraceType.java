//===================================================================
// TraceType.java
// 	Description:
//		This is a breakout of the traceType XML command. The
//		options vary based the option specified and the amount
//		of space required to code this function properly pushed
//		it into it's own class.
//	
//	Functions:
//		-- formatOption(String option) : converts the option to
//			the required case-sensitive value.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class TraceType
{
	public String formatFileName(String type, String controller)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
		LocalDateTime now = LocalDateTime.now();
		
		String name;

		if(type.equals("QIP") || type.equals("QIPDump"))
		{
			name = "Full" + type + ":" + formatStripSlashes(controller) + "Log_"
				+ dtf.format(now) + ".log";
		}
		else
		{
			name = "Full" + type + "Log_" + dtf.format(now) + ".log";
		}

		return name;
	}

	public String formatStripSlashes(String controller)
	{
		// Strips the slashes from the controller name
		// otherwise the save fails as java sees the 
		// file name as a directory path.

		String[] cont = controller.split("/");

		return cont[0] + "_" + cont[1] + "_" + cont[2];
	}

	public String formatType(String option)
	{
		// The XML input is case-sensitive.
		// This function reformats the command into the 
		// required format.

		switch(option)
		{
			case "ACTION":
			case "Action":
			case "action":
				option = "Action";
				break;
			case "AUTODRIVECLEAN":
			case "AutoDriveClean":
			case "Autodriveclean":
			case "autodriveclean":
				option = "AutoDriveClean";
				break;
			case "AUTOSUPPORT":
			case "AutoSupport":
			case "Autosupport":
			case "autosupport":
				option = "AutoSupport";
				break;
			case "BACKGROUNDCLIENT":
			case "BackgroundClient":
			case "Backgroundclient":
			case "backgroundclient":
				option = "BackgroundClient";
				break;
			case "CAN":
			case "Can":
			case "can":
				option = "CAN";
				break;
			case "CONNECTION":
			case "Connection":
			case "connection":
			case "conn":
				option = "Connection";
				break;
			case "ENCRYPTION":
			case "Encryption":
			case "encryption":
				option = "Encryption";
				break;
			case "ERROR":
			case "Error":
			case "error":
				option = "Error";
				break;
			case "ETHERLIB":
			case "EtherLib":
			case "Etherlib":
			case "etherlib":
				option = "EtherLib";
				break;
			case "EVENT":
			case "Event":
			case "event":
				option = "Event";
				break;
			case "GPIO":
			case "Gpio":
			case "gpio":
				option = "GPIO";
				break;
			case "HHM":
			case "Hhm":
			case "hhm":
				option = "HHM";
				break;
			case "INITIALIZATION":
			case "Initialization":
			case "initialization":
			case "init":
				option = "Initialization";
				break;
			case "INVENTORY":
			case "Inventory":
			case "inventory":
			case "inv":
				option = "Inventory";
				break;
			case "KERNEL":
			case "Kernel":
			case "kernel":
			case "kern":
				option = "Kernel";
				break;
			case "LOCK":
			case "Lock":
			case "lock":
				option = "Lock";
				break;
			case "MESSAGE":
			case "Message":
			case "message":
				option = "Message";
				break;
			case "MLM":
			case "Mlm":
			case "mlm":
				option = "MLM";
				break;
			case "PACKAGEUPDATE":
			case "PackageUpdate":
			case "Packageupdate":
			case "packageupdate":
				option = "PackageUpdate";
				break;
			case "SECURITY":
			case "Security":
			case "security":
				option = "Security";
				break;
			case "SNMP":
			case "Snmp":
			case "snmp":
				option = "SNMP";
				break;
			case "WEBSERVER":
			case "WebServer":
			case "Webserver":
			case "webserver":
				option = "WebServer";
				break;
			case "LOGICALLIBRARY":
			case "LogicalLibrary":
			case "Logicallibrary":
			case "logicallibrary":
				option = "LogicalLibrary";
				break;
			case "MOTION":
			case "Motion":
			case "motion":
				option = "Motion";
				break;
			case "HYDRAEXIT":
			case "HydraExit":
			case "Hydraexit":
			case "hydraexit":
				option = "HydraExit";
				break;
			case "GEOMETRY":
			case "Geometry":
			case "geometry":
			case "geo":
				option = "Geometry";
				break;
			case "POOLS":
			case "Pools":
			case "pools":
			case "pool":
				option = "Pools";
				break;
			case "MOTIONINVENTORY":
			case "MotionInventory":
			case "Motioninventory":
			case "motioninventory":
				option = "MotionInventory";
				break;
			case "MOTIONRESTART1":
			case "MotionRestart1":
			case "Motionrestart1":
			case "motionrestart1":
			case "motion1":
				option = "MotionRestart1";
				break;
			case "MOTIONRESTART2":
			case "MotionRestart2":
			case "Motionrestart2":
			case "motionrestart2":
			case "motion2":
				option = "MotionRestart2";
				break;
			case "QIP":
			case "Qip":
			case "qip":
				option = "QIP";
				break;
			case "QIPDUMP":
			case "QIPdump":
			case "QipDump":
			case "Qipdump":
			case "qipdump":
				option = "QIPDump";
				break;
			default:
				option = "invalid";
				break;
		}

		return option;
	}

}
