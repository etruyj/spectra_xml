//============================================================================
// SpectraController.java
// 	Description:
// 		This class functions as an overlay for the varios command calls
// 		in order to abstract the layout of the different subclasses from
// 		the interface. It also handles data validation for the various
// 		inputs before handing them off to the commands.
//============================================================================

package com.socialvagrancy.spectraxml.utils;

import com.socialvagrancy.spectraxml.commands.AdvancedCommands;
import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.TeraPack;
import com.socialvagrancy.spectraxml.structures.XMLResult;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectraController
{
	private Logger logbook;
	private AdvancedCommands advanced;
	private BasicXMLCommands library;

	//====================================================================
	// Constructor
	//====================================================================
	
	public SpectraController(String server, boolean secure, boolean ignoreSSL, String log_path, int log_level, int log_size, int log_count)
	{
		// Declared logger in SpectraController as opposed to 
		// in connector to allow logging of issues within the commands.
		logbook = new Logger(log_path, log_size, log_count, log_level);

		library = new BasicXMLCommands(server, secure, ignoreSSL, logbook);
		advanced = new AdvancedCommands(library, logbook);
	}

	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================

	public void calibrateDrives(String partition, String output_format, boolean printToShell)
	{
		advanced.calibrateDrives(partition, output_format, printToShell);
	}

	public XMLResult[] checkProgress(String operationName)
	{
		return library.checkProgress(operationName);

	}

	public void createPartition(String file_name, boolean printToShell)
	{
		advanced.createPartition(file_name, printToShell);
	}

	public XMLResult[] displayBarcodeReporting()
	{
		return library.displayBarcodeReporting();
	}

	public XMLResult[] displayBarcodeVerification()
	{
		return library.displayBarcodeVerification();
	}

	public XMLResult[] driveLoadCount(String option)
	{
		return library.driveLoadCount(option);
	}

	public XMLResult[] driveStatus(String partition)
	{
		return advanced.driveStatus(partition);
	}

	public XMLResult[] downloadASL(String aslName)
	{
		return library.downloadASL(aslName);
	}

	public XMLResult[] downloadDriveTrace()
	{
		return library.downloadDriveTrace();
	}

	public XMLResult[] downloadMLMReport(String output_path)
	{
		return library.downloadMLMReport(output_path);
	}

	public XMLResult[] downloadTrace(String traceType, String name)
	{
		return library.downloadTrace(traceType, name);
	}

	public void downloadXMLSheet(String doc, String option1, String save_path, String file_name)
	{
		// Temporary script to add download XML functionality
		library.saveXML(doc, option1, save_path, file_name);
	}

	public void ejectEmpty(String partition, boolean printToShell)
	{
		advanced.ejectEmpty(partition, printToShell);
	}

	public void ejectListed(String partition, String file_name, int max_moves, String output_format, boolean printToShell)
	{
		if(output_format.equals("shell"))
		{
			advanced.ejectListedTapes(partition, file_name, printToShell);
		}
		else
		{
			advanced.ejectToEE(partition, file_name, max_moves, output_format, printToShell);
		}
	}

	public void ejectTeraPack(String partition, String tape, String terapack, boolean printToShell)
	{
		advanced.ejectTeraPack(partition, terapack, tape, printToShell);
	}

	public XMLResult[] etherLibStatus()
	{
		return library.etherLibStatus();
	}

	public XMLResult[] generateASL()
	{
		return library.generateASL();
	}

	public void getLibraryType(boolean printToShell)
	{
		advanced.getLibraryType(printToShell);
	}

	public XMLResult[] getPackageResults()
	{
		return library.getPackageResults();
	}

	public XMLResult[] getSystemMessages()
	{
		return library.getSystemMessages();
	}

	public XMLResult[] getTapState(String tap, String drawer)
	{
		return library.getTapState(tap, drawer);
	}

	public XMLResult[] getTraceType(String type, String controller)
	{
		return library.getTraceType(type, controller);
	}

	public XMLResult[] getXMLStatusMessage(String query, String option1, String option2, String option3, String option4, String option5)
	{
		return library.getXMLStatusMessage(query, option1, option2, option3, option4, option5);
	}

	public void groupListedTapes(String partition, String file_name, int max_moves, String output_format, boolean printToShell)
	{
		advanced.groupListedTapes(partition, file_name, max_moves, output_format, printToShell);
	}

	public XMLResult[] libraryMoveDetails()
	{
		return library.libraryMoveDetails();
	}

	public void libraryProfile(boolean printToShell)
	{
		advanced.profileLibrary(printToShell);
	}
	
	public XMLResult[] libraryRCMStatus(String rcm)
	{
		return library.libraryRCMStatus(rcm);
	}

	public XMLResult[] libraryStatus()
	{
		return library.libraryStatus();
	}

	public XMLResult[] listASLs()
	{
		return library.listASLs();
	}
	
	public XMLResult[] listControllers()
	{
		return library.listControllers();
	}

	public XMLResult[] listDrives()
	{
		return library.listDrives();
	}

	public XMLResult[] listHHMData()
	{
		return library.listHHMData();
	}

	public XMLResult[] listInventory(String partition)
	{
		return library.listInventory(partition);
	}

	public XMLResult[] listMLMSettings()
	{
		return library.listMLMSettings();
	}

	public XMLResult[] listOptionKeys()
	{
		return library.listOptionKeys();
	}

	public XMLResult[] listPackages()
	{
		return library.listPackages();
	}

	public XMLResult[] listPackageDetails(String pack)
	{
		return library.listPackageDetails(pack);
	}

	public XMLResult[] listPackageFirmware()
	{
		return library.listPackageFirmware();
	}
	
	public XMLResult[] listPartitionDetails(String option)
	{
		return library.listPartitionDetails(option);
	}

	public XMLResult[] listPartitions()
	{
		return library.listPartitions();
	}

	public XMLResult[] listSettings()
	{
		return library.listSettings();
	}

	public XMLResult[] listTasks()
	{
		return library.listTasks();
	}

	public XMLResult[] listTraceNames(String traceType)
	{
		return library.listTraceNames(traceType);
	}

	public boolean login(String user, String password)
	{
		return library.login(user, password);
	}

	public boolean logout()
	{
		return library.logout();
	}

	public void magazineCapacity(String partition, boolean printToShell)
	{
		advanced.magazineCapacity(partition, printToShell);
	}

	public void magazineCompaction(String partition, int maxMoves, boolean verify_moves, String output_type, boolean printToShell)
	{
		advanced.magazineCompaction(partition, maxMoves, verify_moves, output_type, printToShell);
	}

	public TeraPack[] magazineContents(String partition, boolean printToShell)
	{
		return advanced.magazineContents(partition, printToShell);
	}

	public void maintenanceHHMReset(boolean printToShell)
	{
		advanced.maintenanceHHMReset(printToShell);
	}

	public XMLResult[] mediaExchange(String partition, String slot, String terapacks, String tap, String timeout)
	{
		return library.mediaExchange(partition, slot, tap, timeout, terapacks);
	}

	public XMLResult[] moveTape(String partition, String sourceID, String sourceNumber, String destID, String destNumber)
	{
		return library.moveTape(partition, sourceID, sourceNumber, destID, destNumber);
	}

	public void organizeTapes(String partition, String file_path, int max_moves, String output_format, boolean printToShell)
	{
		advanced.arrangeTapes(partition, file_path, max_moves, output_format, printToShell);
	}

	public XMLResult[] physicalInventory(String partition)
	{
		return library.physicalInventory(partition);
	}

	public XMLResult[] resetHHMCounter(String type, String subtype, String robot)
	{
		return library.resetHHMCounter(type, subtype, robot);
	}

	public XMLResult[] robotUtilization()
	{
		return library.robotUtilization();
	}

	public XMLResult[] setHHMThreshold(String event, String keepDefault, String value)
	{
		return library.setHHMThreshold(event, keepDefault, value);
	}

	public XMLResult[] uploadPackageUpdate(String filename)
	{
		return library.uploadPackageUpdate(filename);
	}

	public void prepareSlotIQ(String partition, int max_moves, String output_format, boolean printToShell)
	{
		advanced.prepareSlotIQ(partition, max_moves, output_format, printToShell);
	}
}


