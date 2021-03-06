//============================================================================
// SpectraController.java
// 	Description:
// 		This class makes all the appropriate function calls to the tape
// 		library. 
//============================================================================

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectraController
{
	private Connector cxn;
	private String libraryAddress;
	private Logger log;

	//====================================================================
	// Constructor
	//====================================================================
	
	public SpectraController(String server, boolean secure)
	{
		// Declared logger in SpectraController as opposed to 
		// in connector to allow logging of issues within the commands.
		log = new Logger("../logs/slxml-main.log", 102400, 3, 1);
		cxn = new Connector(log);
		
		if(secure)
		{
			libraryAddress = "https://";
		}
		else
		{
			libraryAddress = "http://";
		}
		
		libraryAddress = libraryAddress + server + "/gf/";

	}

	//====================================================================
	// Get URL Functions
	// 	These functions generate the URLs used to Query the library.
	//====================================================================

	private String getASLDownloadURL(String aslName)
	{
		return libraryAddress + "autosupport.xml?action=getASL&name=" + aslName.replace(" ", "%20");
	}

	private String getASLGenerateURL()
	{
		return libraryAddress + "autosupport.xml?action=generateASL";
	}

	private String getASLNamesURL()
	{
		return libraryAddress + "autosupport.xml?action=getASLNames";
	}

	private String getASLProgressURL()
	{
		return libraryAddress + "autosupport.xml?progress";
	}

	private String getControllerDisableFailoverURL(String controller)
	{
		return libraryAddress + "controllers.xml?action=disableFailover&controller=" 
			+ controller;
	}

	private String getControllerEnableFailoverURL(String primController, String secController)
	{
		return libraryAddress + "controllers.xml?action=enableFailover&controller="
			 + primController + "&spare=" + secController;
	}

	private String getControllerListURL()
	{
		return libraryAddress + "controllers.xml?action=list";
	}

	private String getControllerProgressURL()
	{
		return libraryAddress + "controllers.xml?progress";
	}

	private String getDriveListProgressURL()
	{
		return libraryAddress + "driveList.xml?progress";
	}

	private String getDriveListURL()
	{
		return libraryAddress + "driveList.xml?action=list";
	}

	private String getDriveLoadCountURL(String drive)
	{
		return libraryAddress + "driveList.xml?action=getDriveLoadCount&driveName=" + drive;
	}

	private String getDriveTraceReplaceDriveURL(String drive)
	{
		return libraryAddress + "driveList.xml?action=prepareToReplaceDrive&driveName=" + drive;
	}

	private String getDriveTraceResetDriveURL(String drive)
	{
		return libraryAddress + "driveList.xml?action=resetDrive&driveName=" + drive;
	}

	private String getDriveTraceRetrieveTracesURL(String option, String target)
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

	private String getDriveTracesURL(String drives)
	{
		// Can specify all drives or a comma separated value of drive ids.
		return libraryAddress + "driveList.xml?action=generateDriveTraces&driveTracesDrives=" + drives;
	}

	private String getEtherLibStatusURL()
	{
		return libraryAddress + "etherLibStatus.xml?action=list";
	}

	private String getEtherLibProgressURL()
	{
		return libraryAddress + "etherLibStatus.xml?progress";
	}

	private String getEtherLibRefreshURL()
	{
		return libraryAddress + "etherLibStatus.xml?action=refresh";
	}

	private String getHHMListURL()
	{
		return libraryAddress + "HHMData.xml?action=list";
	}

	private String getHHMResetCounterURL(String type, String subtype, String robot)
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

	private String getHHMSetThresholdURL(String event, String keepDefault, String value)
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

	private String getImportExportListURL(String partition, String location, String magazine_offsets)
	{
		return libraryAddress + "mediaExchange.xml?action=prepareImportExportList&partition=" + partition.replace(" ", "%20") + "&slotType=" + location + "&TeraPackOffsets=" + magazine_offsets;
	}

	private String getInventoryAuditURL(String partition, String elementType, String offset)
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

	private String getInventoryAuditResultsURL()
	{
		return libraryAddress + "inventory.xml?action=getAuditResults";
	}

	private String getInventoryListURL(String partition)
	{
		return libraryAddress + "inventory.xml?action=list&partition=" + partition.replace(" ", "%20");
	}	

	private String getInventoryMoveResultURL(String partition)
	{
		return libraryAddress + "inventory.xml?acton=getMoveResult&partition=" 
			+ partition.replace(" ", "%20");
	}

	private String getLibraryStatusURL()
	{
		return libraryAddress + "libraryStatus.xml";
	}

	private String getLibraryMoveDetailsURL()
	{
		return libraryAddress + "libraryStatus.xml?action=getMoveOperationDetails";
	}

	private String getLibraryProgressURL()
	{
		return libraryAddress + "libraryStatus.xml?progress";
	}

	private String getLibraryRCMStatusURL(String rcm)
	{
		return libraryAddress + "libraryStatus.xml?action=RCMStatus&id=" + rcm;
	}

	private String getLibraryRefreshECInfoURL()
	{
		return libraryAddress + "libraryStatus.xml?action=refreshECInfo";
	}

	private String getLibraryRefreshEnvironmentURL()
	{
		return libraryAddress + "libraryStatus.xml?action=refreshEnvironment";
	}

	private String getLibrarySettingsURL()
	{
		return libraryAddress + "librarySettings.xml?action=list";
	}

	private String getLibraryUpdateSettingURL(String setting, String value)
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

	private String getLoginURL(String user, String password)
	{
		return libraryAddress + "login.xml?username=" + user 
			+ "&password=" + password;
	}

	private String getLogoutURL()
	{
		return libraryAddress + "logout.xml";
	}

	private String getMediaExchangeCleanURL(String partition, String tap)
	{
		return libraryAddress + "mediaExcahnge.xml?action=clean&partition="
			+ partition.replace(" ", "%20") + "&TAPDevice=" + tap;
	}

	private String getMediaExchangeTAPStateURL(String tap, String drawer)
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

	private String getMLMSettingsListURL()
	{
		return libraryAddress + "mlmSettings.xml?action=list";
	}

	private String getMLMSettingUpdateURL(String setting, String value)
	{
		return libraryAddress + "mlmSettings.xml?action=set&" + setting + value;
	}

	private String getMoveURL(String partition, String sourceID, String sourceNumber, String destID, String destNumber)
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

	private String getOptionKeyAddURL(String key)
	{
		return libraryAddress + "optionKeys.xml?action=add&key=" + key;
	}

	private String getOptionKeyListURL()
	{
		return libraryAddress + "optionKeys.xml?action=list";
	}

	private String getPackageDetailsURL(String pack)
	{
		return libraryAddress + "package.xml?action=displayPackageDetails&package=" + pack;
	}

	private String getPackageFirmwareURL()
	{
		return libraryAddress + "package.xml?action=displayCurrentFirmwareVersions";
	}

	private String getPackageListURL()
	{
		return libraryAddress + "package.xml?action=list";
	}

	private String getPackageProgressURL()
	{
		return libraryAddress + "package.xml?progress";
	}

	private String getPackageResultsURL()
	{
		return libraryAddress + "package.xml?action=getResults";
	}

	private String getPackageStageURL(String pack)
	{
		return libraryAddress + "package.xml?action=stagePackage&package=" + pack;
	}

	private String getPackageUpdateURL(String pack)
	{
		return libraryAddress + "package.xml?action=update&package="
			+ pack + "&autoFinish"; 
		// autoFinish automatically reboots the LCM and RCM at the
		// end of the package update. Without that option. The library
		// must be reset at the end with the getResults XML command.
	}

	private String getPartitionAutoCreateURL(String partition, String saveTo)
	{
		String url = libraryAddress + "partition.xml?action=autocreate&partition=" + partition.replace(" ", "%20");
	       
		if(!saveTo.equals("none"))
		{	
			url = url + "&saveLibraryConfiguration=" + saveTo;
		}
		
		return url;
	}
	
	private String getPartitionDeleteURL(String partition, String saveTo)
	{
		String url = libraryAddress + "partition.xml?action=delete&partition=" + partition.replace(" ", "%20");
		
		if(!saveTo.equals("none"))
		{	
			url = url + "&saveLibraryConfiguration=" + saveTo;
		}
		
		return url;
	}

	private String getPartitionListURL()
	{
		return libraryAddress + "partition.xml?action=list";
	}

	private String getPartitionNamesListURL()
	{
		return libraryAddress + "partitionList.xml";
	}

/*	This one needs some work.... Missing an identifier in the var declarations
 *	+ a whole lot of other logic and code required in the calling function to 
 *	get all these details..
	private String getPartitionNewURL(String partition, String type, String exporter, String exporterType, String globalSpares, String numStorageSlots, String numEESlots, String eeType, String barcodeLength, String barcodeShortenedSide, String barcodeChecksum, String, barcodeChecksumCalculated, String drive, String cleaningPartition, String enablePreScan, String enableFullScan, String enableQuickScan, String scanAfter, String includeDriveAndMediaGenerationInRES, String enableSoftLoad, String slotIQ, String enableMediaZoning, String QIPList)
	{
		String url = libraryAddress + "partition.xml?action=new";

		return url;
	}
*/
	private String getPartitionResizeSlotsURL(String partition, String type, String value)
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
			+ partition + "&type=" + type + operation; 
	}

	private String getPhysicalInventoryURL(String partition)
	{
		return libraryAddress + "physInventory.xml?partition=" + partition.replace(" ", "%20");
	}

	private String getPostPackageUpdateURL()
	{
		return libraryAddress + "packageUpload.xml";
	}

	private String getPowerOffURL(String delay)
	{
		String url = libraryAddress + "powerOff.xml";

		if(!delay.equals("none"))
		{
			url = url + "?numSecondsToRemainOff=" + delay;
		}

		return url;
	}

	private String getRobotReturnFromServiceURL(String robot)
	{
		return libraryAddress + "robotService.xml?action=returnFromService&robot="
			+ robot.replace(" ", "%20");
	}

	private String getRobotSendToServiceURL(String robot)
	{
		return libraryAddress + "robotService.xml?action=sendToService&robot="
			+ robot.replace(" ", "%20");
	}

	private String getRobotUtilizationURL()
	{
		return libraryAddress + "robotUtilization.xml";
	}

	private String getSecurityAuditAbortURL()
	{
		return libraryAddress + "securityAudit.xml?action=abort";
	}

	private String getSecurityAuditStartURL()
	{
		return libraryAddress + "securityAudit.xml?action=start";
	}

	private String getSecurityAuditStatusURL()
	{
		return libraryAddress + "securityAudit.xml?acton=status";
	}

	private String getSystemMessagesURL()
	{
		return libraryAddress + "systemMessages.xml";
	}

	private String getTaskListURL()
	{
		return libraryAddress + "taskList.xml";
	}

	private String getTracesNamesURL(String traceType)
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

	private String getTracesDownloadURL(String traceType, String name)
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

	private String getTracesGatherURL(String traceType, String name)
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

	private String getTraceTypeURL(String type)
	{
		return libraryAddress + "traces.xml?traceType=" + type;
	}

	private String getUtilsDisplayBarcodeReportingURL()
	{
		return libraryAddress + "utils.xml?action=displayBarcodeReportingSettings";
	}

	private String getUtilsDisplayTapeBarcodeVerificationURL()
	{
		return libraryAddress + "utils.xml?action=displayTapeBarcodeVerificationSetting";
	}

	private String getUtilsLockTensionRodsURL(String state)
	{
		return libraryAddress + "utils.xml?action=lockTensionRods&state=" + state;
	}

	private String getUtilsModifyBarcodeReportingURL(String checksums, String readFrom, String charactersToReport)
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

	private String getUtilsModifyTapeBarcodeVerifyURL(String state)
	{
		return libraryAddress + "utils.xml?action=modifyTapeBarcodeVerificationSettings&state=" 
			+ state;
	}

	private String getUtilsProgressURL()
	{
		return libraryAddress + "utils.xml?progress";
	}

	private String getUtilsRemoveAllPartitionsURL()
	{
		return libraryAddress + "utils.xml?action=removeAlLibraryPartitions";
	}

	private String getUtilsResetControllerURL(String controller)
	{
		return libraryAddress + "utils.xml?action=resetController&id="
			+ controller;
	}

	private String getUtilsResetInventoryURL()
	{
		return libraryAddress + "utils.xml?action=resetInventory";
	}

	private String getUtilsResetLCMURL()
	{
		return libraryAddress + "utils.xml?action=resetLCM";
	}

	private String getUtilsResetRobotURL(String QIP)
	{
		String url = libraryAddress + "utils.xml?action=resetRobot";

		if(!QIP.equals("none"))
		{
			url = url + "&id=" + QIP;
		}

		return url;
	}

	private String getUtilsResetRobotCalibrationURL(String robot)
	{
		return libraryAddress + "utils.xml?action=resetRobotCalibrationSettings&robot="
			+ robot;
	}

	private String getUtilsRobotStateURL(String robot)
	{
		return libraryAddress + "utils.xml?action=saveRobotState&number=" + robot;
	}

	private String getUtilsSetSnowplowURL(String state)
	{
		return libraryAddress + "utils.xml?action=selectiveSnowplow&state=" + state;
	}

	private String getUtilsVerifyMagazineBarcodesURL()
	{
		return libraryAddress + "utils.xml?action=verifyMagazineBarcodes";
	}

	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================

	public boolean checkProgress(String operationName, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status"};

		String url = "none";

		switch(operationName)
		{
			case "ASL":
			case "asl":
				url = getASLProgressURL();
				break;
			case "controller":
				url = getControllerProgressURL();
				break;
			case "drive":
			case "driveList":
			case "drive-list":
				url = getDriveListProgressURL();
				break;
			case "etherlib":
			case "etherLib":
				url = getEtherLibProgressURL();
				break;
			case "library":
			case "library-refresh":
				url = getLibraryProgressURL();
				break;
			case "package":
			case "package-update":
				url = getPackageProgressURL();
				break;
			case "utils":
				url = getUtilsProgressURL();
				break;
		}

		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(response.length>0)
		{

			if(printToShell)
			{
				printOutput(response, "none", true);
			}

			if(response[0].value.equalsIgnoreCase("OK"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else // Something went wrong.
		{
			return false;
		}
	}

	public int countMagazines(String partition)
	{
		// Queries the library for 
		// Counts the number of magazines in the partition.

		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"offset"};

		String url = getPhysicalInventoryURL(partition);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(response[0].value.equals("no results"))
		{
			return 0;
		}
		else
		{
			return response.length;
		}
	}

	public XMLResult[] displayBarcodeReporting(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"checksummedBehavoir",
					"directionToStartReportingCharacters",
					"maxNumberOfCharacters"};

		String url = getUtilsDisplayBarcodeReportingURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}

	public XMLResult[] displayBarcodeVerification(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"state"};

		String url = getUtilsDisplayTapeBarcodeVerificationURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", false);
		}

		return response;
	}

	public void driveLoadCount(String option, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"loadCount"};

		String url = getDriveLoadCountURL(option);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}
	}

	public void downloadASL(String aslName, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status","message"};

		String url = getASLDownloadURL(aslName);
		
		cxn.downloadFromLibrary(url, "../output/", aslName);
	}

	public void downloadDriveTrace()
	{
		String url = getDriveTraceRetrieveTracesURL("download", "none");
		cxn.downloadFromLibrary(url, "../output/", "drive_traces.zip");	
	}

	public void downloadTrace(String traceType, String name)
	{
		String url = getTracesDownloadURL(traceType, name);
		cxn.downloadFromLibrary(url, "../output/", name);
	}

	public void ejectEmpty(String partition, boolean printToShell)
	{
		// ejectEmpty
		//  Prepares an export list of empty terapacks in the 
		//  storage partition and uploads it into the library.
		//  The actual eject must be done from the library's
		//  front panel.

		int emptyTeraPacks = 0;
		int tempOffset = 0;
		StringBuilder offset = new StringBuilder();
		TeraPack[] magazines = magazineContents(partition, false);
		XMLResult[] response = new XMLResult[1];
		String url = "none";

		for(int i=0; i<magazines.length; i++)
		{
			if(magazines[i].getLocation().equalsIgnoreCase("storage") && magazines[i].getCapacity() == 0)
			{
				// Fun fact, the offset returned by XML is base 1.
				// The offset used by XML for identifying TeraPacks is
				// base 0.
				// All offsets must have -1 applied to reference the
				// correct TeraPack.
				tempOffset = Integer.parseInt(magazines[i].getOffset());
				tempOffset--; 
				
				offset.append(Integer.toString(tempOffset) + ",");
				emptyTeraPacks++;
			}


		}
			
		if(emptyTeraPacks>0)
		{
			// Shave off the last comma.
			offset.setLength(offset.length()-1);
		
			String xmlOutput;

			XMLParser xmlparser = new XMLParser();
			String[] searchTerms = {"status", "message"};

			url = getImportExportListURL(partition, "storage", offset.toString());
			xmlOutput = cxn.queryLibrary(url);

			xmlparser.setXML(xmlOutput);
			response = xmlparser.parseXML(searchTerms);
		}

		if(printToShell)
		{
			if(emptyTeraPacks>0)
			{
				for(int i=0; i<response.length; i++)
				{
					System.out.println(response[i].value);
				}
				
				if(response[0].value.equalsIgnoreCase("OK"))
				{
					System.out.println("To export the specified TeraPacks, please log into the front panel of your Spectra tape library and access the Advanced Import/Export menu. Press the Populate button to load the moves and click Start Moves.");
				}
			}
			else
			{
				System.out.println("There are no empty TeraPacks in the storage chambers to export.");
			}
		}
	}

	public void etherLibStatus(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"component",
					"ID",
					"connection",
					"target",
					"connected"};

		String url = getEtherLibStatusURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "component", true);
		}
	}

	public void generateASL(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;
		
		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status", "message"};
		
		String url = getASLGenerateURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

	}

	public XMLResult[] getPackageResults(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"message",
					"updateResults",
					"packageName",
					"component",
					"name",
					"previousVersion",
					"updatedVersion",
					"updateStatus",
					"rebootInProgress"};

		String url = getPackageResultsURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}

	public XMLResult[] getSystemMessages(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"message",
					"number",
					"severity",
					"date",
					"time",
					"notification",
					"remedy",
					"month",
					"day",
					"year",
					"hour",
					"minute",
					"second"};

		String url = getSystemMessagesURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "message", true);
		}

		return response;
	}

	public void getTapState(String tap, String drawer, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"doorOpen",
					"magazinePresent",
					"magazineSeated",
					"magazineType",
					"rotaryPosition"};

		tap = convertTAPString(tap);
		String url = getMediaExchangeTAPStateURL(tap, drawer);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}
		
	}

	public void getTraceType(String type, boolean printToShell)
	{
		String stringOutput;
		String url = getTraceTypeURL(type);

		stringOutput = cxn.queryLibrary(url);

		if(printToShell)
		{
			System.out.println(stringOutput);
		}
	}

	public void getXMLStatusMessage(String query, String option1, String option2, String option3, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;
		
		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status", "message"};
	
		String url = "none";

		switch (query)
		{
			case "abort-audit":
				// Security audit not inventory audit.
				url = getSecurityAuditAbortURL();
				break;
			case "add-key":
				url = getOptionKeyAddURL(option1);
				break;
			case "audit-inventory":
				url = getInventoryAuditURL(option1, option2, option3);
				break;
			case "audit-inventory-result":
				url = getInventoryAuditResultsURL();
				break;
			case "audit-status":
				// Security audit, not inventory audit.
				url = getSecurityAuditStatusURL();
				break;
			case "controller-disable":
				url = getControllerDisableFailoverURL(option1);
				break;		
			case "controller-enable":
				url = getControllerEnableFailoverURL(option1, option2);
				break;
			case "create-partition-auto":
				url = getPartitionAutoCreateURL(option1, option2);
				break;
			case "delete-partition":
				url = getPartitionDeleteURL(option1, option2);
				break;
			case "download-drive-trace":
				url = getDriveTraceRetrieveTracesURL(option1, option2);
			case "empty-bulk-tap":
				// clean up the option values.
				option2 = convertTAPString(option2);
				url = getMediaExchangeCleanURL(option1, option2);
				break;
			case "gather-trace":
				url = getTracesGatherURL(option1, option3);
				break;
			case "generate-asl":
				url = getASLGenerateURL();
				break;
			case "generate-drive-trace":
				url = getDriveTracesURL(option1);
				break;
			case "lock-tension-rods":
				url = getUtilsLockTensionRodsURL(option1);
				break;
			case "modify-barcode-settings":
				url = getUtilsModifyBarcodeReportingURL(option1, option2, option3);
				break;
			case "modify-tape-verification":
				url = getUtilsModifyTapeBarcodeVerifyURL(option1);
				break;
			case "move-result":
				url = getInventoryMoveResultURL(option1);
				break;
			case "power-off":
				url = getPowerOffURL(option3);
				break;
			case "refresh-ec-info":
				url = getLibraryRefreshECInfoURL();
				break;
			case "refresh-environment":
				url = getLibraryRefreshEnvironmentURL();
				break;
			case "refresh-etherlib":
				url = getEtherLibRefreshURL();
				break;
			case "remove-all-partitions":
				url = getUtilsRemoveAllPartitionsURL();
				break;
			case "replace-drive":
				url = getDriveTraceReplaceDriveURL(option1);
				break;
			case "reset-controller":
				url = getUtilsResetControllerURL(option1);
				break;
			case "reset-drive":
				url = getDriveTraceResetDriveURL(option1);
				break;
			case "reset-inventory":
				url = getUtilsResetInventoryURL();
				break;
			case "reset-robot":
				url = getUtilsResetRobotURL(option1);
				break;
			case "reset-robot-calibration":
				url = getUtilsResetRobotCalibrationURL(option3);
				break;
			case "resize-partition":
				url = getPartitionResizeSlotsURL(option1, option2, option3);
				break;
			case "return-from-service":
				url = getRobotReturnFromServiceURL(option3);
				break;
			case "save-robot-state":
				url = getUtilsRobotStateURL(option3);
				break;
			case "selective-snowplow":
				url = getUtilsSetSnowplowURL(option1);
				break;
			case "send-to-service":
				url = getRobotSendToServiceURL(option3);
				break;
			case "set-mlm":
				option1 = convertMLMSetting(option1);
				url = getMLMSettingUpdateURL(option1, option3);
				break;
			case "stage-package":
				url = getPackageStageURL(option1);
				break;
			case "start-audit":
				// security audit, not inventory audit.
				url = getSecurityAuditStartURL();
				break;
			case "update-package":
				url = getPackageUpdateURL(option1);
				break;
			case "update-setting":
				url = getLibraryUpdateSettingURL(option1, option3);
				break;
			case "verify-mag-barcodes":
				url = getUtilsVerifyMagazineBarcodesURL();
				break;
		}	
		
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}


	}

	public XMLResult[] libraryMoveDetails(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"move",
					"startTime",
					"endTime",
					"overallStatus",
					"overallSense",
					"source",
					"destination",
					"firstRobot",
					"secondRobot",
					"partition",
					"elementType",
					"elementOffset",
					"number",
					"lastOperation"};

		String url = getLibraryMoveDetailsURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "move", true);
		}

		return response;
	}

	public XMLResult[] libraryRCMStatus(String rcm, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"RCMStatus",
					"ID",
					"overallStatus",
					"loglibStatus",
					"motionStatus",
					"repeaterStatus"};

		String url = getLibraryRCMStatusURL(rcm);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "RCMStatus", true);
		}

		return response;	
	}

	public XMLResult[] libraryStatus(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"libraryType",
					"libraryUpTimeSeconds",
					"maintenanceMode",
					"frames",
					"railPowerOn",
					"robot",
					"controllerEnvironmentInfo",
					"controller",
					"driveControlModule",
					"powerSupplyFRU",
					"powerControlModule",
					"fanControlModule",
					"frameManagementModule",
					"serviceBayControlModule",
					"ECInfo",
					"SDInfo",
					"componenent",
					"chassisCount",
					"physicalFrame",
					"framePosition",
					"frameType",
					"state",
					"transporterType",
					"topHAXGear",
					"bottomHAXGear",
					"topHAXSolenoid",
					"bottomHAXSolenoid",
					"ID",
					"temperatureInCelsius",
					"portALinkUp",
					"portBLinkUp",
					"failoverStatus",
					"type",
					"fwVersion",
					"twelveVoltVoltage",
					"fiveVoltVoltage",
					"fanCurrentInAmps",
					"isFullHeight",
					"driveFwVersion",
					"inputPowerOkay",
					"outputPowerOkay",
					"temperatureWarning",
					"temperatureAlarm",
					"modelNumber",
					"manufactuererPartNumber",
					"serialNumber",
					"modLevel",
					"manufacturer",
					"countryOfManufacturer",
					"communicatingWithPCM",
					"fanInPowerSupplyFRU",
					"nominalVoltage",
					"actualVoltage",
					"actualCurrentInAmps",
					"frameIDInfo",
					"twentyFourVoltVoltage",
					"fanRailVoltage",
					"switchedRailVoltage",
					"twentyFourVoltCurrentInAmps",
					"powerConsumedInWatts",
					"sampleRateInSeconds",
					"samplesTaken",
					"EPMTemperatureInCelsius",
					"frameToFrameTemperatureInCelsius",
					"frameToFrameAttached",
					"frameToFrame5VoltEnabled",
					"fansEnabled",
					"backSwitchOpen",
					"filterSwitchOPen",
					"frontSwitchOpen",
					"safetyInterlockOpen",
					"driveFrameNumber",
					"switchRailState",
					"robotPowerEnabled",
					"internalLightsEnabled",
					"externalLightsEnabled",
					"fanPair",
					"fainInFMM",
					"present",
					"safetyDoorState",
					"overrideSwitch",
					"rearAccessPanel",
					"sideAccessPanel",
					"sidePanel",
					"robotInServiceFrame",
					"bulkIEPresent",
					"bulkIEDoorOpen",
					"bulkIEAjar",
					"solenoidPinPosition",
					"bulkTAPLocation",
					"EC",
					"topLevelAssemblyEC",
					"topLevelAssemblySerialNumber",
					"date",
					"isGen3",
					"freeSpaceInMB",
					"parallelACPresent",
					"primaryACPresent",
					"secondaryACPresent",
					"supplyDetectionWorking",
					"ACCurrentInAmps",
					"primaryACVoltage",
					"secondaryACVoltage",
					"onBoardTemperatureInCelsius",
					"remoteTemperatureInCelsius",
					"powerSupplyInPCM",
					"backPanelSwitch",
					"fanPanelSwitch",
					"filterPanelSwitch",
					"frontTAPFramePanelSwitch",
					"boardVoltage",
					"fanInputVoltage",
					"fanSpeedVoltage",
					"fanSpeedSetting",
					"newFansCalibrated",
					"FanInFCM",
					"lightBank",
					"number",
					"okay",
					"speedInRPM",
					"position",
					"faulted",
					"poweredOn"};					


		String url = getLibraryStatusURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}

	public void listASLs(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;
		
		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"ASLName"};
		
		String url = getASLNamesURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", false);
		}

	}
	
	public void listControllers(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"controller",
					"ID",
					"status",
					"firmware",
					"type",
					"failoverFrom",
					"port",
					"name",
					"useSoftAddress",
					"loopId",
					"initiatorEnabled",
					"fibreConnectionMode"};

		String url = getControllerListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}
	}

	public XMLResult[] listDrives(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"drive",
					"ID",
					"driveStatus",
					"partition",
					"partitionDriveNumber",
					"driveType",
					"connection",
					"serialNumber",
					"manufacturerSerialNumber",
					"driveFirmware",
					"dcmFirmware",
					"wwn",
					"fibreAddress",
					"loopNumber",
					"sparedWith",
					"spareFor",
					"health",
					"firmwareStaging",
					"connectionStatus",
					"hostID",
					"portID",
					"firmware",
					"complete",
					"percentStaged",
					"committing"};

		String url = getDriveListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "drive", true);
		}

		return response;
	}

	public XMLResult[] listHHMData(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"subType",
					"reminder",
					"typeName",
					"value",
					"unit",
					"severity",
					"defaultThreshold",
					"currentThreshold",
					"postedDate"};

		String url = getHHMListURL();
		xmlOutput = cxn.queryLibrary(url);
		
		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "typeName", true);
		}	

		return response;
	}

	public XMLResult[] listInventory(String partition, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"partition",
					"storageSlot",
					"entryExitSlot",
					"drive",
					"id",
					"offset",
					"barcode",
					"isQueued",
					"full"};
	
		String url = getInventoryListURL(partition);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "partition", true);
		}

		return response;
	}

	public XMLResult[] listMLMSettings(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"MLMEnabled",
					"nonMLMAlertsEnaabled",
					"loadCountDiscrepancyAlertsEnabled",
					"minCleaningPassesBeforeWarningCount",
					"maxTapeLoadsBeforeWarningCount",
					"autoDiscoveryEnabled",
					"autoDiscoveryIdleWaitInMinutes",
					"broadcastBaseConversion",
					"broadcastMegabitPerSecond",
					"postScanTapeBlackOut",
					"noncertifiedMAMBarcodeWriteEnabled",
					"sunday",
					"monday",
					"tuesday",
					"wednesday",
					"thursday",
					"friday",
					"saturday",
					"start",
					"stop"};

		String url = getMLMSettingsListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}

	public XMLResult[] listOptionKeys(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"optionKey",
					"keyValue",
					"description",
					"action",
					"daysRemaining"};

		String url = getOptionKeyListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "optionKey", true);
		}

		return response;
	}

	public XMLResult[] listPackages(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"current",
					"list",
					"name"};

		String url = getPackageListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}

	public XMLResult[] listPackageDetails(String pack, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"packageName",
					"allComponentsUpToDate",
					"allComponentsFullyStaged",
					"component",
					"name",
					"currentVersion",
					"packageVersion",
					"fullyStaged"};

		String url = getPackageDetailsURL(pack);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}

	public XMLResult[] listPackageFirmware(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"packageName",
					"allComponentsUpToDate",
					"component",
					"name",
					"currentVersion",
					"packageVersion"};

		String url = getPackageFirmwareURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}
	
	public XMLResult[] listPartitionDetails(String option, boolean printToShell)
	{
		// Get detailed information on the partitions.
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"name",
					"slotsPerChamber",
					"numStorageSlots",
					"numEESlots",
					"eeType",
					"includeDriveAndMediaGenerationInRES",
					"exporters",
					"exporter",
					"drives",
					"globalSpares",
					"MLMMediaVerification",
					"preScan",
					"postScan",
					"scanAfterDays",
					"scanAfterWrite",
					"scanAfterRead",
					"daysToScanAfter",
					"allowUsers",
					"cleaningPartition",
					"id",
					"type"};
			
		String url = getPartitionListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);


		// Filter out results to just a partition if specified by the user.
		if(!(option.equals("none") || option.equals("all")))
		{
			response = filterXMLByTagName(response, "name", option);
		}
	
		if(printToShell)
		{
			printOutput(response, "name", true);
		}

		return response;
	}

	public void listPartitions(boolean printToShell)
	{
		// Print a list of partitions
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"partitionName"};

		String url = getPartitionNamesListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "name", false);
		}
	}

	public XMLResult[] listSettings(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"libraryName",
					"autoLogoutTimeoutInMinutes",
					"drivePerformanceMonitoringEnabled",
					"SNMPSettings",
					"autoMaticPowerUpAfterPowerFailureEnabled",
					"trapDestination",
					"enabled",
					"systemContact",
					"systemLocation",
					"community",
					"description",
					"ipAddress"};

		String url = getLibrarySettingsURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}

	public XMLResult[] listTasks(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"currentAsynchronousAction",
					"currentBackgroundTasks",
					"pageNeedingProgressRequest",
					"task",
					"name",
					"status",
					"feedbackString",
					"thread",
					"description",
					"extraInformation"};

		String url = getTaskListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", true);
		}

		return response;
	}

	public XMLResult[] listTraceNames(String traceType, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;
		boolean printHeaders = false;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"logName", "gathered"};

		String url = getTracesNamesURL(traceType);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			// motion traces and security logs have a gathered field.
			// To make the output less confusing, we'll print the headers here.
			if(traceType.substring(0, 8).equals("security") || traceType.equals("motion"))
			{
				printHeaders = true;
			}

			printOutput(response, "none", printHeaders);
		}

		return response;
	}

	public boolean login(String user, String password)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status"};

		String url = getLoginURL(user, password);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(response[0].value.equalsIgnoreCase("OK"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean logout()
	{
		String xmlOutput;

		String url = getLogoutURL();
		xmlOutput = cxn.queryLibrary(url);
		
		return true;
	}

	public void magazineCapacity(String partition, boolean printToShell)
	{
		// This prints a summary of the magazine contents.
		float utilization = 0;
		TeraPack[] magazines = magazineContents(partition, false);

		int full = 0;
		int empty = 0;
		int almostEmpty = 0;
		int quarter = 0;
		int half = 0;
		int threeQuarter = 0;

		for(int i=0; i<magazines.length; i++)
		{
			utilization = (float)magazines[i].getCapacity() / magazines[i].getNumSlots();
			if(utilization == 1) { full++; }
			else if(utilization >= .75) { threeQuarter++; }
			else if(utilization >= .5) { half++; }
			else if(utilization >= .25) { quarter++; }
			else if(utilization > 0) { almostEmpty++; }
			else if(utilization == 0) { empty++; }
			
		}

		if(printToShell)
		{
			partition = partition.replace("%20", " ");
			System.out.println("There are " + magazines.length + " TeraPacks in partition " + partition);
			if(full>0) { System.out.println("Full: " + full); }
			if(threeQuarter>0) { System.out.println(">75%: " + threeQuarter); }
			if(half>0) { System.out.println(">50%: " + half); }
			if(quarter>0) { System.out.println(">25%: " + quarter); }
			if(almostEmpty>0) { System.out.println("<25%: " + almostEmpty); }
			if(empty>0) { System.out.println("Empty: " + empty); }
		}

	}

	public void magazineCompaction(String partition, int maxMoves, String output_type, boolean printToShell)
	{
		TeraPack[] magazine = sortMagazines(partition, true, true);

		// Determine if the move commands will be issued to the library via
		// XML or if a move list will be generated in ../output/MoveQueue.txt

		// Filename requirements are specific for the move queue.
		// It has to have this name to work.
		String fileName = "../output/MoveQueue.txt";
		
		if(output_type.equals("move-queue"))
		{
		
			if(moveListCreateFile(fileName))
			{
				log.log("Created move queue file: " + fileName, 1);
			}
			else
			{
				log.log("Unable to create move queue file: " + fileName, 3);
			}
		}	

		planCompaction(partition, magazine, maxMoves, output_type, fileName, true);

		if(output_type.equals("move-queue"))
		{
			System.out.println("\nGeneration of move queue is complete. The file can be found in the ../output directory. Upload the move queue to the library either by USB or the web GUI. When using USB, the file must be named MoveQueue.txt and placed in the root (/) directory to be uploaded. The move queue can be uploaded from the Inventory > Advanced menu.\n");
		}
	}

	public TeraPack[] magazineContents(String partition, boolean printToShell)
	{
		// This creates an array of terapacks for higher level work
		// and also performs a cleaner output than the physical inventory
		// option.

		// Get the library type, needed to know TeraPack size
		String libraryType = "none";
		XMLResult[] response = listPartitionDetails(partition, false);
		int magIterator = 0; // Track which magagzine we're on.

		// Search for type header.
		for(int i=0; i<response.length; i++)
		{
			if(response[i].headerTag.equalsIgnoreCase("type"))
			{
				libraryType = response[i].value;
			}
		}

		// Calculate the magazine capacity
		int magazineCount = countMagazines(partition);
		
		// Parse the magazine XML data to make an array of Terapacks.
		response = physicalInventory(partition, false);
		TeraPack[] magazines = new TeraPack[magazineCount];

		for(int j=0; j<response.length; j++)
		{
			if((response[j].headerTag.equalsIgnoreCase("storage>magazine>offset")) || (response[j].headerTag.equalsIgnoreCase("entryExit>magazine>offset")))
			{
				if(j>0)
				{
					magIterator++;
				}

				magazines[magIterator] = new TeraPack(libraryType);
			}
			magazines[magIterator].importXMLResult(response[j]);
			

		}

		for(int m=0; m<magazines.length; m++)
		{
			magazines[m].calculateCapacity();
		}
		

		if(printToShell)
		{
			System.out.println("Magazine count: " + magazineCount);

			for(int k=0; k<magazines.length; k++)
			{
				System.out.println("\nBarcode: " + magazines[k].getMagazineBarcode());
				System.out.println("utilization: " + magazines[k].getCapacity());
				for(int l=0; l<magazines[k].getNumSlots(); l++)
				{
					System.out.println(l + ": " + magazines[k].getTapeBarcode(l));
				}
			}
		}


		return magazines;
	}

	public void maintenanceHHMReset(boolean printToShell)
	{
		String libraryType = getLibraryType(printToShell);
		String robot = "none";
		int iterations = 1;

		if(libraryType.equalsIgnoreCase("TFinity"))
		{
			iterations = 2;
		}

		for(int i=0; i<iterations; i++)
		{
			if(libraryType.equalsIgnoreCase("TFinity"))
			{
				robot = "Robot " + Integer.toString(i+1);
			}

			resetHHMCounter("Vertical Axis", "Trip 1", robot, printToShell);
			resetHHMCounter("Vertical Axis", "Trip 2", robot, printToShell);
		}
		
	}

	public void moveListAppendLine(String source_type, String source, String dest_type, String destination, String fileName)
	{
		//========================================
		// moveListAppendLine
		//	This function creates the MoveQueue.txt
		//	file for the BlueScale move list.
		//	This MoveList can be uploaded from
		//	the web GUI or the LCM via USB to
		//	issue moves.
		//
		//	This command adds a move command
		//	line to the MoveQueue.txt file.
		//========================================

		String delimiter = ":";
		String line = source_type + source + delimiter + dest_type + destination + "\r"; // added the \r to which creates the \r\n DOS endline character. **necessary**
		FileManager movelist = new FileManager();
		movelist.appendToFile(fileName, line);

	}

	public boolean moveListCreateFile(String fileName)
	{
		//=========================================
		// moveListCreateFile
		// 	This function creates the MoveQueue.txt
		// 	for a BlueScale move list. This move
		// 	List can be be uploaded from the web
		// 	GUI or to the LCM via USB to issue
		// 	moves.
		//
		// 	Since we're issueing commands to the
		// 	library, we need to make sure the old
		// 	file is deleted before starting.
		//
		// 	File name is specified per the T950
		// 	user guide.
		//=========================================
		
		FileManager newFile = new FileManager();
		
		return newFile.createFileDeleteOld(fileName, true);
	}

	public XMLResult[] moveTape(String partition, String sourceID, String sourceNumber, String destID, String destNumber, boolean printToShell)
	{
		// Issue a move command on the library.
		// This feature isn't supported for BlueScale before 12.8
		// parititon - the partition in which the move will occur.
		// sourceID - What type of source is being specified.
		// 		valid inputs are SLOT, EE, DRIVE,
		// 		and BC (barcode)
		// sourceNumber - How the source is identified.
		// 		Slot (offset) or barcode.
		// destID - What type of destination is being specified
		// 		valid inputs are SLOT, EE, DRIVE
		// destNumber - What is the Slot (offset) of the destination.

		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status", "message"};

		String url = getMoveURL(partition, sourceID, sourceNumber, destID, destNumber);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", false);
		}
	
		return response;
	}

	public XMLResult[] physicalInventory(String partition, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;
		
		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"storage",
					"entryExit",
					"magazine",
					"offset",
					"barcode",
					"frameNumber",
					"tapeBayNumber",
					"drawerNumber",
					"slot",
					"number",
					"barcode",
					"barcodeValid"};

		String url = getPhysicalInventoryURL(partition);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "magazine", true);
		}

		return response;
	}

	public void printDebug(XMLResult[] response)
	{
		for(int i=0; i<response.length; i++)
		{
			System.out.println(i + ": level: " + response[i].docLevel 
					+ " path: " + response[i].headerTag 
					+ " value: " + response[i].value);
		}
	}

	public void printOutput(XMLResult[] response, String firstLine, boolean includeHeaders)
	{
		// Track the level in the doc of the previous element to determine
		// what to print. Start at 1 as this is the '0' value returned by
		// the xml parser.
		String[] headers = {"none"};
		String[] oldHeaders = {"none"};
		String[] tempHeaders;

		for(int i=0; i<response.length; i++)
		{
			// Print the headers and indents if desired.
			if(includeHeaders)
			{
				// Split out the headers.
				headers = response[i].headerTag.split(">");
				
				// Increment through the headers to print them.
				for(int j=0; j < headers.length; j++)
				{
					// Check to see if index exists in the old
					// header before comparing the values.
					// Also check header length as the final
					// header should be printed regardless.
					// If there are three items listed in a catagory
					// we'll want the header printed next to all
					// three and not just the first.
					if(j < oldHeaders.length && j < headers.length)
					{
						// Check to see if this header is the
						// same as the last one. If so, omit.
						if(!headers[j].equalsIgnoreCase(oldHeaders[j]))
						{
							// Values aren't the same. Follow
							// the same process printed in the
							// else statement from here.
						
							// Check to see if there is a value
							// on this line. Opening tags get built
							// into the header of the tag with a value.
							// They don't get their own. Closing tags
							// get a blank value. Parsing for this
							// blank value allows identification of
							// closing tags.
							if(response[i].value.length()>0)
							{
								// Response isn't blank.
								// Print indents
								// There is one indent per level
								// of the xml document.
								for(int k=0; k<j; k++)
								{
									System.out.print("\t");
								}
							
								// Print the header
								System.out.print(headers[j] + ":   ");
							
								// Print a new line character if this
								// is not the last header to print for
								// the value.
								if(j < headers.length-1)
								{
									System.out.print("\n");
								}
							}
							else
							{
								// If this is a closing array.
								// Delete the tag from the array
								// before saving it for comparison
								// on the next iteration.
								tempHeaders = new String[headers.length-1];
								for(int l=0; l < tempHeaders.length; l++)
								{
									tempHeaders[l] = headers[l];
								}
	
								headers = tempHeaders;
							}
						}
					}
					else // Just print the new header.
					{
						// Check to see if there is a value
						// on this line. Opening tags get built
						// into the header of the tag with a value.
						// They don't get their own. Closing tags
						// get a blank value. Parsing for this
						// blank value allows identification of
						// closing tags.
						if(response[i].value.length()>0)
						{
							// Response isn't blank.
							// Print indents
							// There is one indent per level
							// of the xml document.
							for(int k=0; k<j; k++)
							{
								System.out.print("\t");
							}
							
							// Print the header
							System.out.print(headers[j] + ":   ");

							// Print a new line character if this
							// isn't the last header to print for
							// the value.
							if(j < headers.length-1)
							{
								System.out.print("\n");
							}
						}
						else
						{
							// If this is a closing array.
							// Delete the tag from the array
							// before saving it for comparison
							// on the next iteration.
							tempHeaders = new String[headers.length-1];
							for(int l=0; l < tempHeaders.length; l++)
							{
								tempHeaders[l] = headers[l];
							}

							headers = tempHeaders;
						}
					}
				}
				
			}
			
			// Print the value if it exists.
			if(response[i].value.length()>0)
			{
				System.out.println(response[i].value);
			}

			// Store last value for comparison.
			oldHeaders = headers;
		}
		
		// Debug the input for testing.
		//printDebug(response);
	}
	
	public void resetHHMCounter(String type, String subtype, String robot, boolean printToShell)
	{
		// Based on this output, this function and the other
		// HHM funtion (setHHMThreshold) could be handled by the
		// generic XML String. Due to the number of possible settings
		// these two functions were pushed out to validate the inputs
		// before calling the command.
		
		// One boolean value set to false will carry though.
		// The next function won't call if this value is false
		// saving compute time.
		boolean isValid = false;

		// Validate Type
		isValid = validateHHMType(type);

		// Validate SubType
		if(isValid)
		{
			isValid = validateHHMSubType(subtype);
		}

		// Validate Robot. None is a valid option.
		if(isValid)
		{
			isValid = validateRobot(robot);
		}

		// Call the actual function.
		if(isValid)
		{
			String xmlOutput;
			XMLResult[] response;

			XMLParser xmlparser = new XMLParser();
			String[] searchTerms = {"status", "message"};

			String url = getHHMResetCounterURL(type, subtype, robot);
			xmlOutput = cxn.queryLibrary(url);

			xmlparser.setXML(xmlOutput);
			response = xmlparser.parseXML(searchTerms);

			if(printToShell)
			{
				printOutput(response, "none", false);
			}

		}
	}

	public XMLResult[] robotUtilization(boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"robotUtilizationDataPoint",
					"hourStartingAt",
					"percentUtilization"};

		String url = getRobotUtilizationURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "robotUtilizationDataPoint", true);
		}

		return response;
	}

	public void setHHMThreshold(String event, String keepDefault, String value, boolean printToShell)
	{
		// Based on this output, this function could be covered
		// by the getXMLStatusString function. However due to the
		// variety of specific commands requested, validation of
		// inputs should be performed.
		
		// One boolean value set to false will carry through.
		// The next functions won't activate if this value is
		// false, saving compute time.
		boolean isValid = false;

		// Validate event
		isValid = validateHHMEvent(event);

		// Validate keepDefault
		if(isValid)
		{
			if(keepDefault.equals("true") || keepDefault.equals("false"))
			{
				isValid = true;
			}
		}

		// Validate the value
		if(isValid)
		{
			if(Integer.valueOf(value)>=0)
			{
				isValid = true;
			}
		}

		// Call the actual function.
		if(isValid)
		{
			String xmlOutput;
			XMLResult[] response;

			XMLParser xmlparser = new XMLParser();
			String[] searchTerms = {"status", "message"};

			String url = getHHMResetCounterURL(event, keepDefault, value);
			xmlOutput = cxn.queryLibrary(url);

			xmlparser.setXML(xmlOutput);
			response = xmlparser.parseXML(searchTerms);

			if(printToShell)
			{
				printOutput(response, "none", false);
			}

		}
	}

	public void uploadPackageUpdate(String filename, boolean printToShell)
	{
		String url = getPostPackageUpdateURL();
		String response;

		response = cxn.postPackageToLibrary(url, filename);

		if(printToShell)
		{
			System.out.println(response);
		}
	}

	//====================================================================
	// Internal Functions
	// 	Private functions used for the more complex query tasks
	// 	the code is capable of.
	//====================================================================

	private String convertMLMSetting(String setting)
	{
		String url = "none";
		switch(setting)
		{
			case "mlm":
				url = "MLM=";
				break;
			case "noncert":
			case "noncertified":
				url = "nonCertifiedMAMBarcodeWrite=";
				break;
		}
		return url;
	}
	
	private String convertTAPString(String tap)
	{
		// Converts the user input to the required
		// XML value
		if(tap.equals("left"))
		{
			tap = "leftBulk";
		}
		else if(tap.equals("right"))
		{
			tap = "rightBulk";
		}
		else if(tap.equals("both"))
		{
			tap = "leftAndRightBulk";
		}
		else if(tap.equals("mainTop"))
		{
			tap = "mainTop";
		}
		else if(tap.equals("mainBottom"))
		{
			tap = "mainBottom";
		}
		else
		{
			tap = "none";
		}

		return tap;
	}

	private TeraPack[] filterEmptyFullEntryExit(TeraPack[] mags)
	{
		// Filter out empty, full, and entry exit terapacks.
		// The end result should only be partially full terapacks
		// in the Storage Partition.
		List<TeraPack> availableInventory = new ArrayList<>();
			
		for(int i=0; i<mags.length; i++)
		{
			if(mags[i].getCapacity()>0 && mags[i].getCapacity()<mags[i].getNumSlots() && mags[i].getLocation().equalsIgnoreCase("storage"))
			{
				availableInventory.add(mags[i]);
			}
		}
		
		// Convert list back into a TeraPack[] array to allow
		// for consistent information.
		TeraPack[] tempTeraPack = new TeraPack[availableInventory.size()];

		for(int i=0; i<availableInventory.size(); i++)
		{
			tempTeraPack[i] = availableInventory.get(i);
		}

		return tempTeraPack;

	}
	
	private XMLResult[] filterXMLByTagName(XMLResult[] response, String header, String value)
	{
		// FilterXMLByTagName
		// 	The purpose of this function to filter an XML response down
		// 	to a repeated section such as by partition or drives. 

		List<XMLResult> filteredResult = new ArrayList<>();
		boolean inTag = false;
		
		for(int i=0; i<response.length; i++)
		{
			// Check to see if we're at the opening tag/value pair.
			// If so, open the write.
			if(response[i].headerTag.equalsIgnoreCase(header) && response[i].value.equalsIgnoreCase(value) && inTag==false)
			{
				inTag = true;
			}

			// Check to see if we're at the closing tag/value pair.
			// If so, close the tag.
			// I do it this way to store an empty line to space out the values.
			
			if(response[i].headerTag.equalsIgnoreCase(header) && !response[i].value.equals(value) && inTag == true)
			{
				inTag = false;
			}

			// Save all rows until the tag is closed.
			if(inTag)
			{
				filteredResult.add(response[i]);
			}
		}

		// Convert the list back to the XMLResult[]

		XMLResult[] result = new XMLResult[filteredResult.size()];

		for(int i=0; i<result.length; i++)
		{
			result[i] = filteredResult.get(i);
		}

		return result;
	}

	private String findDestinationSlot(String partition, int destSlot, int magSlot, String barcode)
	{
		// Finds the slot string of the destination slot
		// by locating the slot of a tape within the same
		// TeraPack and adjusting the slot based on the difference.

		// destSlot = target
		// magSlot = slot occupied by passed barcode.
		int difference = destSlot - magSlot;
		String anchor = "none";
		anchor = findSlotString(partition, barcode);

		if(!anchor.equals("none"))
		{
			difference = difference + Integer.valueOf(anchor);
			return Integer.toString(difference);
		}

		return "none";		
	}

	private ArrayList<String> findEmptyTeraPacks(String partition, int magazine_size, boolean printToShell)
	{
		ArrayList<String> empty_slots = findEmptySlots(partition);
		ArrayList<String> terapack_slots = new ArrayList<String>();
		int firstSlot = Integer.valueOf(empty_slots.get(0));
		
		int terapack = firstSlot % magazine_size;
		terapack = (firstSlot - terapack)+1;

		boolean searching = true;
		int itr = 0;

		while(searching)
		{
			// If the slot is the first in the terapack
			// Check the index + mag size to determine
			// if all the slots are in the list.
			if(Integer.valueOf(empty_slots.get(itr)) == terapack)
			{
				if(Integer.valueOf(empty_slots.get(itr+magazine_size-1)) == (terapack + magazine_size - 1))
				{
					// Add slots to array.
					for(int i = itr; i<itr+magazine_size; i++)
					{
						terapack_slots.add(empty_slots.get(i));
					}
				}
			}

			// If in the last slot of the TP increment the tp.
			if(Integer.valueOf(empty_slots.get(itr)) == (terapack + magazine_size - 1))
			{
				terapack+=magazine_size;
			}

			// Check to see if we got to the end of the array.
			// if the last slot in the terapack is higher than the
			// last value of the list, we're done.
			if((terapack + magazine_size - 1) > Integer.valueOf(empty_slots.get(empty_slots.size()-1)))
			{
				searching = false;
			}

			itr++;
		}
	
		return terapack_slots;
	}

	private ArrayList<String> findEmptySlots(String partition)
	{
		ArrayList<String> empty_slots = new ArrayList<String>();
		boolean searching = true;
		int itr = 0;
		String checkSlot = "0";

		XMLResult[] response = listInventory(partition, false);
		
		// Build a list of all available slots.
		while(searching)
		{
			// Grab a slot number.
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>Offset"))
			{
				checkSlot = response[itr].value;
			}
			
			// Determine if that slot is occupied.
			// If not store the number for verificatation.
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>full") && response[itr].value.equalsIgnoreCase("No"))
			{
				empty_slots.add(checkSlot);
			}

			// Determine the end of the search.
			if(response[itr].headerTag.equalsIgnoreCase("partition>entryExitSlot"))
			{
				searching = false;
			}

			itr++;
		}

		return empty_slots;
	}	

	private String findSlotString(String partition, String barcode)
	{
		// Search the inventory for the barcode.
		// Export the Slot number of the barcode.

		boolean slotFound = false;
		String slot = "none";
		int itr = 0; // using while with an iterator to save cycles.

		XMLResult[] response = listInventory(partition, false);

		while(!slotFound)
		{
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>Offset"))
			{
				slot = response[itr].value;
			}
			
			// have to use trim() as there's whitespace in the barcode
			// for some reason.
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>barcode") && response[itr].value.trim().equalsIgnoreCase(barcode))
			{
				slotFound = true;
			}

			itr++;
		}

		return slot;
	}
	
	private String generateSlotString(String TeraPackOffset, int TapeSlot)
	{
		int librarySlot = (10 * Integer.valueOf(TeraPackOffset)) - (10 - TapeSlot) + 1;
		return Integer.toString(librarySlot);
	}

	public String getLibraryType(boolean printToShell)
	{
		String libraryType = "none";
		boolean typeFound = false;
		int itr = 0;

		XMLResult[] libraryProfile = libraryStatus(false);
		
		while(!typeFound)
		{
			if(libraryProfile[itr].headerTag.equalsIgnoreCase("libraryType"))
			{
				libraryType = libraryProfile[itr].value;
				typeFound = true;
			}
			itr++;
		}

		if(printToShell)
		{
			System.out.println(libraryType);
		}

		return libraryType;

	}

	public String getMediaType(String partition, boolean printToShell)
	{
		String mediaType="none";
		boolean typeFound = false;
		int itr = 0;

		XMLResult[] partitionProfile = listPartitionDetails(partition, false);

		while(!typeFound)
		{
			if(partitionProfile[itr].headerTag.equalsIgnoreCase("Type"))
			{
				mediaType = partitionProfile[itr].value;
				typeFound = true;
			}
			itr++;
		}	

		if(printToShell)
		{
			System.out.println("Partition " + partition + " uses " + mediaType + " media.");
		}

		return mediaType;
	}
	
	private void planCompaction(String partition, TeraPack[] mags, int maxMoves, String output_type, String fileName, boolean printToShell)
	{
		int source = 0; // Incrementor for source TP
		int destination = mags.length - 1; // Increment for destination TP
		int sourceTapes = mags[source].getCapacity(); // How many tapes are in the Source Magazine.
		int destSlots = mags[destination].getNumSlots() - mags[destination].getCapacity(); // How many slots are available in the destination TeraPack.

		if(source>=destination)
		{

			log.log("There are no moves to free up any TeraPacks.", 1);
			if(printToShell)
			{
				System.out.println("There are no moves to free up any TeraPacks.");
			}
		}
		else if(sourceTapes > maxMoves)
		{
			log.log("There are " + sourceTapes + " tapes in this TeraPack and only " + maxMoves + " moves allowed with this operation. No TeraPacks will be freed.", 1);
			if(printToShell)
			{
				System.out.println("There are " + sourceTapes + " tapes in this TeraPack and only " + maxMoves + " moves allowed with this operation. No TeraPacks will be freed.");
			}
		}

		int tapeSlot = -1;
		int emptySlot = -1;
		int moves = 0;
		String sourceBarcode;
		String sourceSlotString = "none";
		String destSlotString = "none";

		// Move validation variables.
		int checkSlot; // slot to check for reference tape.
		String checkBarcode;
		String checkSlotString;
		boolean isValidMove;
	
		while((source < destination) && (moves < maxMoves))
		{
			// Variables are set here to reset them
			// with every iteration.
			isValidMove = false;
			checkSlot = -1;

			tapeSlot = mags[source].getNextOccupiedSlot(tapeSlot);
			emptySlot = mags[destination].getNextEmptySlot(emptySlot);
		
			sourceBarcode = mags[source].getBarcodeAtPosition(tapeSlot);

			// VALIDATION
			// The formula for the actual slot is a best-guess
			// we'll check to see if the barcode is in the source
			// slot, the destination slot is empty, and a barcode
			// in the same destination tp is in the slot we expect.

			checkSlot = mags[destination].getNextOccupiedSlot(checkSlot);
			if(checkSlot>=0)
			{
				checkBarcode = mags[destination].getBarcodeAtPosition(checkSlot);

				// Identify the target slots.
				// sourceSlot - slot of source tape.
				// destSlot - empty destination slot.
				// checkSlot - occupied slot in the same TeraPack to
				// 		anchor the TeraPack to the inventory
				// 		slot. destSlot is calculated from here.
				if(printToShell)
				{
					System.out.println("\nPreparing move " + moves);
				}

				sourceSlotString = findSlotString(partition, sourceBarcode);
				checkSlotString = findSlotString(partition, checkBarcode);
				destSlotString = findDestinationSlot(partition, emptySlot, checkSlot, checkBarcode);

				// Move validation.
				// Will be removed/commented out in a future release.
				// This was placed here when the slot was calculated
				// by generateSlotString() to verify the calculated
				// value. findSlotString() performs a similar task as
				// validateMove(), so this function validates the move
				// with by the same process that generates it. It's
				// redundant.
				isValidMove = validateMove(partition, sourceSlotString, sourceBarcode, destSlotString, checkSlotString, checkBarcode, true);				
			}
			else
			{
				// There's an error here where a checkslot is not being returned
				// and the move queue ends up duplicating the last issued move.
				log.log("Unable to validate move " + moves + ": cannot locate reference tape to verify destination for " + sourceBarcode + ".", 3);
				log.log("Check slot " + checkSlot + " returned for destination magazine " + destination, 3);
			     	if(printToShell)
				{
					System.out.println("Unable to validate move " + moves + " for tape " + sourceBarcode + ". Please re-run the command to re-try.");
				}	
			}

			// Actually Perform the move
			if(isValidMove)
			{
				if(printToShell)
				{
					System.out.println("Move " + moves + ": " + sourceBarcode  + " at slot " + sourceSlotString + " moving to " + destSlotString);
				}
			
				if(output_type.equals("move-queue"))
				{
					moveListAppendLine("Slot", sourceSlotString, "Slot", destSlotString, fileName);
				}
				else
				{
					sendMove(partition, sourceSlotString, destSlotString);
				}
			}
			else
			{
				log.log("Move " + moves + " failed for barcode " + sourceBarcode, 3);
				if(printToShell)
				{
					System.out.println("Cannot verify slot information for move " + moves + ". Cancelling action.");
				}
			}

		
			// Remove tape from mag count.
			// And move to the next mag if this on is empty.
			sourceTapes--;
			
			if(sourceTapes<1)
			{
				source++;
				sourceTapes = mags[source].getCapacity();
				tapeSlot = -1;
			}

			// Remove one available slot from destination
			// And move to the next mag if this one is empty.
			destSlots--;

			if(destSlots<1)
			{
				destination--;
				destSlots = mags[destination].getNumSlots() - mags[destination].getCapacity();
				emptySlot = -1;
			}

			moves++;
		}

	}
	
	public void prepareSlotIQ(String partition, int max_moves, String output_format, boolean printToShell)
	{
		// This command prepares a library for configuration with
		// SlotIQ. Slot IQ requires at least 1 empty slot to be
		// available in each TeraPack. This library will move
		// tapes out of the 8th slot of all full TeraPacks.
		//
		// This command should be run before SlotIQ is enabled
		// on the library.

		log.log("Preparing library for SlotIQ", 1);

		if(printToShell)
		{
			System.out.println("Preparing library for SlotIQ...");
		}

		String mediaType = getMediaType(partition, true);

		TeraPack[] magazines = sortMagazines(partition, false, true); 

		if(slotIQIsPossible(magazines, mediaType, true))
		{
			// Filename requirements are specific for the move queue.
			// It has to have this name to work.
			String fileName = "../output/MoveQueue.txt";
		
			if(output_format.equals("move-queue"))
			{
		
				if(moveListCreateFile(fileName))
				{
					log.log("Created move queue file: " + fileName, 1);
				}
				else
				{
					log.log("Unable to create move queue file: " + fileName, 3);
				}
			}

			System.out.println("Slot IQ preparation is possible.");
			
			slotIQEmptyFullTerapacks(magazines, partition, mediaType, max_moves, output_format, fileName, true);
			
			if(output_format.equals("move-queue"))
			{
				System.out.println("\nGeneration of move queue is complete. The file can be found in the ../output directory. Upload the move queue to the library either by USB or the web GUI. When using USB, the file must be named MoveQueue.txt and placed in the root (/) directory to be uploaded. The move queue can be uploaded from the Inventory > Advanced menu.\n");
			}
		
		}
		else
		{
			log.log("Unable to prepare library for Slot IQ.", 3); 
		
			if(printToShell)
			{
				System.out.println("There are not enough available slots to perform SlotIQ preparation."); 
			}
		}	
	}

	//==============================================
	// QUICK SORT
	//==============================================
	//
	private TeraPack[] quickSort(TeraPack[] mags, int low, int high)
	{
		if(low < high)
		{
			int pi = partition(mags, low, high);

			// Separately sort elements before
			// partition and after partition
			quickSort(mags, low, pi - 1);
			quickSort(mags, pi + 1, high);
		}

		return mags;
	}
	//
	// Part of the quick sort algorithm, not anything to do with library partitions.
	private int partition(TeraPack[] mags, int low, int high)
	{
		// Pivot
		int pivot = mags[high].getCapacity();

		// Index of smaller element and 
		// indicates the right position of the
		// pivot found so far
		int i = (low - 1);

		for(int j = low; j < high; j++)
		{
			// If the current element is smaller
			// than the pivot
			if(mags[j].getCapacity() < pivot)
			{
				i++;
				swapTeraPacks(mags, i, j);
			}
		}

		swapTeraPacks(mags, i+1, high);
		return (i + 1);
	}
	//
	//==============================================
	// END QUICK SORT
	//==============================================
	

	private boolean sendMove(String partition, String sourceSlot, String destSlot)
	{
		// Send the move to the library.
		// Wait until the move is complete before exiting function.

		moveTape(partition, "SLOT", sourceSlot, "SLOT", destSlot, true);

		return true;	
	}

	private void slotIQEmptyFullTerapacks(TeraPack[] mags, String partition, String mediaType, int max_moves, String output_format, String fileName, boolean printToShell)
	{
		int max_slots_per_terapack;
		int source_terapack = mags.length-1;
		int target_terapack = mags.length-1;
		int moves_to_empty_terapack = 0; // tracking movement into empty terapacks.
		int move_counter = 0;
		boolean moving_tapes = true;

		if(mediaType.equals("LTO"))
		{
			max_slots_per_terapack=10;
		}
		else
		{
			max_slots_per_terapack=9;
		}
	
		ArrayList<String> empty_slots = findEmptyTeraPacks(partition, max_slots_per_terapack, printToShell);
		
		while(moving_tapes)
		{
			// Find next destination terapack.
			target_terapack = slotIQFindNextAvailableTerapack(mags, max_slots_per_terapack, target_terapack);

			// Determine if moves are possible or end the loop.
			if(target_terapack == -1 || mags[source_terapack].getCapacity() < max_slots_per_terapack || move_counter >= max_moves)
			{
				if(target_terapack == -1)
				{
					log.log("Error: Unable to find a destination TeraPack.", 3);
				}
				else if(move_counter >= max_moves)
				{
					log.log("Maximum requested moves (" + max_moves + ") has been reached.", 3);

					if(printToShell)
					{
						System.out.println("Maximum requested moves (" + max_moves + ") has been reached.");
					}
				}
				else
				{
					log.log("SlotIQ preparation complete. All TeraPacks have at least 1 open slot.", 3);

					if(printToShell)
					{
						System.out.println("SlotIQ preparation complete. All TeraPacks have at least 1 open slot.");
					}
				}
				moving_tapes = false;
			}
			else
			{
				if(printToShell)
				{
					// Starts with newline character to space between outputs for readability.
					System.out.println("\nPreparing move " + (move_counter + 1) + "...");
				}

				if(mags[target_terapack].getCapacity()>0)
				{
					// There is a tape in the TeraPack to use as an anchor
					slotIQQueueMovesToOccupied(mags, max_slots_per_terapack, source_terapack, target_terapack, partition, output_format, fileName, printToShell);
				}
				else
				{
					// Terapack is empty, so use empty terapack slots.
					// (empty_slots)
					moves_to_empty_terapack++;
					slotIQQueueMovesToEmpty(mags, max_slots_per_terapack, source_terapack, target_terapack, empty_slots.get(0), moves_to_empty_terapack, partition, output_format, fileName);
					empty_slots.remove(0);

					if(moves_to_empty_terapack==(max_slots_per_terapack-1))
					{
						// If moves_to_empty_terapack = max_slots - 1, there is only 1 slot left in the TeraPack.
						// Terapack capacity was incremented to ensure we move to the next target terapack.
						// Reset this counter to 0.
						moves_to_empty_terapack=0;
					}
				}
				
				move_counter++;
				source_terapack--;
			}
		}	
	}

	private int slotIQFindNextAvailableTerapack(TeraPack[] mags, int mag_size, int index)
	{
		if(mags[index].getCapacity()<mag_size-1)
		{
			return index;
		}
		else if(index-1>=0)
		{
			return slotIQFindNextAvailableTerapack(mags, mag_size, index-1);
		}
		else
		{
			return -1;
		}
	}

	private boolean slotIQIsPossible(TeraPack[] mags, String mediaType, boolean printToShell)
	{
		int tapes_to_move = 0;
		int slots_available = 0;
		int max_slots_per_magazine;

		if(mediaType.equals("LTO"))
		{
			max_slots_per_magazine = 10;
		}
		else
		{
			max_slots_per_magazine = 9;
		}

		for(int i=mags.length-1; i>=0; i--)
		{
			if(mags[i].getCapacity()==max_slots_per_magazine && mags[i].getLocation().equals("storage"))
			{
				tapes_to_move++;
			}
			else if(mags[i].getLocation().equals("storage"))
			{
				// Add the amount of available slots in the TeraPack - 1 to the available slots.
				// We need the -1 because this terapack also needs an open slot for slot iq
				slots_available = slots_available + (max_slots_per_magazine - mags[i].getCapacity() - 1);
			}
		}

		if(printToShell)
		{
			System.out.println(tapes_to_move + " moves are needed to prepare this partition for Slot IQ.");
			System.out.println("There are " + slots_available + " slots available for this task.");
		}

		if(tapes_to_move <= slots_available)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void slotIQQueueMovesToEmpty(TeraPack[] mags, int magazine_size, int source_terapack, int target_terapack, String target_slot, int slot_number, String partition, String output_format, String fileName)
	{
		String source_barcode = mags[source_terapack].getBarcodeAtPosition(0);
		String source_slot = findSlotString(partition, source_barcode);

		if(output_format.equals("move-queue"))
		{
			log.log("Writing move to move queue. (" + source_slot + ": " + source_barcode + ") to " + target_slot + ".", 2);
			moveListAppendLine("Slot", source_slot, "Slot", target_slot, fileName);
		}
		else
		{
			log.log("Sending move to library. (" + source_slot + ": " + source_barcode + ") to " + target_slot + ".", 2);
			sendMove(partition, source_slot, target_slot);
		}

		if(slot_number == magazine_size-1)
		{
			// Increment the tape number in this terapack so the queue ticks
			// to the next terapack.
			mags[target_terapack].addTapeCount(slot_number);
		}
	}

	private void slotIQQueueMovesToOccupied(TeraPack[] mags, int magazine_size, int source_terapack, int target_terapack, String partition, String output_format, String fileName, boolean printToShell)
	{
		String source_barcode;
		String source_slot;
		String target_slot;
		String check_slot;
		String check_barcode;
		int check_slot_int;
		int target_slot_int;

		source_barcode = mags[source_terapack].getBarcodeAtPosition(0);
		source_slot = findSlotString(partition, source_barcode);

		check_slot_int = mags[target_terapack].getNextOccupiedSlot(0);
		check_barcode = mags[target_terapack].getBarcodeAtPosition(check_slot_int);
		check_slot = findSlotString(partition, check_barcode);

		target_slot_int = mags[target_terapack].getNextEmptySlot(0);
		target_slot = findDestinationSlot(partition, target_slot_int, check_slot_int, check_barcode);


		if(validateMove(partition, source_slot, source_barcode, target_slot, check_slot, check_barcode, printToShell))
		{
			mags[target_terapack].addTapeToSlot(source_barcode, target_slot_int);

			if(output_format.equals("move-queue"))
			{
				log.log("Writing move to move queue. " + source_slot + " (" + source_barcode + ") to " + target_slot + ".", 2);
				moveListAppendLine("Slot", source_slot, "Slot", target_slot, fileName);
			}
			else
			{
				log.log("Sending move to library. " + source_slot + " (" + source_barcode + ") to " + target_slot + ".", 2);
				sendMove(partition, source_slot, target_slot);
			}
		}
		else
		{
			log.log("Error: Unable to validate move (" + source_slot + ": " + source_barcode + ") to (" + target_slot + ")", 3);
			
			if(printToShell)
			{
				System.out.println("Error: Unable to validate move " + source_slot + " (" + source_barcode + ") to (" + target_slot + ")");
			}
		}
	}

	private TeraPack[] sortMagazines(String partition, boolean omitEmptyFull, boolean printToShell)
	{
		// Gathering TeraPack from library.
		if(printToShell) 
		{ 
			System.out.println("Gathering TeraPack information from library..."); 
		}

		TeraPack[] magazines = magazineContents(partition, false);

		// Analyze TeraPacks
		if(printToShell)
		{
			System.out.println("Analyzing TeraPack Contents...");
		}

		// Remove the Empty and full TeraPacks if desired.
		if(omitEmptyFull)
		{
			magazines = filterEmptyFullEntryExit(magazines);
		}

		/* Debug Code
		// Print before and after.
		for(int i=0; i<magazines.length; i++)
		{
			System.out.print(magazines[i].getCapacity() + " ");
		}

		System.out.print("\n");
		*/

		quickSort(magazines, 0, magazines.length-1);

		/* Debug Code
		for(int i=0; i<magazines.length; i++)
		{
			System.out.print(magazines[i].getCapacity() + " ");
		}

		System.out.print("\n");
		*/

		return magazines;
	}

	private void swapTeraPacks(TeraPack[] terapacks, int i, int j)
	{
		// Swaps the TeraPack at position i with the 
		// TeraPack at position j in the terapacks array.
		TeraPack temp;
		temp = terapacks[i];
		terapacks[i] = terapacks[j];
		terapacks[j] = temp;
	}

	private boolean validateHHMEvent(String event)
	{
		boolean isValid = false;
		
		switch(event)
		{
			case "Check Contact Brushes":
			case "Service HAX":
			case "Service HAX Belt":
			case "Service VAX":
			case "Service VAX Belt":
			case "Service VAX Cable":
			case "Service Transporter":
			case "Service Required":
				isValid = true;
				break;
			default:
				// Duplicating effort to make
				// the function easier to read.
				isValid = false;
				// Print valid options to help input.
				System.out.println("ERROR: Invalid event entered. Please select an event from the list: Check Contact Brushes, Serivce HAX, Service HAX Belt, Service VAX, Service VAX Belt, Service VAX Cable, Service Transporter, and Service Required.");
				System.out.println("Note: events are case sensitive.");
				break;
		}


		return isValid;
	}

	private boolean validateHHMType(String type)
	{
		boolean isValid = false;

		switch(type)
		{
			case "Horizontal Axis":
			case "Vertical Axis":
			case "Picker Axis":
			case "Toggle Axis":
			case "Rotational Axis":
			case "Magazine Axis":
			case "Side Axis":
			case "Drive to Drive Move":
			case "Drive to Slot Move":
			case "Slot to Slot Move":
			case "Slot to Drive Move":
			case "TAP In Move":
			case "TAP Out Move":
				isValid = true;
				break;
			default:
				// Assigning false to make the code
				// easier to read.
				isValid = false;

				// Print options to help usability.
				System.out.println("ERROR: Invalid type entered. Please select a type from the list: Horizontal Axis, Vertical Axis, Picker Axis, Rotational Axis, Magazine Axis, Toggle Axis, Side Axis, Drive to Drive Move, Drive to Slot Move, Slot to Slot Move, Slot to Drive Move, TAP In Move, TAP Out Move");
				System.out.println("Note: events are case sensitive");
				break;
		}

		return isValid; 
	}

	private boolean validateHHMSubType(String subtype)
	{
		boolean isValid = false;

		switch(subtype)
		{
			case "Trip1":
			case "Trip2":
			case "None":
				isValid = true;
				break;
			default:
				// Assign false to make the code 
				// easier to read.
				isValid = false;

				// Print options to help usability.
				System.out.println("ERROR: Invalid subtype entered. Please select a subtype from the list: Trip1, Trip2, and None");
				System.out.println("Note: subtypes are case sensitive");
				break;
		}

		return isValid;
	}


	private boolean validateMove(String partition, String sourceSlot, String sourceBarcode, String destSlot, String destSlot2, String destBarcode, boolean printToShell)
	{
		// The purpose of this function is to validate the source and destination slot
		// against the library inventory before initiating the move.
		// I'm guessing the formula for slot number is (10 * TeraPack Offset) - (10 - TeraPack[i].tape's array index) + 1.
		// I'm also assuming the TeraPack offsets will update along with the slot numbers if a 
		// TeraPack is exported or imported into the library.
		// As there is a lot of guessing.... there's a validateMove.
		

		// Sort the three slots into ascending order
		// Needed to compare against the order the results come in.
		List<String> slotOrder = new ArrayList<>();
		
		if(Integer.valueOf(sourceSlot)<Integer.valueOf(destSlot))
		{
			slotOrder.add(sourceSlot);
			slotOrder.add(destSlot);
		}
		else
		{
			slotOrder.add(destSlot);
			slotOrder.add(sourceSlot);
		}

		if(Integer.valueOf(destSlot2) > Integer.valueOf(slotOrder.get(1)))
		{
			slotOrder.add(destSlot2);
		}
		else if(Integer.valueOf(destSlot2) < Integer.valueOf(slotOrder.get(0)))
		{
			slotOrder.add(0, destSlot2);
		}
		else
		{
			slotOrder.add(1, destSlot2);
		}
		
		// Log progress so far.
		log.log("Validating Move: " + sourceBarcode + " from source " + sourceSlot + " to " + destSlot, 1);
		log.log("Verifying destination with tape " + destBarcode + " at slot " + destSlot2, 1);

		// Search for the slots in the library's inventory.
		boolean inSlot = false; // Parsing the correct slot
		boolean success = true; // Success default true. Any failed test results in false.
		int listIndex = 0; // The index for the linked list.
		String searchValue = "none";
	
		// slotCheck is used to make sure all three slots have been verified during this process.
		// If source, dest, and dest2 are checked the corresponding index is marked true.
		// isValid is determined based on success and the indices on slotCheck.
		// results are logged.
		boolean[] slotCheck = {false, false, false};

		XMLResult[] response = listInventory(partition, false);

		
		for(int i=0; i<response.length; i++)
		{
			// Find the right storage slot
			if(listIndex<3)
			{
				if(response[i].headerTag.equalsIgnoreCase("partition>storageSlot>Offset") && response[i].value.equals(slotOrder.get(listIndex)))
				{
					inSlot = true;
					
					if(slotOrder.get(listIndex).equals(sourceSlot))
					{
						if(printToShell)
						{
							System.out.print("Verifying source...\t\t\t");
						}
						searchValue = sourceBarcode;
						slotCheck[0] = true; // source has been checked.
					}
					else if(slotOrder.get(listIndex).equals(destSlot))
					{
						if(printToShell)
						{
							System.out.print("Verifying destination...\t\t");
						}
						searchValue = "No";
						slotCheck[1] = true; // destination has been checked.
					}
					else if(slotOrder.get(listIndex).equals(destSlot2))
					{	
						if(printToShell)
						{
							System.out.print("Verifying destination TeraPack...\t");
						}
						searchValue = destBarcode;
						slotCheck[2] = true; // reference barcode has been checked.
					}
				}

				if(inSlot)
				{

					// Check the barcode in the slot against the one expected.
					// If it matches return true. Otherwise return false.
					if(response[i].headerTag.equalsIgnoreCase("partition>storageSlot>barcode"))
					{
						if(!response[i].value.trim().equalsIgnoreCase(searchValue.trim()))
						{
							if(printToShell)
							{
								System.out.println("[FAILED]");
							}
							success = false;
						}
						else if(printToShell)
						{
							System.out.println("[SUCCESS]");
						}
						listIndex++;
						inSlot = false;	
					}

					// Check the empty slot.
					// There is no barcode field in this slot
					// test for <full>no
					if(searchValue.equals("No") && response[i].headerTag.equalsIgnoreCase("partition>storageSlot>full"))
					{
						// False value
						if(!response[i].value.equalsIgnoreCase("no"))
						{
							if(printToShell)
							{
								System.out.println("[FAILED]");
							}
							success = false;
						}
						else if(printToShell)
						{
							System.out.println("[SUCCESS]");
						}
						listIndex++;
						inSlot = false;
					}
				}
			}
		}			

		if(success && slotCheck[0] && slotCheck[1] && slotCheck[2])
		{
			// All three slots were checked (slotCheck)
			// None of values failed.
			log.log("Move validated", 1);
			return true;
		}
		else
		{
			// The validation failed. Log why.
			if(!slotCheck[0])
			{
				log.log("Invalid Move (" + sourceBarcode + ": " + sourceSlot + " > " + destSlot + "): Unable to check source slot.", 3);
			}

			if(!slotCheck[1])
			{
				log.log("Invalid Move (" + sourceBarcode + ": " + sourceSlot + " > " + destSlot + "): Unable to check destination slot.", 3);
			}

			if(!slotCheck[2])
			{
				log.log("Invalid Move (" + sourceBarcode + ": " + sourceSlot + " > " + destSlot + "): Unable to check reference barcode " + destBarcode + "(" + destSlot2 + ").", 3);
			}

			if(!success)
			{
				log.log("Invalid Move (" + sourceBarcode + ": " + sourceSlot + " > " + destSlot + "): One of the slots checked did not have the expected value.", 3);
			}
			return false;
		}
	}

	private boolean validateRobot(String robot)
	{
		boolean	isValid = false;

		switch(robot)
		{
			case "Robot 1":
			case "Robot 2":
			case "none":
				isValid = true;
				break;
			default:
				// Set isValid to false to make
				// the code easier to read.
				isValid = false;

				// Print options to help with usability.
				System.out.println("ERROR: Invalid robot selected. Please select an option from the list: Robot 1, Robot 2, or none");
				break;
		}

		return isValid;
	}

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


