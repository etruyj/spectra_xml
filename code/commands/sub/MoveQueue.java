//============================================================================
// MoveQueue.java
// 	Description:
//		This code handles the commands around creating a MoveQueue
//		output file.
//============================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

public class MoveQueue
{
	private Logger log;

	//====================================================================
	// Constructor
	//====================================================================
	
	public MoveQueue(Logger logbook)
	{
		log = logbook;
	}

	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================

	public void appendLine(String source_type, String source, String dest_type, String destination, String fileName)
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

	public boolean createFile(String fileName)
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

}


