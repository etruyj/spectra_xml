//============================================================================
// URLs.java
// 	Description:
// 		This class holds all the classes necessary to format URLs for
// 		the Spectra XML interface. All URLs described in the Spectra
// 		Logic's Tape XML command reference guide should be defined
// 		here.
// 		 
//============================================================================

package com.socialvagrancy.spectraxml.commands;

public class URLs
{
	private String libraryAddress;

	public URLs(String library)
	{
		libraryAddress = library;
	}

	//====================================================================
	// Get URL Functions
	// 	These functions generate the URLs used to Query the library.
	//====================================================================

	public String getASLDownloadURL(String aslName)
	{
		return libraryAddress + "autosupport.xml?action=getASL&name=" + aslName.replace(" ", "%20");
	}

	public String getASLGenerateURL()
	{
		return libraryAddress + "autosupport.xml?action=generateASL";
	}

	public String getASLNamesURL()
	{
		return libraryAddress + "autosupport.xml?action=getASLNames";
	}

	public String getASLProgressURL()
	{
		return libraryAddress + "autosupport.xml?progress";
	}

	public String getControllerDisableFailoverURL(String controller)
	{
		return libraryAddress + "controllers.xml?action=disableFailover&controller=" 
			+ controller;
	}

	public String getControllerEnableFailoverURL(String primController, String secController)
	{
		return libraryAddress + "controllers.xml?action=enableFailover&controller="
			 + primController + "&spare=" + secController;
	}

	public String getControllerListURL()
	{
		return libraryAddress + "controllers.xml?action=list";
	}

	public String getControllerProgressURL()
	{
		return libraryAddress + "controllers.xml?progress";
	}

	public String getDriveListProgressURL()
	{
		return libraryAddress + "driveList.xml?progress";
	}

	public String getDriveListURL()
	{
		return libraryAddress + "driveList.xml?action=list";
	}

	public String getDriveLoadCountURL(String drive)
	{
		return libraryAddress + "driveList.xml?action=getDriveLoadCount&driveName=" + drive;
	}

	public String getDriveTraceReplaceDriveURL(String drive)
	{
		return libraryAddress + "driveList.xml?action=prepareToReplaceDrive&driveName=" + drive;
	}

	public String getDriveTraceResetDriveURL(String drive)
	{
		return libraryAddress + "driveList.xml?action=resetDrive&driveName=" + drive;
	}

	public String getDriveTraceRetrieveTracesURL(String option, String target)
	{
		// Three options to choose from.
		// Decided to parse them to make the input easier.
		if(option.equals("download") || option.equals("usb"))
		{
			return libraryAddress + "driveList.xml?action=getDriveTraces&driveTracesGetType="
					+ option;
		}
		else if(option.equals("email"))
		{
			return libraryAddress + "driveList.xml?action=getDriveTraces&driveTracesGetType="
					+ option + "&emailAddress=" + target;
		}
		else
		{
			return "error"; // Had to have something here.
		}

	}

	public String getDriveTracesURL(String drives)
	{
		// Can specify all drives or a comma separated value of drive ids.
		return libraryAddress + "driveList.xml?action=generateDriveTraces&driveTracesDrives=" + drives;
	}

	public String getEtherLibStatusURL()
	{
		return libraryAddress + "etherLibStatus.xml?action=list";
	}

	public String getEtherLibProgressURL()
	{
		return libraryAddress + "etherLibStatus.xml?progress";
	}

	public String getEtherLibRefreshURL()
	{
		return libraryAddress + "etherLibStatus.xml?action=refresh";
	}

	public String getHHMListURL()
	{
		return libraryAddress + "HHMData.xml?action=list";
	}

	public String getHHMResetCounterURL(String type, String subtype, String robot)
	{
		// This command corresponds to the HHM: Set counters advanced utility in the BlueScale user interface.
		String url = libraryAddress + "HHMData.xml?action=resetCounterData&type=" + type.replace(" ", "%20") 
		       		+ "&subtype=" + subtype;

		// Robot value only needs to be specified for TFINITY
		if(!robot.equals("none"))
		{
			url = url + "&robot=" + robot.replace(" ", "%20");
		}	
	
		return url;
	}

	public String getHHMSetThresholdURL(String event, String keepDefault, String value)
	{
		String url = libraryAddress + "HHMData.xml?action=setThresholdData&event=" + event.replace(" ", "%20");

		if(keepDefault.equals("true"))
		{
			url = url + "&default=true";
		}
		else
		{
			url = url + "&value=" + value;
		}
		
		return url;
	}

	public String getImportExportListURL(String partition, String location, String magazine_offsets)
	{
		return libraryAddress + "mediaExchange.xml?action=prepareImportExportList&partition=" + partition.replace(" ", "%20") + "&slotType=" + location + "&TeraPackOffsets=" + magazine_offsets;
	}

	public String getInventoryAuditURL(String partition, String elementType, String offset)
	{
		// Convert the offset to 0-based.
		// The offset in the physical inventory screen is 1-based
		// The offset value required by the inventory menu is 0-based.
		// Subtract 1 from the value to convert to a valid input.
		int convertedOffset = Integer.valueOf(offset);
		convertedOffset--;

		return libraryAddress + "inventory.xml?action=audit&partition=" + partition.replace(" ", "%20") 
			+ "&elementType=" + elementType + "&TeraPackOffset=" + Integer.toString(convertedOffset);
	}

	public String getInventoryAuditResultsURL()
	{
		return libraryAddress + "inventory.xml?action=getAuditResults";
	}

	public String getInventoryListURL(String partition)
	{
		return libraryAddress + "inventory.xml?action=list&partition=" + partition.replace(" ", "%20");
	}	

	public String getInventoryMoveResultURL(String partition)
	{
		return libraryAddress + "inventory.xml?acton=getMoveResult&partition=" 
			+ partition.replace(" ", "%20");
	}

	public String getLibraryStatusURL()
	{
		return libraryAddress + "libraryStatus.xml";
	}

	public String getLibraryMoveDetailsURL()
	{
		return libraryAddress + "libraryStatus.xml?action=getMoveOperationDetails";
	}

	public String getLibraryProgressURL()
	{
		return libraryAddress + "libraryStatus.xml?progress";
	}

	public String getLibraryRCMStatusURL(String rcm)
	{
		return libraryAddress + "libraryStatus.xml?action=RCMStatus&id=" + rcm;
	}

	public String getLibraryRefreshECInfoURL()
	{
		return libraryAddress + "libraryStatus.xml?action=refreshECInfo";
	}

	public String getLibraryRefreshEnvironmentURL()
	{
		return libraryAddress + "libraryStatus.xml?action=refreshEnvironment";
	}

	public String getLibrarySettingsURL()
	{
		return libraryAddress + "librarySettings.xml?action=list";
	}

	public String getLibraryUpdateSettingURL(String setting, String value)
	{
		String url_addendum = "none";
		url_addendum = validateSetting(setting);

		if(!url_addendum.equals("none"))
		{
			return libraryAddress + "librarySettings.xml?action=set&"
				+ url_addendum + value;
		}
		else
		{
			return libraryAddress + "invalidInput.xml";
		}			
	}

	public String getLoginURL(String user, String password)
	{
		return libraryAddress + "login.xml?username=" + user 
			+ "&password=" + password;
	}

	public String getLogoutURL()
	{
		return libraryAddress + "logout.xml";
	}

	public String getMediaExchangeCleanURL(String partition, String tap)
	{
		return libraryAddress + "mediaExcahnge.xml?action=clean&partition="
			+ partition.replace(" ", "%20") + "&TAPDevice=" + tap;
	}

	public String getMediaExchangeImportExportURL(String partition, String slotType, String tap, String timeout, String terapack_list)
	{
		return libraryAddress + "mediaExchange.xml?action=importExport&partion=" 
			+ partition + "&slotType=" + slotType + "&TAPdevice=" 
			+ tap + "&timeoutInMinutes=" + timeout + "TeraPackOffsets=" 
			+ terapack_list;
	}
	
	public String getMediaExchangeTAPStateURL(String tap, String drawer)
	{
		String url = libraryAddress + "mediaExchange.xml?action=getTAPState&TAPDevice=" 
			+ tap;

		// Optional value
		if(!drawer.equals("none"))
		{
			url = url + "&drawerNumber=" + drawer;
		}

		return url;
	}

	public String getMLMSettingsListURL()
	{
		return libraryAddress + "mlmSettings.xml?action=list";
	}

	public String getMLMSettingUpdateURL(String setting, String value)
	{
		return libraryAddress + "mlmSettings.xml?action=set&" + setting + value;
	}

	public String getMoveURL(String partition, String sourceID, String sourceNumber, String destID, String destNumber)
	{
		// Generates the move URL for the XML interface.
		// parititon - the partition in which the move will occur.
		// sourceID - What type of source is being specified.
		// 		valid inputs are SLOT, EE, DRIVE,
		// 		and BC (barcode)
		// sourceNumber - How the source is identified.
		// 		Slot (offset) or barcode.
		// destID - What type of destination is being specified
		// 		valid inputs are SLOT, EE, DRIVE
		// destNumber - What is the Slot (offset) of the destination.

		return libraryAddress + "inventory.xml?action=move&partition=" + partition.replace(" ", "%20")  
			+ "&sourceID=" + sourceID + "&sourceNumber=" + sourceNumber 
			+ "&destinationID=" + destID + "&destinationNumber=" + destNumber;
	}

	public String getOptionKeyAddURL(String key)
	{
		return libraryAddress + "optionKeys.xml?action=add&key=" + key;
	}

	public String getOptionKeyListURL()
	{
		return libraryAddress + "optionKeys.xml?action=list";
	}

	public String getPackageDetailsURL(String pack)
	{
		return libraryAddress + "package.xml?action=displayPackageDetails&package=" + pack;
	}

	public String getPackageFirmwareURL()
	{
		return libraryAddress + "package.xml?action=displayCurrentFirmwareVersions";
	}

	public String getPackageListURL()
	{
		return libraryAddress + "package.xml?action=list";
	}

	public String getPackageProgressURL()
	{
		return libraryAddress + "package.xml?progress";
	}

	public String getPackageResultsURL()
	{
		return libraryAddress + "package.xml?action=getResults";
	}

	public String getPackageStageURL(String pack)
	{
		return libraryAddress + "package.xml?action=stagePackage&package=" + pack;
	}

	public String getPackageUpdateURL(String pack)
	{
		return libraryAddress + "package.xml?action=update&package="
			+ pack + "&autoFinish"; 
		// autoFinish automatically reboots the LCM and RCM at the
		// end of the package update. Without that option. The library
		// must be reset at the end with the getResults XML command.
	}

	public String getPartitionAutoCreateURL(String partition, String saveTo)
	{
		String url = libraryAddress + "partition.xml?action=autocreate&partition=" + partition.replace(" ", "%20");
	       
		if(!saveTo.equals("none"))
		{	
			url = url + "&saveLibraryConfiguration=" + saveTo;
		}
		
		return url;
	}
	
	public String getPartitionDeleteURL(String partition, String saveTo)
	{
		String url = libraryAddress + "partition.xml?action=delete&partition=" + partition.replace(" ", "%20");
		
		if(!saveTo.equals("none"))
		{	
			url = url + "&saveLibraryConfiguration=" + saveTo;
		}
		
		return url;
	}

	public String getPartitionListURL()
	{
		return libraryAddress + "partition.xml?action=list";
	}

	public String getPartitionNamesListURL()
	{
		return libraryAddress + "partitionList.xml";
	}

/*	This one needs some work.... Missing an identifier in the var declarations
 *	+ a whole lot of other logic and code required in the calling function to 
 *	get all these details..
	public String getPartitionNewURL(String partition, String type, String exporter, String exporterType, String globalSpares, String numStorageSlots, String numEESlots, String eeType, String barcodeLength, String barcodeShortenedSide, String barcodeChecksum, String, barcodeChecksumCalculated, String drive, String cleaningPartition, String enablePreScan, String enableFullScan, String enableQuickScan, String scanAfter, String includeDriveAndMediaGenerationInRES, String enableSoftLoad, String slotIQ, String enableMediaZoning, String QIPList)
	{
		String url = libraryAddress + "partition.xml?action=new";

		return url;
	}
*/
	public String getPartitionResizeSlotsURL(String partition, String type, String value)
	{
		String operation;

		// Check if added or substracted slots.
		if(value.substring(0,1).equals("-"))
		{
			operation = "&decrease=" + value.substring(1, value.length());
		}
		else
		{
			operation = "&increase=" + value;
		}

		return libraryAddress + "partition.xml?action=resize&partition=" 
			+ partition.replace(" ", "%20") + "&type=" + type + operation; 
	}

	public String getPhysicalInventoryURL(String partition)
	{
		return libraryAddress + "physInventory.xml?partition=" + partition.replace(" ", "%20");
	}

	public String getPostPackageUpdateURL()
	{
		return libraryAddress + "packageUpload.xml";
	}

	public String getPowerOffURL(String delay)
	{
		String url = libraryAddress + "powerOff.xml";

		if(!delay.equals("none"))
		{
			url = url + "?numSecondsToRemainOff=" + delay;
		}

		return url;
	}

	public String getRobotReturnFromServiceURL(String robot)
	{
		return libraryAddress + "robotService.xml?action=returnFromService&robot="
			+ robot.replace(" ", "%20");
	}

	public String getRobotSendToServiceURL(String robot)
	{
		return libraryAddress + "robotService.xml?action=sendToService&robot="
			+ robot.replace(" ", "%20");
	}

	public String getRobotUtilizationURL()
	{
		return libraryAddress + "robotUtilization.xml";
	}

	public String getSecurityAuditAbortURL()
	{
		return libraryAddress + "securityAudit.xml?action=abort";
	}

	public String getSecurityAuditStartURL()
	{
		return libraryAddress + "securityAudit.xml?action=start";
	}

	public String getSecurityAuditStatusURL()
	{
		return libraryAddress + "securityAudit.xml?acton=status";
	}

	public String getSystemMessagesURL()
	{
		return libraryAddress + "systemMessages.xml";
	}

	public String getTaskListURL()
	{
		return libraryAddress + "taskList.xml";
	}

	public String getTracesNamesURL(String traceType)
	{
		String url = libraryAddress + "traces.xml?action=";

		switch(traceType)
		{
			case "can":
			case "CAN":
				url = url + "getCanLogNames";
				break;
			case "motion":
				url = url + "getFullMotionLogNames";
				break;
			case "kernel":
				url = url + "getKernelLogNames";
				break;
			case "QIP":
			case "qip":
			case "RCM":
			case "rcm":
				url = url + "getQIPLogNames";
				break;
			case "security":
			case "security-audit":
				url = url + "getSecurityAuditLogNames";
				break;
			default:
				url = url + "none";
				break;
		}

		return url;
	}

	public String getTracesDownloadURL(String traceType, String name)
	{
		String url = libraryAddress + "traces.xml?action=";

		switch(traceType)
		{
			case "can":
			case "CAN":
				url = url + "getCanLog";
				break;
			case "motion":
				url = url + "getFullMotionLog";
				break;
			case "kernel":
				url = url + "getKernelLog";
				break;
			case "QIP":
			case "qip":
			case "RCM":
			case "rcm":
				url = url + "getQIPLog";
				break;
			case "security":
			case "security-audit":
				url = url + "getSecurityAuditLog";
				break;
			default:
				url = url + "none";
				break;
		}

		url = url + "&name=" + name;

		return url;
	}

	public String getTracesGatherURL(String traceType, String name)
	{
		String url = libraryAddress + "traces.xml?action=";

		switch(traceType)
		{
			case "motion":
				url = url + "gatherFullMotion";
				break;
			case "security":
			case "security-audit":
				url = url + "gatherSecurityAuditLog";
			default:
				url = url + "none";
				break;
		}

		url = url + "&name=" + name;

		return url;
	}

	public String getTraceTypeURL(String type)
	{
		return libraryAddress + "traces.xml?traceType=" + type;
	}

	public String getUtilsDisplayBarcodeReportingURL()
	{
		return libraryAddress + "utils.xml?action=displayBarcodeReportingSettings";
	}

	public String getUtilsDisplayTapeBarcodeVerificationURL()
	{
		return libraryAddress + "utils.xml?action=displayTapeBarcodeVerificationSetting";
	}

	public String getUtilsLockTensionRodsURL(String state)
	{
		return libraryAddress + "utils.xml?action=lockTensionRods&state=" + state;
	}

	public String getUtilsModifyBarcodeReportingURL(String checksums, String readFrom, String charactersToReport)
	{
		String url = libraryAddress + "utils.xml?action=modifyBarcodeReportingSettings";

		if(!checksums.equals("none"))
		{
			url = url + "&checksummedBehavior=" + checksums;
		}

		if(!readFrom.equals("none"))
		{
			url = url + "&directionToStartReportingCharacters=" + readFrom;
		}

		if(!charactersToReport.equals("none"))
		{
			url = url + "&maxNumberOfCharactersToReport=" + charactersToReport;
		}

		return url;
	}

	public String getUtilsModifyTapeBarcodeVerifyURL(String state)
	{
		return libraryAddress + "utils.xml?action=modifyTapeBarcodeVerificationSettings&state=" 
			+ state;
	}

	public String getUtilsProgressURL()
	{
		return libraryAddress + "utils.xml?progress";
	}

	public String getUtilsRemoveAllPartitionsURL()
	{
		return libraryAddress + "utils.xml?action=removeAlLibraryPartitions";
	}

	public String getUtilsResetControllerURL(String controller)
	{
		return libraryAddress + "utils.xml?action=resetController&id="
			+ controller;
	}

	public String getUtilsResetInventoryURL()
	{
		return libraryAddress + "utils.xml?action=resetInventory";
	}

	public String getUtilsResetLCMURL()
	{
		return libraryAddress + "utils.xml?action=resetLCM";
	}

	public String getUtilsResetRobotURL(String QIP)
	{
		String url = libraryAddress + "utils.xml?action=resetRobot";

		if(!QIP.equals("none"))
		{
			url = url + "&id=" + QIP;
		}

		return url;
	}

	public String getUtilsResetRobotCalibrationURL(String robot)
	{
		return libraryAddress + "utils.xml?action=resetRobotCalibrationSettings&robot="
			+ robot;
	}

	public String getUtilsRobotStateURL(String robot)
	{
		return libraryAddress + "utils.xml?action=saveRobotState&number=" + robot;
	}

	public String getUtilsSetSnowplowURL(String state)
	{
		return libraryAddress + "utils.xml?action=selectiveSnowplow&state=" + state;
	}

	public String getUtilsVerifyMagazineBarcodesURL()
	{
		return libraryAddress + "utils.xml?action=verifyMagazineBarcodes";
	}

	//==============================================
	// INPUT VALIDATION FUNCTIONS
	// 	Validates inputs for the URLs
	//==============================================


	private String validateSetting(String setting)
	{
		String url;

		switch(setting)
		{
			case "name":
				url = "LibraryName=";
				break;
			case "auto-logout":
				url = "autoLogoutTimoutInMinutes=";
				break;
			case "online-access":
				url = "onlineAccessEnabled=";
				break;
			case "drive-performance":
			case "monitor-drive-performance":
				url = "drivePerformanceMonitoringEnabled=";
				break;
			case "snmp":
			case "SNMP":
				url = "SNMPSettings=";
				break;
			case "auto-powerup":
				url = "automaticallyPowerUpAfterPowerFailureEnabled=";
				break;
			default:
				url = "none";
				break;
		}

		return url;
	}
}


