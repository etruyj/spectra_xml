//============================================================================
// SpectraController.java
// 	Description:
// 		This class makes all the appropriate function calls to the tape
// 		library. 
//============================================================================

package com.socialvagrancy.spectraxml.commands;

import com.socialvagrancy.spectraxml.structures.TeraPack;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.spectraxml.utils.XMLParser;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvancedCommands
{
	private BasicXMLCommands library;
	private Logger log;

	//====================================================================
	// Constructor
	//====================================================================
	
	public AdvancedCommands(BasicXMLCommands base_command_set, Logger logbook)
	{
		library = base_command_set;
		log = logbook;

	}

	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================

	public void ejectEmpty(String partition, boolean printToShell)
	{
		// ejectEmpty
		//  Prepares an export list of empty terapacks in the 
		//  storage partition and uploads it into the library.
		//  The actual eject must be done from the library's
		//  front panel.

		int emptyTeraPacks = 0;
		int tempOffset = 0;
		StringBuilder offset = new StringBuilder();
		TeraPack[] magazines = magazineContents(partition, false);
		XMLResult[] response = new XMLResult[1];
		String url = "none";

		for(int i=0; i<magazines.length; i++)
		{
			if(magazines[i].getLocation().equalsIgnoreCase("storage") && magazines[i].getCapacity() == 0)
			{
				// Fun fact, the offset returned by XML is base 1.
				// The offset used by XML for identifying TeraPacks is
				// base 0.
				// All offsets must have -1 applied to reference the
				// correct TeraPack.
				tempOffset = Integer.parseInt(magazines[i].getOffset());
				tempOffset--; 
				
				offset.append(Integer.toString(tempOffset) + ",");
				emptyTeraPacks++;
			}


		}
			
		if(emptyTeraPacks>0)
		{
			// Shave off the last comma.
			offset.setLength(offset.length()-1);
		
			response = library.prepareImportExportList(partition, "storage", offset.toString());
		}

		if(printToShell)
		{
			if(emptyTeraPacks>0)
			{
				for(int i=0; i<response.length; i++)
				{
					System.out.println(response[i].value);
				}
				
				if(response[0].value.equalsIgnoreCase("OK"))
				{
					System.out.println("To export the specified TeraPacks, please log into the front panel of your Spectra tape library and access the Advanced Import/Export menu. Press the Populate button to load the moves and click Start Moves.");
				}
			}
			else
			{
				System.out.println("There are no empty TeraPacks in the storage chambers to export.");
			}
		}
	}

	public void magazineCapacity(String partition, boolean printToShell)
	{
		// This prints a summary of the magazine contents.
		float utilization = 0;
		TeraPack[] magazines = magazineContents(partition, false);

		int full = 0;
		int empty = 0;
		int almostEmpty = 0;
		int quarter = 0;
		int half = 0;
		int threeQuarter = 0;

		for(int i=0; i<magazines.length; i++)
		{
			utilization = (float)magazines[i].getCapacity() / magazines[i].getNumSlots();
			if(utilization == 1) { full++; }
			else if(utilization >= .75) { threeQuarter++; }
			else if(utilization >= .5) { half++; }
			else if(utilization >= .25) { quarter++; }
			else if(utilization > 0) { almostEmpty++; }
			else if(utilization == 0) { empty++; }
			
		}

		if(printToShell)
		{
			partition = partition.replace("%20", " ");
			System.out.println("There are " + magazines.length + " TeraPacks in partition " + partition);
			if(full>0) { System.out.println("Full: " + full); }
			if(threeQuarter>0) { System.out.println(">75%: " + threeQuarter); }
			if(half>0) { System.out.println(">50%: " + half); }
			if(quarter>0) { System.out.println(">25%: " + quarter); }
			if(almostEmpty>0) { System.out.println("<25%: " + almostEmpty); }
			if(empty>0) { System.out.println("Empty: " + empty); }
		}

	}

	public void magazineCompaction(String partition, int maxMoves, String output_type, boolean printToShell)
	{
		TeraPack[] magazine = sortMagazines(partition, true, true);

		// Determine if the move commands will be issued to the library via
		// XML or if a move list will be generated in ../output/MoveQueue.txt

		// Filename requirements are specific for the move queue.
		// It has to have this name to work.
		String fileName = "../output/MoveQueue.txt";
		
		if(output_type.equals("move-queue"))
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

		planCompaction(partition, magazine, maxMoves, output_type, fileName, true);

		if(output_type.equals("move-queue"))
		{
			System.out.println("\nGeneration of move queue is complete. The file can be found in the ../output directory. Upload the move queue to the library either by USB or the web GUI. When using USB, the file must be named MoveQueue.txt and placed in the root (/) directory to be uploaded. The move queue can be uploaded from the Inventory > Advanced menu.\n");
		}
	}

	public TeraPack[] magazineContents(String partition, boolean printToShell)
	{
		// This creates an array of terapacks for higher level work
		// and also performs a cleaner output than the physical inventory
		// option.

		// Get the library type, needed to know TeraPack size
		String libraryType = "none";
		XMLResult[] response = library.listPartitionDetails(partition, false);
		int magIterator = 0; // Track which magagzine we're on.

		// Search for type header.
		for(int i=0; i<response.length; i++)
		{
			if(response[i].headerTag.equalsIgnoreCase("type"))
			{
				libraryType = response[i].value;
			}
		}

		// Calculate the magazine capacity
		int magazineCount = library.countMagazines(partition);
		
		// Parse the magazine XML data to make an array of Terapacks.
		response = library.physicalInventory(partition, false);
		TeraPack[] magazines = new TeraPack[magazineCount];

		for(int j=0; j<response.length; j++)
		{
			if((response[j].headerTag.equalsIgnoreCase("storage>magazine>offset")) || (response[j].headerTag.equalsIgnoreCase("entryExit>magazine>offset")))
			{
				if(j>0)
				{
					magIterator++;
				}

				magazines[magIterator] = new TeraPack(libraryType);
			}
			magazines[magIterator].importXMLResult(response[j]);
			

		}

		for(int m=0; m<magazines.length; m++)
		{
			magazines[m].calculateCapacity();
		}
		

		if(printToShell)
		{
			System.out.println("Magazine count: " + magazineCount);

			for(int k=0; k<magazines.length; k++)
			{
				System.out.println("\nBarcode: " + magazines[k].getMagazineBarcode());
				System.out.println("utilization: " + magazines[k].getCapacity());
				for(int l=0; l<magazines[k].getNumSlots(); l++)
				{
					System.out.println(l + ": " + magazines[k].getTapeBarcode(l));
				}
			}
		}


		return magazines;
	}

	public void maintenanceHHMReset(boolean printToShell)
	{
		String libraryType = getLibraryType(printToShell);
		String robot = "none";
		int iterations = 1;

		if(libraryType.equalsIgnoreCase("TFinity"))
		{
			iterations = 2;
		}

		for(int i=0; i<iterations; i++)
		{
			if(libraryType.equalsIgnoreCase("TFinity"))
			{
				robot = "Robot " + Integer.toString(i+1);
			}

			library.resetHHMCounter("Vertical Axis", "Trip 1", robot, printToShell);
			library.resetHHMCounter("Vertical Axis", "Trip 2", robot, printToShell);
		}
		
	}

	public void moveListAppendLine(String source_type, String source, String dest_type, String destination, String fileName)
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

	public boolean moveListCreateFile(String fileName)
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

	private TeraPack[] filterEmptyFullEntryExit(TeraPack[] mags)
	{
		// Filter out empty, full, and entry exit terapacks.
		// The end result should only be partially full terapacks
		// in the Storage Partition.
		List<TeraPack> availableInventory = new ArrayList<>();
			
		for(int i=0; i<mags.length; i++)
		{
			if(mags[i].getCapacity()>0 && mags[i].getCapacity()<mags[i].getNumSlots() && mags[i].getLocation().equalsIgnoreCase("storage"))
			{
				availableInventory.add(mags[i]);
			}
		}
		
		// Convert list back into a TeraPack[] array to allow
		// for consistent information.
		TeraPack[] tempTeraPack = new TeraPack[availableInventory.size()];

		for(int i=0; i<availableInventory.size(); i++)
		{
			tempTeraPack[i] = availableInventory.get(i);
		}

		return tempTeraPack;

	}
	
	private String findDestinationSlot(String partition, int destSlot, int magSlot, String barcode)
	{
		// Finds the slot string of the destination slot
		// by locating the slot of a tape within the same
		// TeraPack and adjusting the slot based on the difference.

		// destSlot = target
		// magSlot = slot occupied by passed barcode.
		int difference = destSlot - magSlot;
		String anchor = "none";
		anchor = findSlotString(partition, barcode);

		if(!anchor.equals("none"))
		{
			difference = difference + Integer.valueOf(anchor);
			return Integer.toString(difference);
		}

		return "none";		
	}

	private ArrayList<String> findEmptyTeraPacks(String partition, int magazine_size, boolean printToShell)
	{
		ArrayList<String> empty_slots = findEmptySlots(partition);
		ArrayList<String> terapack_slots = new ArrayList<String>();
		int firstSlot = Integer.valueOf(empty_slots.get(0));
		
		int terapack = firstSlot % magazine_size;
		terapack = (firstSlot - terapack)+1;

		boolean searching = true;
		int itr = 0;

		while(searching)
		{
			// If the slot is the first in the terapack
			// Check the index + mag size to determine
			// if all the slots are in the list.
			if(Integer.valueOf(empty_slots.get(itr)) == terapack)
			{
				if(Integer.valueOf(empty_slots.get(itr+magazine_size-1)) == (terapack + magazine_size - 1))
				{
					// Add slots to array.
					for(int i = itr; i<itr+magazine_size; i++)
					{
						terapack_slots.add(empty_slots.get(i));
					}
				}
			}

			// If in the last slot of the TP increment the tp.
			if(Integer.valueOf(empty_slots.get(itr)) == (terapack + magazine_size - 1))
			{
				terapack+=magazine_size;
			}

			// Check to see if we got to the end of the array.
			// if the last slot in the terapack is higher than the
			// last value of the list, we're done.
			if((terapack + magazine_size - 1) > Integer.valueOf(empty_slots.get(empty_slots.size()-1)))
			{
				searching = false;
			}

			itr++;
		}
	
		return terapack_slots;
	}

	private ArrayList<String> findEmptySlots(String partition)
	{
		ArrayList<String> empty_slots = new ArrayList<String>();
		boolean searching = true;
		int itr = 0;
		String checkSlot = "0";

		XMLResult[] response = library.listInventory(partition, false);
		
		// Build a list of all available slots.
		while(searching)
		{
			// Grab a slot number.
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>Offset"))
			{
				checkSlot = response[itr].value;
			}
			
			// Determine if that slot is occupied.
			// If not store the number for verificatation.
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>full") && response[itr].value.equalsIgnoreCase("No"))
			{
				empty_slots.add(checkSlot);
			}

			// Determine the end of the search.
			if(response[itr].headerTag.equalsIgnoreCase("partition>entryExitSlot"))
			{
				searching = false;
			}

			itr++;
		}

		return empty_slots;
	}	

	private String findSlotString(String partition, String barcode)
	{
		// Search the inventory for the barcode.
		// Export the Slot number of the barcode.

		boolean slotFound = false;
		String slot = "none";
		int itr = 0; // using while with an iterator to save cycles.

		XMLResult[] response = library.listInventory(partition, false);

		while(!slotFound)
		{
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>Offset"))
			{
				slot = response[itr].value;
			}
			
			// have to use trim() as there's whitespace in the barcode
			// for some reason.
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>barcode") && response[itr].value.trim().equalsIgnoreCase(barcode))
			{
				slotFound = true;
			}

			itr++;
		}

		return slot;
	}
	
	private String generateSlotString(String TeraPackOffset, int TapeSlot)
	{
		int librarySlot = (10 * Integer.valueOf(TeraPackOffset)) - (10 - TapeSlot) + 1;
		return Integer.toString(librarySlot);
	}

	public String getLibraryType(boolean printToShell)
	{
		String libraryType = "none";
		boolean typeFound = false;
		int itr = 0;

		XMLResult[] libraryProfile = library.libraryStatus(false);
		
		while(!typeFound)
		{
			if(libraryProfile[itr].headerTag.equalsIgnoreCase("libraryType"))
			{
				libraryType = libraryProfile[itr].value;
				typeFound = true;
			}
			itr++;
		}

		if(printToShell)
		{
			System.out.println(libraryType);
		}

		return libraryType;

	}

	public String getMediaType(String partition, boolean printToShell)
	{
		String mediaType="none";
		boolean typeFound = false;
		int itr = 0;

		XMLResult[] partitionProfile = library.listPartitionDetails(partition, false);

		while(!typeFound)
		{
			if(partitionProfile[itr].headerTag.equalsIgnoreCase("Type"))
			{
				mediaType = partitionProfile[itr].value;
				typeFound = true;
			}
			itr++;
		}	

		if(printToShell)
		{
			System.out.println("Partition " + partition + " uses " + mediaType + " media.");
		}

		return mediaType;
	}
	
	private void planCompaction(String partition, TeraPack[] mags, int maxMoves, String output_type, String fileName, boolean printToShell)
	{
		int source = 0; // Incrementor for source TP
		int destination = mags.length - 1; // Increment for destination TP
		int sourceTapes = mags[source].getCapacity(); // How many tapes are in the Source Magazine.
		int destSlots = mags[destination].getNumSlots() - mags[destination].getCapacity(); // How many slots are available in the destination TeraPack.

		if(source>=destination)
		{

			log.log("There are no moves to free up any TeraPacks.", 1);
			if(printToShell)
			{
				System.out.println("There are no moves to free up any TeraPacks.");
			}
		}
		else if(sourceTapes > maxMoves)
		{
			log.log("There are " + sourceTapes + " tapes in this TeraPack and only " + maxMoves + " moves allowed with this operation. No TeraPacks will be freed.", 1);
			if(printToShell)
			{
				System.out.println("There are " + sourceTapes + " tapes in this TeraPack and only " + maxMoves + " moves allowed with this operation. No TeraPacks will be freed.");
			}
		}

		int tapeSlot = -1;
		int emptySlot = -1;
		int moves = 0;
		String sourceBarcode;
		String sourceSlotString = "none";
		String destSlotString = "none";

		// Move validation variables.
		int checkSlot; // slot to check for reference tape.
		String checkBarcode;
		String checkSlotString;
		boolean isValidMove;
	
		while((source < destination) && (moves < maxMoves))
		{
			// Variables are set here to reset them
			// with every iteration.
			isValidMove = false;
			checkSlot = -1;

			tapeSlot = mags[source].getNextOccupiedSlot(tapeSlot);
			emptySlot = mags[destination].getNextEmptySlot(emptySlot);
		
			sourceBarcode = mags[source].getBarcodeAtPosition(tapeSlot);

			// VALIDATION
			// The formula for the actual slot is a best-guess
			// we'll check to see if the barcode is in the source
			// slot, the destination slot is empty, and a barcode
			// in the same destination tp is in the slot we expect.

			checkSlot = mags[destination].getNextOccupiedSlot(checkSlot);
			if(checkSlot>=0)
			{
				checkBarcode = mags[destination].getBarcodeAtPosition(checkSlot);

				// Identify the target slots.
				// sourceSlot - slot of source tape.
				// destSlot - empty destination slot.
				// checkSlot - occupied slot in the same TeraPack to
				// 		anchor the TeraPack to the inventory
				// 		slot. destSlot is calculated from here.
				if(printToShell)
				{
					System.out.println("\nPreparing move " + moves);
				}

				sourceSlotString = findSlotString(partition, sourceBarcode);
				checkSlotString = findSlotString(partition, checkBarcode);
				destSlotString = findDestinationSlot(partition, emptySlot, checkSlot, checkBarcode);

				// Move validation.
				// Will be removed/commented out in a future release.
				// This was placed here when the slot was calculated
				// by generateSlotString() to verify the calculated
				// value. findSlotString() performs a similar task as
				// validateMove(), so this function validates the move
				// with by the same process that generates it. It's
				// redundant.
				isValidMove = validateMove(partition, sourceSlotString, sourceBarcode, destSlotString, checkSlotString, checkBarcode, true);				
			}
			else
			{
				// There's an error here where a checkslot is not being returned
				// and the move queue ends up duplicating the last issued move.
				log.log("Unable to validate move " + moves + ": cannot locate reference tape to verify destination for " + sourceBarcode + ".", 3);
				log.log("Check slot " + checkSlot + " returned for destination magazine " + destination, 3);
			     	if(printToShell)
				{
					System.out.println("Unable to validate move " + moves + " for tape " + sourceBarcode + ". Please re-run the command to re-try.");
				}	
			}

			// Actually Perform the move
			if(isValidMove)
			{
				if(printToShell)
				{
					System.out.println("Move " + moves + ": " + sourceBarcode  + " at slot " + sourceSlotString + " moving to " + destSlotString);
				}
			
				if(output_type.equals("move-queue"))
				{
					moveListAppendLine("Slot", sourceSlotString, "Slot", destSlotString, fileName);
				}
				else
				{
					sendMove(partition, sourceSlotString, destSlotString);
				}
			}
			else
			{
				log.log("Move " + moves + " failed for barcode " + sourceBarcode, 3);
				if(printToShell)
				{
					System.out.println("Cannot verify slot information for move " + moves + ". Cancelling action.");
				}
			}

		
			// Remove tape from mag count.
			// And move to the next mag if this on is empty.
			sourceTapes--;
			
			if(sourceTapes<1)
			{
				source++;
				sourceTapes = mags[source].getCapacity();
				tapeSlot = -1;
			}

			// Remove one available slot from destination
			// And move to the next mag if this one is empty.
			destSlots--;

			if(destSlots<1)
			{
				destination--;
				destSlots = mags[destination].getNumSlots() - mags[destination].getCapacity();
				emptySlot = -1;
			}

			moves++;
		}

	}
	
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

	//==============================================
	// QUICK SORT
	//==============================================
	//
	private TeraPack[] quickSort(TeraPack[] mags, int low, int high)
	{
		if(low < high)
		{
			int pi = partition(mags, low, high);

			// Separately sort elements before
			// partition and after partition
			quickSort(mags, low, pi - 1);
			quickSort(mags, pi + 1, high);
		}

		return mags;
	}
	//
	// Part of the quick sort algorithm, not anything to do with library partitions.
	private int partition(TeraPack[] mags, int low, int high)
	{
		// Pivot
		int pivot = mags[high].getCapacity();

		// Index of smaller element and 
		// indicates the right position of the
		// pivot found so far
		int i = (low - 1);

		for(int j = low; j < high; j++)
		{
			// If the current element is smaller
			// than the pivot
			if(mags[j].getCapacity() < pivot)
			{
				i++;
				swapTeraPacks(mags, i, j);
			}
		}

		swapTeraPacks(mags, i+1, high);
		return (i + 1);
	}
	//
	//==============================================
	// END QUICK SORT
	//==============================================
	

	private boolean sendMove(String partition, String sourceSlot, String destSlot)
	{
		// Send the move to the library.
		// Wait until the move is complete before exiting function.

		library.moveTape(partition, "SLOT", sourceSlot, "SLOT", destSlot, true);

		return true;	
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

	private TeraPack[] sortMagazines(String partition, boolean omitEmptyFull, boolean printToShell)
	{
		// Gathering TeraPack from library.
		if(printToShell) 
		{ 
			System.out.println("Gathering TeraPack information from library..."); 
		}

		TeraPack[] magazines = magazineContents(partition, false);

		// Analyze TeraPacks
		if(printToShell)
		{
			System.out.println("Analyzing TeraPack Contents...");
		}

		// Remove the Empty and full TeraPacks if desired.
		if(omitEmptyFull)
		{
			magazines = filterEmptyFullEntryExit(magazines);
		}

		/* Debug Code
		// Print before and after.
		for(int i=0; i<magazines.length; i++)
		{
			System.out.print(magazines[i].getCapacity() + " ");
		}

		System.out.print("\n");
		*/

		quickSort(magazines, 0, magazines.length-1);

		/* Debug Code
		for(int i=0; i<magazines.length; i++)
		{
			System.out.print(magazines[i].getCapacity() + " ");
		}

		System.out.print("\n");
		*/

		return magazines;
	}

	private void swapTeraPacks(TeraPack[] terapacks, int i, int j)
	{
		// Swaps the TeraPack at position i with the 
		// TeraPack at position j in the terapacks array.
		TeraPack temp;
		temp = terapacks[i];
		terapacks[i] = terapacks[j];
		terapacks[j] = temp;
	}


	private boolean validateMove(String partition, String sourceSlot, String sourceBarcode, String destSlot, String destSlot2, String destBarcode, boolean printToShell)
	{
		// The purpose of this function is to validate the source and destination slot
		// against the library inventory before initiating the move.
		// I'm guessing the formula for slot number is (10 * TeraPack Offset) - (10 - TeraPack[i].tape's array index) + 1.
		// I'm also assuming the TeraPack offsets will update along with the slot numbers if a 
		// TeraPack is exported or imported into the library.
		// As there is a lot of guessing.... there's a validateMove.
		

		// Sort the three slots into ascending order
		// Needed to compare against the order the results come in.
		List<String> slotOrder = new ArrayList<>();
		
		if(Integer.valueOf(sourceSlot)<Integer.valueOf(destSlot))
		{
			slotOrder.add(sourceSlot);
			slotOrder.add(destSlot);
		}
		else
		{
			slotOrder.add(destSlot);
			slotOrder.add(sourceSlot);
		}

		if(Integer.valueOf(destSlot2) > Integer.valueOf(slotOrder.get(1)))
		{
			slotOrder.add(destSlot2);
		}
		else if(Integer.valueOf(destSlot2) < Integer.valueOf(slotOrder.get(0)))
		{
			slotOrder.add(0, destSlot2);
		}
		else
		{
			slotOrder.add(1, destSlot2);
		}
		
		// Log progress so far.
		log.log("Validating Move: " + sourceBarcode + " from source " + sourceSlot + " to " + destSlot, 1);
		log.log("Verifying destination with tape " + destBarcode + " at slot " + destSlot2, 1);

		// Search for the slots in the library's inventory.
		boolean inSlot = false; // Parsing the correct slot
		boolean success = true; // Success default true. Any failed test results in false.
		int listIndex = 0; // The index for the linked list.
		String searchValue = "none";
	
		// slotCheck is used to make sure all three slots have been verified during this process.
		// If source, dest, and dest2 are checked the corresponding index is marked true.
		// isValid is determined based on success and the indices on slotCheck.
		// results are logged.
		boolean[] slotCheck = {false, false, false};

		XMLResult[] response = library.listInventory(partition, false);

		
		for(int i=0; i<response.length; i++)
		{
			// Find the right storage slot
			if(listIndex<3)
			{
				if(response[i].headerTag.equalsIgnoreCase("partition>storageSlot>Offset") && response[i].value.equals(slotOrder.get(listIndex)))
				{
					inSlot = true;
					
					if(slotOrder.get(listIndex).equals(sourceSlot))
					{
						if(printToShell)
						{
							System.out.print("Verifying source...\t\t\t");
						}
						searchValue = sourceBarcode;
						slotCheck[0] = true; // source has been checked.
					}
					else if(slotOrder.get(listIndex).equals(destSlot))
					{
						if(printToShell)
						{
							System.out.print("Verifying destination...\t\t");
						}
						searchValue = "No";
						slotCheck[1] = true; // destination has been checked.
					}
					else if(slotOrder.get(listIndex).equals(destSlot2))
					{	
						if(printToShell)
						{
							System.out.print("Verifying destination TeraPack...\t");
						}
						searchValue = destBarcode;
						slotCheck[2] = true; // reference barcode has been checked.
					}
				}

				if(inSlot)
				{

					// Check the barcode in the slot against the one expected.
					// If it matches return true. Otherwise return false.
					if(response[i].headerTag.equalsIgnoreCase("partition>storageSlot>barcode"))
					{
						if(!response[i].value.trim().equalsIgnoreCase(searchValue.trim()))
						{
							if(printToShell)
							{
								System.out.println("[FAILED]");
							}
							success = false;
						}
						else if(printToShell)
						{
							System.out.println("[SUCCESS]");
						}
						listIndex++;
						inSlot = false;	
					}

					// Check the empty slot.
					// There is no barcode field in this slot
					// test for <full>no
					if(searchValue.equals("No") && response[i].headerTag.equalsIgnoreCase("partition>storageSlot>full"))
					{
						// False value
						if(!response[i].value.equalsIgnoreCase("no"))
						{
							if(printToShell)
							{
								System.out.println("[FAILED]");
							}
							success = false;
						}
						else if(printToShell)
						{
							System.out.println("[SUCCESS]");
						}
						listIndex++;
						inSlot = false;
					}
				}
			}
		}			

		if(success && slotCheck[0] && slotCheck[1] && slotCheck[2])
		{
			// All three slots were checked (slotCheck)
			// None of values failed.
			log.log("Move validated", 1);
			return true;
		}
		else
		{
			// The validation failed. Log why.
			if(!slotCheck[0])
			{
				log.log("Invalid Move (" + sourceBarcode + ": " + sourceSlot + " > " + destSlot + "): Unable to check source slot.", 3);
			}

			if(!slotCheck[1])
			{
				log.log("Invalid Move (" + sourceBarcode + ": " + sourceSlot + " > " + destSlot + "): Unable to check destination slot.", 3);
			}

			if(!slotCheck[2])
			{
				log.log("Invalid Move (" + sourceBarcode + ": " + sourceSlot + " > " + destSlot + "): Unable to check reference barcode " + destBarcode + "(" + destSlot2 + ").", 3);
			}

			if(!success)
			{
				log.log("Invalid Move (" + sourceBarcode + ": " + sourceSlot + " > " + destSlot + "): One of the slots checked did not have the expected value.", 3);
			}
			return false;
		}
	}
}
