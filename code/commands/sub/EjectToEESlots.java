//===================================================================
// EjectTapesToEESlots.java
// 	Description:
// 		Moves listed tapes to the EE Slots.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class EjectToEESlots
{
	public static ArrayList<Move> fromTapeList(String file_path, int maxMoves, ArrayList<String> ee_slots, Logger log)
	{
		log.INFO("Calling LoadFile.tape_list(" + file_path + ")...");
		ArrayList<String> tape_list = LoadFile.tapeList(file_path);
		ArrayList<Move> move_list = new ArrayList<Move>();
		Move move;

		int itr = 0;

		while((itr < tape_list.size()) && (itr < ee_slots.size()) && (itr < maxMoves))
		{
			move = new Move();
			
			log.INFO("Queueing move: barcode [" + tape_list.get(itr) 
				+ "] to EE slot " + (itr + 1));

			move.barcode = tape_list.get(itr);
			move.target_type = "EE";
			move.target_slot = ee_slots.get(itr);
			move_list.add(move);

			itr++;
		}

		EjectListedTapes.exportRemainingTapes(tape_list, itr, file_path);

		return move_list;	
	}
}
