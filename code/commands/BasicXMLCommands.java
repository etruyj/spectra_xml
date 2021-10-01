//============================================================================
// BasicXMLCommands.java
// 	Description:
//		This class holds all the commands specified in the Spectra XML
//		reference guide. This code was broken out from the URLs and the
//		advanced commands to make it easier to administer.
//============================================================================

package com.socialvagrancy.spectraxml.commands;

import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.spectraxml.utils.Connector;
import com.socialvagrancy.spectraxml.utils.XMLParser;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicXMLCommands
{
	private Connector cxn;
	private Logger log;
	private URLs url_list;

	//====================================================================
	// Constructor
	//====================================================================
	
	public BasicXMLCommands(String server, boolean secure, Logger logbook)
	{
		// Declared logger in SpectraController as opposed to 
		// in connector to allow logging of issues within the commands.
		log = logbook;
		cxn = new Connector(log);
	
		String libraryAddress;

		if(secure)
		{
			libraryAddress = "https://";
		}
		else
		{
			libraryAddress = "http://";
		}
		
		url_list = new URLs(libraryAddress + server + "/gf/");

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
				url = url_list.getASLProgressURL();
				break;
			case "controller":
				url = url_list.getControllerProgressURL();
				break;
			case "drive":
			case "driveList":
			case "drive-list":
				url = url_list.getDriveListProgressURL();
				break;
			case "etherlib":
			case "etherLib":
				url = url_list.getEtherLibProgressURL();
				break;
			case "library":
			case "library-refresh":
				url = url_list.getLibraryProgressURL();
				break;
			case "package":
			case "package-update":
				url = url_list.getPackageProgressURL();
				break;
			case "utils":
				url = url_list.getUtilsProgressURL();
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

		String url = url_list.getPhysicalInventoryURL(partition);
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

		String url = url_list.getUtilsDisplayBarcodeReportingURL();
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

		String url = url_list.getUtilsDisplayTapeBarcodeVerificationURL();
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

		String url = url_list.getDriveLoadCountURL(option);
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

		String url = url_list.getASLDownloadURL(aslName);
		
		cxn.downloadFromLibrary(url, "../output/", aslName);
	}

	public void downloadDriveTrace()
	{
		String url = url_list.getDriveTraceRetrieveTracesURL("download", "none");
		cxn.downloadFromLibrary(url, "../output/", "drive_traces.zip");	
	}

	public void downloadTrace(String traceType, String name)
	{
		String url = url_list.getTracesDownloadURL(traceType, name);
		cxn.downloadFromLibrary(url, "../output/", name);
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

		String url = url_list.getEtherLibStatusURL();
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
		
		String url = url_list.getASLGenerateURL();
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

		String url = url_list.getPackageResultsURL();
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

		String url = url_list.getSystemMessagesURL();
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
		String url = url_list.getMediaExchangeTAPStateURL(tap, drawer);
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
		String url = url_list.getTraceTypeURL(type);

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
				url = url_list.getSecurityAuditAbortURL();
				break;
			case "add-key":
				url = url_list.getOptionKeyAddURL(option1);
				break;
			case "audit-inventory":
				url = url_list.getInventoryAuditURL(option1, option2, option3);
				break;
			case "audit-inventory-result":
				url = url_list.getInventoryAuditResultsURL();
				break;
			case "audit-status":
				// Security audit, not inventory audit.
				url = url_list.getSecurityAuditStatusURL();
				break;
			case "controller-disable":
				url = url_list.getControllerDisableFailoverURL(option1);
				break;		
			case "controller-enable":
				url = url_list.getControllerEnableFailoverURL(option1, option2);
				break;
			case "create-partition-auto":
				url = url_list.getPartitionAutoCreateURL(option1, option2);
				break;
			case "delete-partition":
				url = url_list.getPartitionDeleteURL(option1, option2);
				break;
			case "download-drive-trace":
				url = url_list.getDriveTraceRetrieveTracesURL(option1, option2);
			case "empty-bulk-tap":
				// clean up the option values.
				option2 = convertTAPString(option2);
				url = url_list.getMediaExchangeCleanURL(option1, option2);
				break;
			case "gather-trace":
				url = url_list.getTracesGatherURL(option1, option3);
				break;
			case "generate-asl":
				url = url_list.getASLGenerateURL();
				break;
			case "generate-drive-trace":
				url = url_list.getDriveTracesURL(option1);
				break;
			case "lock-tension-rods":
				url = url_list.getUtilsLockTensionRodsURL(option1);
				break;
			case "modify-barcode-settings":
				url = url_list.getUtilsModifyBarcodeReportingURL(option1, option2, option3);
				break;
			case "modify-tape-verification":
				url = url_list.getUtilsModifyTapeBarcodeVerifyURL(option1);
				break;
			case "move-result":
				url = url_list.getInventoryMoveResultURL(option1);
				break;
			case "power-off":
				url = url_list.getPowerOffURL(option3);
				break;
			case "refresh-ec-info":
				url = url_list.getLibraryRefreshECInfoURL();
				break;
			case "refresh-environment":
				url = url_list.getLibraryRefreshEnvironmentURL();
				break;
			case "refresh-etherlib":
				url = url_list.getEtherLibRefreshURL();
				break;
			case "remove-all-partitions":
				url = url_list.getUtilsRemoveAllPartitionsURL();
				break;
			case "replace-drive":
				url = url_list.getDriveTraceReplaceDriveURL(option1);
				break;
			case "reset-controller":
				url = url_list.getUtilsResetControllerURL(option1);
				break;
			case "reset-drive":
				url = url_list.getDriveTraceResetDriveURL(option1);
				break;
			case "reset-inventory":
				url = url_list.getUtilsResetInventoryURL();
				break;
			case "reset-robot":
				url = url_list.getUtilsResetRobotURL(option1);
				break;
			case "reset-robot-calibration":
				url = url_list.getUtilsResetRobotCalibrationURL(option3);
				break;
			case "resize-partition":
				url = url_list.getPartitionResizeSlotsURL(option1, option2, option3);
				break;
			case "return-from-service":
				url = url_list.getRobotReturnFromServiceURL(option3);
				break;
			case "save-robot-state":
				url = url_list.getUtilsRobotStateURL(option3);
				break;
			case "selective-snowplow":
				url = url_list.getUtilsSetSnowplowURL(option1);
				break;
			case "send-to-service":
				url = url_list.getRobotSendToServiceURL(option3);
				break;
			case "set-mlm":
				option1 = convertMLMSetting(option1);
				url = url_list.getMLMSettingUpdateURL(option1, option3);
				break;
			case "stage-package":
				url = url_list.getPackageStageURL(option1);
				break;
			case "start-audit":
				// security audit, not inventory audit.
				url = url_list.getSecurityAuditStartURL();
				break;
			case "update-package":
				url = url_list.getPackageUpdateURL(option1);
				break;
			case "update-setting":
				url = url_list.getLibraryUpdateSettingURL(option1, option3);
				break;
			case "verify-mag-barcodes":
				url = url_list.getUtilsVerifyMagazineBarcodesURL();
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

		String url = url_list.getLibraryMoveDetailsURL();
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

		String url = url_list.getLibraryRCMStatusURL(rcm);
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


		String url = url_list.getLibraryStatusURL();
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
		
		String url = url_list.getASLNamesURL();
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

		String url = url_list.getControllerListURL();
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

		String url = url_list.getDriveListURL();
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

		String url = url_list.getHHMListURL();
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
	
		String url = url_list.getInventoryListURL(partition);
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

		String url = url_list.getMLMSettingsListURL();
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

		String url = url_list.getOptionKeyListURL();
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

		String url = url_list.getPackageListURL();
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

		String url = url_list.getPackageDetailsURL(pack);
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

		String url = url_list.getPackageFirmwareURL();
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
			
		String url = url_list.getPartitionListURL();
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

		String url = url_list.getPartitionNamesListURL();
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

		String url = url_list.getLibrarySettingsURL();
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

		String url = url_list.getTaskListURL();
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

		String url = url_list.getTracesNamesURL(traceType);
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

		String url = url_list.getLoginURL(user, password);
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

		String url = url_list.getLogoutURL();
		xmlOutput = cxn.queryLibrary(url);
		
		return true;
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

		String url = url_list.getMoveURL(partition, sourceID, sourceNumber, destID, destNumber);
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

		String url = url_list.getPhysicalInventoryURL(partition);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "magazine", true);
		}

		return response;
	}

	public XMLResult[] prepareImportExportList(String partition, String domain, String terapacks)
	{
		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status", "message"};

		String url = url_list.getImportExportListURL(partition, domain, terapacks);
		String xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		XMLResult[] response = xmlparser.parseXML(searchTerms);

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

			String url = url_list.getHHMResetCounterURL(type, subtype, robot);
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

		String url = url_list.getRobotUtilizationURL();
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

			String url = url_list.getHHMResetCounterURL(event, keepDefault, value);
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
		String url = url_list.getPostPackageUpdateURL();
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
}


