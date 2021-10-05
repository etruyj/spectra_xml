//============================================================================
// SpectraController.java
// 	Description:
// 		This class functions as an overlay for the varios command calls
// 		in order to abstract the layout of the different subclasses from
// 		the interface.e
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
	
	public SpectraController(String server, boolean secure)
	{
		// Declared logger in SpectraController as opposed to 
		// in connector to allow logging of issues within the commands.
		logbook = new Logger("../logs/slxml-main.log", 102400, 3, 1);

		library = new BasicXMLCommands(server, secure, logbook);
		advanced = new AdvancedCommands(library, logbook);
	}

	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================

	public XMLResult[] checkProgress(String operationName)
	{
		return library.checkProgress(operationName);

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

	public XMLResult[] downloadASL(String aslName)
	{
		return library.downloadASL(aslName);
	}

	public XMLResult[] downloadDriveTrace()
	{
		return library.downloadDriveTrace();
	}

	public XMLResult[] downloadTrace(String traceType, String name)
	{
		return library.downloadTrace(traceType, name);
	}

	public void ejectEmpty(String partition, boolean printToShell)
	{
		advanced.ejectEmpty(partition, printToShell);
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

	public void getTraceType(String type)
	{
		library.getTraceType(type);
	}

	public XMLResult[] getXMLStatusMessage(String query, String option1, String option2, String option3)
	{
		return library.getXMLStatusMessage(query, option1, option2, option3);
	}

	public XMLResult[] libraryMoveDetails()
	{
		return library.libraryMoveDetails();
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

	public void magazineCompaction(String partition, int maxMoves, String output_type, boolean printToShell)
	{
		advanced.magazineCompaction(partition, maxMoves, output_type, printToShell);
	}

	public TeraPack[] magazineContents(String partition, boolean printToShell)
	{
		return advanced.magazineContents(partition, printToShell);
	}

	public void maintenanceHHMReset(boolean printToShell)
	{
		advanced.maintenanceHHMReset(printToShell);
	}

	public void moveListAppendLine(String source_type, String source, String dest_type, String destination, String fileName)
	{
		advanced.moveListAppendLine(source_type, source, dest_type, destination, fileName);
	}

	public boolean moveListCreateFile(String fileName)
	{
		return advanced.moveListCreateFile(fileName);
	}

	public XMLResult[] moveTape(String partition, String sourceID, String sourceNumber, String destID, String destNumber)
	{
		return library.moveTape(partition, sourceID, sourceNumber, destID, destNumber);
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

