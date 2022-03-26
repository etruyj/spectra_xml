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
			log.WARN("CHECK: All tapes are in the correct position");
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
		ArrayList<String> tape_list = Inventory.findTapes(inv);	

		logs.INFO("There are (" + empty_slots.size() + ") empty slots in the library.");

		if(empty_slots.size()>0)
		{
			logs.INFO("Mapping inventory...");
			
			// Build Source Map
			HashMap<String, String> tape_slot_map = mapSourceSlots(empty_slots, all_slots, tape_list);
			
			// Alphabetize Tapes
			Collections.sort(tape_list);

			// Build Target Map
			tape_slot_map.putAll(mapTargetSlots(all_slots, tape_list));

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
			// also ensuring tape_map has a value for the specified key. Otherwise the slot should be empty.
			if(!all_slots.get(i).equals(tape_map.get("t" + tape_map.get("s" + all_slots.get(i)))))			{
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

	public static HashMap<String, String> mapSourceSlots(ArrayList<String> empty_slots, ArrayList<String> all_slots, ArrayList<String> tape_list)
	{
		// This one is complex. We're creating a map of source:barcode, barcode:source, target:barcode, 
		// and barcode:target to allow us to quickly parse the information. To differentiate between
		// the two values 's' will be prepended to the slot or barcode for the source pairs and 't' will
		// be prepended for the target pairs. This reduces the number of variables being passed around. 

		HashMap<String, String> tape_slot_map = new HashMap<String, String>();

		// Source Mappping
		// Act on the unsorted tape list.
		int slot;
		int empty;
		int e = 0; // (e)mpty Slot Iterator
		int s = 0; // inventory (s)lot interator
		int t = 0; // placed (t)ape counter.

		while(t<tape_list.size())
		{
			// Initiate values:
			// 	Get inventory slot for the slot at postition (s)
			// 	Get the empty slot for the empty slot at position (e)
			slot = Integer.valueOf(all_slots.get(s));
			empty = Integer.valueOf(empty_slots.get(e));
			
			//System.err.println(e + "/" + empty_slots.size() + "\t" + s + "/" + all_slots.size() + "\t" + t + "/" + tape_list.size() + "\t" + slot + ":" + empty);

			// If the slot isn't empty:
			// 	 add it to the queue
			// 	 	Mapped source slot to tape.
			// 	 	Mapped tape to source slot.
			// 	 incremet tape counter
			// If the slot is empty:
			// 	don't add it to the queue.
			// 	increment empty slot iterator
			//
			// Increment the slot at the end.

			if(slot!=empty)
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
				if(e < empty_slots.size()-1)
				{
					e++;
				}
			}

			// Increment slot at the end.
			if(s<all_slots.size()-1)
			{
				s++;
			}
		}

		return tape_slot_map;
	}

	public static HashMap<String, String> mapTargetSlots(ArrayList<String> all_slots, ArrayList<String> tape_list)
	{
		HashMap<String, String> tape_slot_map = new HashMap<String, String>();

		for(int i=0; i<tape_list.size(); i++)
		{
			// Assuming the number of tapes in the storage partition is less than the
			// number of slots in the storage partition. If invalid index occurs on all_slots,
			// there's an issue with the code.
			tape_slot_map.put("t" + all_slots.get(i), tape_list.get(i));
			tape_slot_map.put("t" + tape_list.get(i), all_slots.get(i));
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
				if(Integer.valueOf(tape_slot_map.get('s' + move.barcode)) <= tape_count)
				{
					empty_slots.add(move_counter+1, tape_slot_map.get('s' + move.barcode));		
				}
				else
				{
					empty_slots.add(tape_slot_map.get('s' + move.barcode));
				}

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

	//===========================================================
	//	GroupListed
	//		This is an alternative and hopefully faster
	//		method to group the listed number of tapes.
	//
	//		It creates two lists of tapes, to be ejected
	//		and to remain and appends them after one
	//		another with the required gap to make sure
	//		the two sets don't share a TeraPack.
	//
	//		// Inventory: [not listed][gap][listed]
	//===========================================================
	
	public static ArrayList<Move> groupListed(XMLResult[] inv, String file_path, int magazine_size, int max_moves, Logger logs)
	{
		logs.INFO("Grouping listed tapes at beginning of inventory...");

		ArrayList<String> empty_slots = Inventory.findEmptySlots(inv);
		ArrayList<String> all_slots = Inventory.findStorageSlots(inv);
		ArrayList<String> tape_list = Inventory.findTapes(inv);
		ArrayList<String> tapes_to_eject = LoadFile.tapeList(file_path);

		logs.INFO("There are (" + empty_slots.size() + ") empty slots in the library.");

		if(empty_slots.size()>0)
		{
			logs.INFO("Mapping inventory...");

			// Map Source Slots
			System.err.println("tape_slot_map");
			HashMap<String, String> tape_slot_map = mapSourceSlots(empty_slots, all_slots, tape_list);

			// Organize Tape List
			System.err.println("tape_list");
			tape_list = sortOutListedTapes(tape_list, tapes_to_eject);			

			// Map Target Slots
			System.err.println("target_slots");
			tape_slot_map.putAll(mapTargetSlots(all_slots, tape_list));

			// Add first moves.
			System.err.println("move_list");
			ArrayList<Move> move_list = queueMoves(tape_slot_map, empty_slots, all_slots, max_moves, logs);

			// Create the gap slots if moves remain.
			if(move_list.size() < max_moves)
			{
				int first_empty_slot = (tape_list.size() - tapes_to_eject.size());
				int number_of_empties = magazine_size - (first_empty_slot % magazine_size);
				
				int itr=0;

				while(itr < empty_slots.size())	
				{
					if(Integer.valueOf(empty_slots.get(itr)) < (first_empty_slot + number_of_empties + 1))
					{
						empty_slots.remove(itr);
					}
					else
					{
						itr++;
					}
				}
				
				Collections.sort(empty_slots);

				if(number_of_empties < empty_slots.size())
				{
					logs.INFO("Clearing gap slots to ensure magazine isolation...");
					move_list.addAll(queueEmptySlotMoves(first_empty_slot+1, number_of_empties, empty_slots, max_moves - move_list.size()));
				}
				else
				{
					logs.WARN("Unable to clear gap slots. Grouped tapes share a magazine.");
					System.err.println("Unable to clear gap slots. Grouped tapes share a magazine.");
				}
			}

			return move_list;
		}
		else
		{
			logs.ERR("Unable to organize inventory. At least 1 empty slot is required.");
		
			return null;
		}
	}

	public static ArrayList<Move> queueEmptySlotMoves(int start_slot, int empties, ArrayList<String> empty_slots, int moves_remaining)
	{
		ArrayList<Move> final_moves = new ArrayList<Move>();
		Move move;


		for(int i = 0;  i < empties; i++)
		{
		
			move = new Move();
			move.source_type = "SLOT";
			move.source_slot = String.valueOf((start_slot + i));
			move.target_type = "SLOT";
			move.target_slot = empty_slots.get(i);

			final_moves.add(move);
		}

		return final_moves;
	}

	public static ArrayList<String> sortOutListedTapes(ArrayList<String> all_tapes, ArrayList<String> listed_tapes)
	{
		ArrayList<String> sorted_tapes = new ArrayList<String>();
		
		// Step 1: Add Gap Slots
		/*
		 * Skipping this step for initial organization.
		int gap_slots = listed_tapes.size() % magazine_size; // number of slots occupied by listed tapes.
		gap_slots = magazine_size - gap_slots; // number of slots that need to be filled.

		if(gap_slots != magazine_size)
		{
			for(int i=0; i<gap_slots; i++)
			{
				sorted_tapes.add("empty");
			}
		}
		*/

		// Step 2: Sort all_tapes alphabetically to guarantee inventory order.
		Collections.sort(all_tapes);		

		// Step 3: If tape is in listed_tapes insert before gap, if not, insert after.
		for(int i=0; i<all_tapes.size(); i++)
		{
			if(listed_tapes.contains(all_tapes.get(i)))
			{
				// Tape is listed
				sorted_tapes.add(all_tapes.get(i));
			}
			else
			{
				// Tape is not listed
				sorted_tapes.add(0, all_tapes.get(i));
			}
		}

		return sorted_tapes;
	}
}
