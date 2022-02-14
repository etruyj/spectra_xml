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
import java.util.HashMap;
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
				log.log("CHECK: Tape (" + tape_list.get(itr).value + ") in slot " + tape_list.get(itr).slot + " belongs in slot " + (itr + 1) + ".", 1);
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

	//=======================================
	// Algorithm 2
	//=======================================

	public static ArrayList<Move> alphabetizeInventory(XMLResult[] inv, int max_moves, Logger logs)
	{
		logs.INFO("Organizing inventory alphabetically...");

		ArrayList<String> empty_slots = Inventory.findEmptySlots(inv);
		ArrayList<String> all_slots = Inventory.findStorageSlots(inv);

		logs.INFO("There are (" + empty_slots.size() + ") empty slots in the library.");

		if(empty_slots.size()>0)
		{
			logs.INFO("Mapping inventory...");
			HashMap<String, String> tape_slot_map = mapSlotsToTapes(inv, empty_slots, all_slots);

			ArrayList<Move> move_list = queueMoves(tape_slot_map, empty_slots, all_slots, max_moves, logs);

			return move_list;
		}
		else
		{
			logs.ERR("Unable to organize inventory. At least 1 empty slot is required.");
		
			return null;
		}
	}

	public static Move completionCheck(HashMap<String, String> tape_map, ArrayList<String> all_slots, String empty_slot)
	{
		Move move = null;

		for(int i=0; i<tape_map.size()/4; i++)
		{
			// Iterate through the list by slot.
			// Compare to see if the tape's source == it's target
			// If not, move it to the last open slot to allow more moves.

			// target slot = tape_map('t' + barcode) || barcode = tape_map('s' + slot)
			if(!all_slots.get(i).equals(tape_map.get("t" + tape_map.get('s' + all_slots.get(i)))))
			{
				move = new Move();
				move.barcode = tape_map.get('s' + all_slots.get(i));
				move.source_type = "SLOT";
				move.source_slot = all_slots.get(i);
				move.target_type = "SLOT";
				move.target_slot = empty_slot;

				updateMap(tape_map, move.barcode, empty_slot);

				return move;
			}
		}

		return move;
	}

	public static HashMap<String, String> mapSlotsToTapes(XMLResult[] inv, ArrayList<String> empty_slots, ArrayList<String> all_slots)
	{
		// This one is complex. We're creating a map of source:barcode, barcode:source, target:barcode, 
		// and barcode:target to allow us to quickly parse the information. To differentiate between
		// the two values 's' will be prepended to the slot or barcode for the source pairs and 't' will
		// be prepended for the target pairs. This reduces the number of variables being passed around. 

		// grab a list of all slots from the inventory as slots aren't re-indexed
		// after TeraPacks are removed from the library.
		ArrayList<String> tape_list = Inventory.findTapes(inv);

		HashMap<String, String> tape_slot_map = new HashMap<String, String>();

		// Source Mappping
		// Act on the unsorted tape list.
		int slot;
		int empty;
		int e = 0;
		int s = 0;
		int t = 0;
		while(t<tape_list.size())
		{
			slot = Integer.valueOf(all_slots.get(s));
			empty = Integer.valueOf(empty_slots.get(e));

			if(slot<empty)
			{
				// Map slot to tape and tape to slot.
				// use the 's' prefix for the key.
				tape_slot_map.put("s" + all_slots.get(s), tape_list.get(t));
				tape_slot_map.put("s" + tape_list.get(t), all_slots.get(s));
				t++;
			}
			else if(slot == empty)
			{
				// This slot is empty. Skip.
				if(e < empty_slots.size())
				{
					e++;
				}
			}

			// Increment slot at the end.
			if(s<all_slots.size())
			{
				s++;
			}
		}

		// Alphabetize barcodes
		Collections.sort(tape_list);
	
		for(int i=0; i<tape_list.size(); i++)
		{
			// Assuming the number of tapes in the storage partition is less than the
			// number of slots in the storage partition. If invalid index occurs on all_slots,
			// there's an issue with the code.
			tape_slot_map.put("t" + all_slots.get(i), tape_list.get(i));
			tape_slot_map.put("t" + tape_list.get(i), all_slots.get(i));
		}

		if((tape_slot_map.size()/4) != tape_list.size())
		{
			System.err.println("ERROR: Map constructed incorrectly.");
		}

		return tape_slot_map;
	}

	

	public static ArrayList<Move> queueMoves(HashMap<String, String> tape_slot_map, ArrayList<String> empty_slots, ArrayList<String> all_slots, int max_moves, Logger log)
	{
		ArrayList<Move> move_list = new ArrayList<Move>();
		Move move;
		boolean finished = false;
		int move_counter = 0;
		int tape_count = tape_slot_map.size()/4; // As there are 4 entries per tape (source:tape, target:tape, tape:source, tape:target)

		log.INFO("Queuing moves...");

		while((move_counter < max_moves) && !finished)
		{
			// <= is used as slots start at index 1 not 0.
			if(Integer.valueOf(empty_slots.get(move_counter)) <= tape_count)
			{
				move = new Move();
				move.barcode = tape_slot_map.get("t" + empty_slots.get(move_counter));
				move.source_type = "SLOT";
				move.source_slot = tape_slot_map.get("s" + move.barcode);
				move.target_type = "SLOT";
				move.target_slot = empty_slots.get(move_counter);
				
				// Add the last move to the back of the list.
				empty_slots.add(move_counter+1, tape_slot_map.get('s' + move.barcode));		
		
				// update the tape positions in the map
				updateMap(tape_slot_map, move.barcode, move.target_slot);

				move_list.add(move);
				move_counter++;
			}
			else
			{
				// check to see if complete.
				log.INFO("CHECKING COMPLETION (" + move_counter + ")...");
			
				move = completionCheck(tape_slot_map, all_slots, empty_slots.get(empty_slots.size()-1));

				if(move != null)
				{
					log.INFO("Tape [" + move.barcode + "] in slot " + move.source_slot + " belongs in slot " + tape_slot_map.get('t' + move.barcode));
				       	log.INFO("Moving " + move.barcode + " to slot " + empty_slots.get(empty_slots.size()-1));

					// Add new available slot and remove the old from the list.
					empty_slots.add(move_counter+1, move.source_slot);
					empty_slots.remove(empty_slots.size()-1);

					move_list.add(move);
					move_counter++;
				}
				else
				{
					log.INFO("Inventory sort complete. Move list is prepared.");
					
					finished = true;
				}
			}
		}
	
		if(!finished)
		{
			log.INFO("Queued " + move_counter + "/" + max_moves + " moves. Library inventory requires more sorting.");
		}

		return move_list;
	}

	public static void updateMap(HashMap<String, String> tape_map, String barcode, String target)
	{
		// Update the source location to where the tape is being moved.
		tape_map.put("s" + target, barcode);
		tape_map.put("s" + barcode, target);
	}
}
