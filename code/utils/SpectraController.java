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

	public boolean checkProgress(String operationName, boolean printToShell)
	{
		return library.checkProgress(operationName, printToShell);

	}

	public XMLResult[] displayBarcodeReporting(boolean printToShell)
	{
		return library.displayBarcodeReporting(printToShell);
	}

	public XMLResult[] displayBarcodeVerification(boolean printToShell)
	{
		return library.displayBarcodeVerification(printToShell);
	}

	public void driveLoadCount(String option, boolean printToShell)
	{
		library.driveLoadCount(option, printToShell);
	}

	public void downloadASL(String aslName, boolean printToShell)
	{
		library.downloadASL(aslName, printToShell);
	}

	public void downloadDriveTrace()
	{
		library.downloadDriveTrace();
	}

	public void downloadTrace(String traceType, String name)
	{
		library.downloadTrace(traceType, name);
	}

	public void ejectEmpty(String partition, boolean printToShell)
	{
		advanced.ejectEmpty(partition, printToShell);
	}

	public void etherLibStatus(boolean printToShell)
	{
		library.etherLibStatus(printToShell);
	}

	public void generateASL(boolean printToShell)
	{
		library.generateASL(printToShell);
	}

	public void getLibraryType(boolean printToShell)
	{
		advanced.getLibraryType(printToShell);
	}

	public XMLResult[] getPackageResults(boolean printToShell)
	{
		return library.getPackageResults(printToShell);
	}

	public XMLResult[] getSystemMessages(boolean printToShell)
	{
		return library.getSystemMessages(printToShell);
	}

	public void getTapState(String tap, String drawer, boolean printToShell)
	{
		library.getTapState(tap, drawer, printToShell);
	}

	public void getTraceType(String type, boolean printToShell)
	{
		library.getTraceType(type, printToShell);
	}

	public void getXMLStatusMessage(String query, String option1, String option2, String option3, boolean printToShell)
	{
		library.getXMLStatusMessage(query, option1, option2, option3, printToShell);
	}

	public XMLResult[] libraryMoveDetails(boolean printToShell)
	{
		return library.libraryMoveDetails(printToShell);
	}

	public XMLResult[] libraryRCMStatus(String rcm, boolean printToShell)
	{
		return library.libraryRCMStatus(rcm, printToShell);
	}

	public XMLResult[] libraryStatus(boolean printToShell)
	{
		return library.libraryStatus(printToShell);
	}

	public void listASLs(boolean printToShell)
	{
		library.listASLs(printToShell);
	}
	
	public void listControllers(boolean printToShell)
	{
		library.listControllers(printToShell);
	}

	public XMLResult[] listDrives(boolean printToShell)
	{
		return library.listDrives(printToShell);
	}

	public XMLResult[] listHHMData()
	{
		return library.listHHMData();
	}

	public XMLResult[] listInventory(String partition)
	{
		return library.listInventory(partition);
	}

	public XMLResult[] listMLMSettings(boolean printToShell)
	{
		return library.listMLMSettings(printToShell);
	}

	public XMLResult[] listOptionKeys(boolean printToShell)
	{
		return library.listOptionKeys(printToShell);
	}

	public XMLResult[] listPackages(boolean printToShell)
	{
		return library.listPackages(printToShell);
	}

	public XMLResult[] listPackageDetails(String pack, boolean printToShell)
	{
		return library.listPackageDetails(pack, printToShell);
	}

	public XMLResult[] listPackageFirmware(boolean printToShell)
	{
		return library.listPackageFirmware(printToShell);
	}
	
	public XMLResult[] listPartitionDetails(String option, boolean printToShell)
	{
		return library.listPartitionDetails(option, printToShell);
	}

	public XMLResult[] listPartitions()
	{
		return library.listPartitions();
	}

	public XMLResult[] listSettings(boolean printToShell)
	{
		return library.listSettings(printToShell);
	}

	public XMLResult[] listTasks(boolean printToShell)
	{
		return library.listTasks(printToShell);
	}

	public XMLResult[] listTraceNames(String traceType, boolean printToShell)
	{
		return library.listTraceNames(traceType, printToShell);
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

	public XMLResult[] moveTape(String partition, String sourceID, String sourceNumber, String destID, String destNumber, boolean printToShell)
	{
		return library.moveTape(partition, sourceID, sourceNumber, destID, destNumber, printToShell);
	}

	public XMLResult[] physicalInventory(String partition, boolean printToShell)
	{
		return library.physicalInventory(partition, printToShell);
	}

	public void resetHHMCounter(String type, String subtype, String robot, boolean printToShell)
	{
		library.resetHHMCounter(type, subtype, robot, printToShell);
	}

	public XMLResult[] robotUtilization(boolean printToShell)
	{
		return library.robotUtilization(printToShell);
	}

	public void setHHMThreshold(String event, String keepDefault, String value, boolean printToShell)
	{
		library.setHHMThreshold(event, keepDefault, value, printToShell);
	}

	public void uploadPackageUpdate(String filename, boolean printToShell)
	{
		library.uploadPackageUpdate(filename, printToShell);
	}

	public void prepareSlotIQ(String partition, int max_moves, String output_format, boolean printToShell)
	{
		advanced.prepareSlotIQ(partition, max_moves, output_format, printToShell);
	}
}


