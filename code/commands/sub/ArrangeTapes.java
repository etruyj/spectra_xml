//===================================================================
// ArrangeTapes.java
// 	Sort the tapes so the barcodes are in numeric order.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.spectraxml.structures.SlotPair;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class ArrangeTapes
{
	public static ArrayList<SlotPair> getOrderedBarcodeList(XMLResult[] inv)
	{
		ArrayList<SlotPair> ordered_tapes = new ArrayList<SlotPair>();
		SlotPair location;
		boolean searching = true;
		int itr = 0;
		String checkslot = "0";

		// Build a list of all tapes.
		while(searching)
		{
			if(inv[itr].headerTag.equalsIgnoreCase("partition>storageSlot>offset"))
			{
				checkslot = inv[itr].value;
			}

			// Grab a slot number
			if(inv[itr].headerTag.equalsIgnoreCase("partition>storageSlot>barcode"))
			{
				location = new SlotPair();
				location.slot = checkslot;
				location.value = inv[itr].value.trim();

				ordered_tapes.add(location);
			}

			itr++;

			// Stop searching if we hit the end of the array or if we move into the entry exit slots.

			if(itr>=inv.length || inv[itr].headerTag.equalsIgnoreCase("partition>entryExitSlot>id"))
			{
				searching = false;
			}
		}

		Collections.sort(ordered_tapes, Comparator.comparing(SlotPair::getValue));

		return ordered_tapes;
	}

	public static ArrayList<Move> queueMoves(ArrayList<SlotPair> ordered_tapes, ArrayList<String> empty_slots, int max_moves, Logger log)
	{
		ArrayList<Move> move_list = new ArrayList<Move>();
		Move move;
		Move check_move = null;
		int slot;
		int freed_slot;
		int move_count=0;
		
		do
		{
			for(int i=0; i<empty_slots.size(); i++)
			{
				slot = Integer.valueOf(empty_slots.get(i)) - 1;
	
				if(slot < ordered_tapes.size() && move_count<max_moves && !empty_slots.get(i).equals(ordered_tapes.get(slot).slot))
				{
					move = new Move();
					move.barcode = ordered_tapes.get(slot).value;
					move.source_type = "SLOT";
					move.source_slot = ordered_tapes.get(slot).slot;
					move.target_type = "SLOT";
					move.target_slot = empty_slots.get(i);
	
					freed_slot = Integer.valueOf(move.source_slot);
					ordered_tapes.get(slot).slot = move.target_slot;

					if(freed_slot-1<ordered_tapes.size())
					{
						empty_slots.add(i+1, move.source_slot);
					}
					else
					{
						empty_slots.add(move.source_slot);
					}
	
					move_list.add(move);
					move_count++;
				}
			}
		
			log.log("(" + move_count + ") Checking for completion...", 1);	
			check_move = compareSlots(ordered_tapes, empty_slots.get(empty_slots.size()-1), log);

			if(check_move!=null && move_count<max_moves)
			{
				if(Integer.valueOf(check_move.source_slot)-1<ordered_tapes.size())
				{
					empty_slots.add(0, check_move.source_slot);
				}

				move_count++;
				move_list.add(check_move);
			}
			
		

		} while(check_move != null && move_count<max_moves);

		return move_list;
	}

	public static Move compareSlots(ArrayList<SlotPair> tape_list, String empty_slot, Logger log)
	{
		Move move = null;
		int itr = 0;

		while(move == null && itr < tape_list.size())
		{
			if(Integer.valueOf(tape_list.get(itr).slot) != itr+1)
			{
				log.log("CHECK: Tape (" + tape_list.get(itr).value + ") in slot " + tape_list.get(itr).slot + " belongs in slot " + itr + ".", 1);
				log.log("Queueing tape (" + tape_list.get(itr).value + ") to move to slot " + empty_slot, 1);

				move = new Move();
				move.barcode = tape_list.get(itr).value;
				move.source_type = "SLOT";
				move.source_slot = tape_list.get(itr).slot;
				move.target_type = "SLOT";
				move.target_slot = empty_slot;

				tape_list.get(itr).slot = move.target_slot;
			}
			itr++;
		}

		if(move==null)
		{
			log.log("CHECK: All tapes are in the correct position", 2);
		}

		return move;
	}
}
