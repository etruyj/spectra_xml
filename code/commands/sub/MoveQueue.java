//============================================================================
// MoveQueue.java
// 	Description:
//		This code handles the commands around creating a MoveQueue
//		output file.
//============================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.structures.Move;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class MoveQueue
{
	public static void appendLine(String source_type, String source, String dest_type, String destination, String fileName)
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

	public static boolean createFile(String fileName)
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

	public static void storeMoves(String fileName, ArrayList<Move> move_list, Logger log)
	{
		// Call this function to create the file and store the moves.

		createFile(fileName);

		for(int i=0; i<move_list.size(); i++)
		{
			if(!move_list.get(i).source_slot.equals("none"))
			{
				appendLine(move_list.get(i).source_type, move_list.get(i).source_slot, move_list.get(i).target_type, move_list.get(i).target_slot, fileName);
			}
			else
			{
				appendLine("BC", move_list.get(i).barcode, move_list.get(i).target_type, move_list.get(i).target_slot, fileName);
			}
		}

		log.INFO("Wrote " + move_list.size() + " moves to file.");
		System.err.println("Wrote " + move_list.size() + " moves to file.");

		System.err.println("\nGeneration of move queue is complete. The file can be found at " + fileName + ". Upload the move queue to the library either by USB or the web GUI. When useing USB, the file must by named MoveQueue.txt and placed in the root (/) directory to be uploaded. The moved queue can be uploaded from the Web GUI from the Inventory > Advanced menu.\n");
	}
}


