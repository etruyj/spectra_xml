//============================================================================
// SpectraController.java
// 	Description:
// 		This class makes all the appropriate function calls to the tape
// 		library. 
//============================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.commands.sub.VerifyMove;
import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.spectraxml.structures.TeraPack;
import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class SlotIQ
{
	public static ArrayList<Move> prepareMoves(BasicXMLCommands library, String partition, TeraPack[] mags, int max_slots_per_terapack, int max_moves, Logger log, boolean printToShell)
	{
		int source_terapack = mags.length-1;
		int target_terapack = mags.length-1;
		int moves_to_empty_terapack = 0; // tracking movement into empty terapacks.
		int move_counter = 0;
		boolean moving_tapes = true;
		Move move;

		ArrayList<String> empty_slots = Inventory.findEmptyTeraPacks(library, partition, max_slots_per_terapack);
		ArrayList<Move> move_list = new ArrayList<Move>();

		while(moving_tapes)
		{
			// Find next destination terapack.
			target_terapack = findNextAvailableTerapack(mags, max_slots_per_terapack, target_terapack);

			log.log("TeraPack (" + mags[target_terapack].getMagazineBarcode() + ") identified with " + mags[target_terapack].getCapacity() + "/" + max_slots_per_terapack + " slots occupied.", 1);

			// Determine if moves are possible or end the loop.
			if(target_terapack == -1 || mags[source_terapack].getCapacity() < max_slots_per_terapack || move_counter >= max_moves)
			{
				if(target_terapack == -1)
				{
					log.log("Error: Unable to find a destination TeraPack.", 3);
				}
				else if(move_counter >= max_moves)
				{
					log.log("Maximum requested moves (" + max_moves + ") has been reached.", 3);

					if(printToShell)
					{
						System.out.println("\nMaximum requested moves (" + max_moves + ") has been reached.");
					}
				}
				else
				{
					log.log("SlotIQ preparation complete. All TeraPacks have at least 1 open slot.", 3);

					if(printToShell)
					{
						System.out.println("\nSlotIQ preparation complete. All TeraPacks have at least 1 open slot.");
					}
				}
				moving_tapes = false;
			}
			else
			{
				if(printToShell)
				{
					// Starts with newline character to space between outputs for readability.
					System.out.println("\nPreparing move " + (move_counter + 1) + "...");
				}

				if(mags[target_terapack].getCapacity()>0)
				{
					// There is a tape in the TeraPack to use as an anchor
					move = queueMovesToOccupied(mags, max_slots_per_terapack, source_terapack, target_terapack, library, partition, log, printToShell);
				}
				else
				{
					// Terapack is empty, so use empty terapack slots.
					// (empty_slots)
					moves_to_empty_terapack++;
					move = queueMovesToEmpty(mags, max_slots_per_terapack, source_terapack, target_terapack, empty_slots.get(0), moves_to_empty_terapack, library, partition, log);
					empty_slots.remove(0);

					if(moves_to_empty_terapack==(max_slots_per_terapack-1))
					{
						// If moves_to_empty_terapack = max_slots - 1, there is only 1 slot left in the TeraPack.
						// Terapack capacity was incremented to ensure we move to the next target terapack.
						// Reset this counter to 0.
						moves_to_empty_terapack=0;
					}
				}
			
				// Only queue valid moves
				if(!move.source_slot.equals("none"))
				{	
					move_list.add(move);
				}

				move_counter++;
				source_terapack--;
			}
		}	

		return move_list;
	}

	private static int findNextAvailableTerapack(TeraPack[] mags, int mag_size, int index)
	{
		if(mags[index].getCapacity()<mag_size-1)
		{
			return index;
		}
		else if(index-1>=0)
		{
			return findNextAvailableTerapack(mags, mag_size, index-1);
		}
		else
		{
			return -1;
		}
	}

	public static boolean isPossible(TeraPack[] mags, String mediaType, boolean printToShell)
	{
		int tapes_to_move = 0;
		int slots_available = 0;
		int max_slots_per_magazine;

		if(mediaType.equals("LTO"))
		{
			max_slots_per_magazine = 10;
		}
		else
		{
			max_slots_per_magazine = 9;
		}

		for(int i=mags.length-1; i>=0; i--)
		{
			if(mags[i].getCapacity()==max_slots_per_magazine && mags[i].getLocation().equals("storage"))
			{
				tapes_to_move++;
			}
			else if(mags[i].getLocation().equals("storage"))
			{
				// Add the amount of available slots in the TeraPack - 1 to the available slots.
				// We need the -1 because this terapack also needs an open slot for slot iq
				slots_available = slots_available + (max_slots_per_magazine - mags[i].getCapacity() - 1);
			}
		}

		if(printToShell)
		{
			System.out.println(tapes_to_move + " moves are needed to prepare this partition for Slot IQ.");
			System.out.println("There are " + slots_available + " slots available for this task.");
		}

		if(tapes_to_move <= slots_available)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private static Move queueMovesToEmpty(TeraPack[] mags, int magazine_size, int source_terapack, int target_terapack, String target_slot, int slot_number, BasicXMLCommands library, String partition, Logger log)
	{
	
		String source_barcode = mags[source_terapack].getBarcodeAtPosition(0);
		String source_slot = VerifyMove.findSlotString(partition, source_barcode, library);

		Move move = new Move();

		move.barcode = source_barcode;
		move.source_type = "SLOT";
		move.source_slot = source_slot;
		move.target_type = "SLOT";
		move.target_slot = target_slot;

		if(slot_number == magazine_size-1)
		{
			// Increment the tape number in this terapack so the queue ticks
			// to the next terapack.
			mags[target_terapack].addTapeCount(slot_number);
		}

		return move;
	}

	private static Move queueMovesToOccupied(TeraPack[] mags, int magazine_size, int source_terapack, int target_terapack, BasicXMLCommands library, String partition, Logger log, boolean printToShell)
	{
		String source_barcode;
		String source_slot;
		String target_slot;
		String check_slot;
		String check_barcode;
		int check_slot_int = -1;
		int target_slot_int;

		Move move = new Move();

		source_barcode = mags[source_terapack].getBarcodeAtPosition(0);
		source_slot = VerifyMove.findSlotString(partition, source_barcode, library);

		do
		{
			check_slot_int++;
			check_slot_int = mags[target_terapack].getNextOccupiedSlot(check_slot_int);
			check_barcode = mags[target_terapack].getBarcodeAtPosition(check_slot_int);
		} while(check_barcode.equals("HOLD")); // repeat if we pull a slot that is on-hold as it can't be validated against the inventory
		
		check_slot = VerifyMove.findSlotString(partition, check_barcode, library);

		target_slot_int = mags[target_terapack].getNextEmptySlot(0);
		target_slot = VerifyMove.findDestinationSlot(partition, target_slot_int, check_slot_int, check_barcode, library);


		if(VerifyMove.validate(partition, source_slot, source_barcode, target_slot, check_slot, check_barcode, library, log, printToShell))
		{
			// Increment the capacity count instead of moving the tape between TeraPacks.
			// As tapes aren't being moved until after this section of the script activates, changes to the mags variable aren't
			// reflected in inventory. If tape 1 is moved and then used as a reference point for tape 2, moves would be unpredictable
			// and could potentially fail as they would target tape 1's old TeraPack. Not updating the barcode in the next magazine
			// should resolve this issue.
			mags[target_terapack].addTapeToSlot("HOLD", target_slot_int);
			
			move.barcode = source_barcode;
			move.source_type = "slot";
			move.source_slot = source_slot;
			move.target_type = "slot";
			move.target_slot = target_slot;
		}
		else
		{
			log.log("Error: Unable to validate move (" + source_slot + ": " + source_barcode + ") to (" + target_slot + ")", 3);
			
			if(printToShell)
			{
				System.out.println("Error: Unable to validate move (" + source_slot + ": " + source_barcode + ") to (" + target_slot + ")");
			}
		}

		return move;
	}

}


