//============================================================================
// SpectraController.java
// 	Description:
// 		This class makes all the appropriate function calls to the tape
// 		library. 
//============================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlotIQCommands
{
	private BasicXMLCommands library;
	private Logger log;

	//====================================================================
	// Constructor
	//====================================================================
	
	public AdvancedCommands(BasicXMLCommands base_command_set)
	{
		library = base_command_set;

		// Declared logger in SpectraController as opposed to 
		// in connector to allow logging of issues within the commands.
		log = new Logger("../logs/slxml-main.log", 102400, 3, 1);

	}

	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================

	public void prepareSlotIQ(String partition, int max_moves, String output_format, boolean printToShell)
	{
		// This command prepares a library for configuration with
		// SlotIQ. Slot IQ requires at least 1 empty slot to be
		// available in each TeraPack. This library will move
		// tapes out of the 8th slot of all full TeraPacks.
		//
		// This command should be run before SlotIQ is enabled
		// on the library.

		log.log("Preparing library for SlotIQ", 1);

		if(printToShell)
		{
			System.out.println("Preparing library for SlotIQ...");
		}

		String mediaType = getMediaType(partition, true);

		TeraPack[] magazines = sortMagazines(partition, false, true); 

		if(slotIQIsPossible(magazines, mediaType, true))
		{
			// Filename requirements are specific for the move queue.
			// It has to have this name to work.
			String fileName = "../output/MoveQueue.txt";
		
			if(output_format.equals("move-queue"))
			{
		
				if(moveListCreateFile(fileName))
				{
					log.log("Created move queue file: " + fileName, 1);
				}
				else
				{
					log.log("Unable to create move queue file: " + fileName, 3);
				}
			}

			System.out.println("Slot IQ preparation is possible.");
			
			slotIQEmptyFullTerapacks(magazines, partition, mediaType, max_moves, output_format, fileName, true);
			
			if(output_format.equals("move-queue"))
			{
				System.out.println("\nGeneration of move queue is complete. The file can be found in the ../output directory. Upload the move queue to the library either by USB or the web GUI. When using USB, the file must be named MoveQueue.txt and placed in the root (/) directory to be uploaded. The move queue can be uploaded from the Inventory > Advanced menu.\n");
			}
		
		}
		else
		{
			log.log("Unable to prepare library for Slot IQ.", 3); 
		
			if(printToShell)
			{
				System.out.println("There are not enough available slots to perform SlotIQ preparation."); 
			}
		}	
	}

	private void slotIQEmptyFullTerapacks(TeraPack[] mags, String partition, String mediaType, int max_moves, String output_format, String fileName, boolean printToShell)
	{
		int max_slots_per_terapack;
		int source_terapack = mags.length-1;
		int target_terapack = mags.length-1;
		int moves_to_empty_terapack = 0; // tracking movement into empty terapacks.
		int move_counter = 0;
		boolean moving_tapes = true;

		if(mediaType.equals("LTO"))
		{
			max_slots_per_terapack=10;
		}
		else
		{
			max_slots_per_terapack=9;
		}
	
		ArrayList<String> empty_slots = findEmptyTeraPacks(partition, max_slots_per_terapack, printToShell);
		
		while(moving_tapes)
		{
			// Find next destination terapack.
			target_terapack = slotIQFindNextAvailableTerapack(mags, max_slots_per_terapack, target_terapack);

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
						System.out.println("Maximum requested moves (" + max_moves + ") has been reached.");
					}
				}
				else
				{
					log.log("SlotIQ preparation complete. All TeraPacks have at least 1 open slot.", 3);

					if(printToShell)
					{
						System.out.println("SlotIQ preparation complete. All TeraPacks have at least 1 open slot.");
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
					slotIQQueueMovesToOccupied(mags, max_slots_per_terapack, source_terapack, target_terapack, partition, output_format, fileName, printToShell);
				}
				else
				{
					// Terapack is empty, so use empty terapack slots.
					// (empty_slots)
					moves_to_empty_terapack++;
					slotIQQueueMovesToEmpty(mags, max_slots_per_terapack, source_terapack, target_terapack, empty_slots.get(0), moves_to_empty_terapack, partition, output_format, fileName);
					empty_slots.remove(0);

					if(moves_to_empty_terapack==(max_slots_per_terapack-1))
					{
						// If moves_to_empty_terapack = max_slots - 1, there is only 1 slot left in the TeraPack.
						// Terapack capacity was incremented to ensure we move to the next target terapack.
						// Reset this counter to 0.
						moves_to_empty_terapack=0;
					}
				}
				
				move_counter++;
				source_terapack--;
			}
		}	
	}

	private int slotIQFindNextAvailableTerapack(TeraPack[] mags, int mag_size, int index)
	{
		if(mags[index].getCapacity()<mag_size-1)
		{
			return index;
		}
		else if(index-1>=0)
		{
			return slotIQFindNextAvailableTerapack(mags, mag_size, index-1);
		}
		else
		{
			return -1;
		}
	}

	private boolean slotIQIsPossible(TeraPack[] mags, String mediaType, boolean printToShell)
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

	private void slotIQQueueMovesToEmpty(TeraPack[] mags, int magazine_size, int source_terapack, int target_terapack, String target_slot, int slot_number, String partition, String output_format, String fileName)
	{
		String source_barcode = mags[source_terapack].getBarcodeAtPosition(0);
		String source_slot = findSlotString(partition, source_barcode);

		if(output_format.equals("move-queue"))
		{
			log.log("Writing move to move queue. (" + source_slot + ": " + source_barcode + ") to " + target_slot + ".", 2);
			moveListAppendLine("Slot", source_slot, "Slot", target_slot, fileName);
		}
		else
		{
			log.log("Sending move to library. (" + source_slot + ": " + source_barcode + ") to " + target_slot + ".", 2);
			sendMove(partition, source_slot, target_slot);
		}

		if(slot_number == magazine_size-1)
		{
			// Increment the tape number in this terapack so the queue ticks
			// to the next terapack.
			mags[target_terapack].addTapeCount(slot_number);
		}
	}

	private void slotIQQueueMovesToOccupied(TeraPack[] mags, int magazine_size, int source_terapack, int target_terapack, String partition, String output_format, String fileName, boolean printToShell)
	{
		String source_barcode;
		String source_slot;
		String target_slot;
		String check_slot;
		String check_barcode;
		int check_slot_int;
		int target_slot_int;

		source_barcode = mags[source_terapack].getBarcodeAtPosition(0);
		source_slot = findSlotString(partition, source_barcode);

		check_slot_int = mags[target_terapack].getNextOccupiedSlot(0);
		check_barcode = mags[target_terapack].getBarcodeAtPosition(check_slot_int);
		check_slot = findSlotString(partition, check_barcode);

		target_slot_int = mags[target_terapack].getNextEmptySlot(0);
		target_slot = findDestinationSlot(partition, target_slot_int, check_slot_int, check_barcode);


		if(validateMove(partition, source_slot, source_barcode, target_slot, check_slot, check_barcode, printToShell))
		{
			mags[target_terapack].addTapeToSlot(source_barcode, target_slot_int);

			if(output_format.equals("move-queue"))
			{
				log.log("Writing move to move queue. " + source_slot + " (" + source_barcode + ") to " + target_slot + ".", 2);
				moveListAppendLine("Slot", source_slot, "Slot", target_slot, fileName);
			}
			else
			{
				log.log("Sending move to library. " + source_slot + " (" + source_barcode + ") to " + target_slot + ".", 2);
				sendMove(partition, source_slot, target_slot);
			}
		}
		else
		{
			log.log("Error: Unable to validate move (" + source_slot + ": " + source_barcode + ") to (" + target_slot + ")", 3);
			
			if(printToShell)
			{
				System.out.println("Error: Unable to validate move " + source_slot + " (" + source_barcode + ") to (" + target_slot + ")");
			}
		}
	}

}


