//============================================================================
// AdvancedCommands.java
// 	Description:
//		This class orchestrates the calls necessary to perform the
//		advanced commmands. Advanced Commands are commands that use
//		the output of the basic commands in order to perform complex
//		tasks with the library.
//
//		More complex operations are defined in their own class in the
//		sub directory.
//
//	Functions:
//		ejectEmpty(partition) : prepares an importExport list of empty
//			terapacks and uploads them to the library for export.
//
//============================================================================

package com.socialvagrancy.spectraxml.commands;

import com.socialvagrancy.spectraxml.commands.sub.Inventory;
import com.socialvagrancy.spectraxml.commands.sub.MagazineCompaction;
import com.socialvagrancy.spectraxml.commands.sub.SlotIQ;
import com.socialvagrancy.spectraxml.commands.sub.SortMagazines;
import com.socialvagrancy.spectraxml.structures.Move;
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
		// Get list of Terapacks.
		if(printToShell)
		{
			System.out.println("Gathering TeraPack information...");
		}

		log.log("Calling magazineContents(" + partition + ", false)", 2);
		TeraPack[] magazines = magazineContents(partition, false);
		
		// Sort list.

		log.log("Calling SortMagazines.sort()", 2);
		magazines = SortMagazines.sort(magazines, true, true);

		int[] requirements = MagazineCompaction.checkMoves(magazines, maxMoves);

		if(requirements[0] == 0)
		{
			log.log("No TeraPacks can be freed by magazine compaction.", 2);

			if(printToShell)
			{
				System.out.println("No TeraPacks can be freed by magazine compaction.");
			}
		}
		else if(requirements[0] < 0)
		{
			log.log("Not enough moves allowed. Maximum of " + maxMoves 
					+ " specified when " + -requirements[0] 
					+ "moves are required to free 1 magazine.", 2);

			if(printToShell)
			{
				System.out.println("Not enough moves allowed. Maximum of " + maxMoves 
					+ " specified when " + -requirements[0] 
					+ "moves are required to free 1 magazine.");
			}

			// END OF FUNCTION AS NO MOVES ARE POSSIBLE
		}
		else
		{
			log.log("Starting compaction.", 2);
			log.log("Maximum moves: " + maxMoves, 2);
			log.log("Available moves: " + requirements[0], 2);
			log.log("Available target slots: " + requirements[1], 2);
			log.log("TeraPacks that can be freed: " + requirements[2], 2);

			if(printToShell)
			{
				System.out.println("\nMaximum moves: " + maxMoves);
				System.out.println("Available moves: " + requirements[0]);
				System.out.println("Available target slots: " + requirements[1]);
				System.out.println("TeraPacks that can be freed: " + requirements[2]);
			}

			ArrayList<Move> move_list = MagazineCompaction.prepareMoves(magazines, maxMoves, partition, library, log, printToShell);
		}
	}

	public TeraPack[] magazineContents(String partition, boolean printToShell)
	{
		// This creates an array of terapacks for higher level work
		// and also performs a cleaner output than the physical inventory
		// option.

		// Get the library type, needed to know TeraPack size
		String libraryType = "none";
		XMLResult[] response = library.listPartitionDetails(partition);
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
		response = library.physicalInventory(partition);
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

			library.resetHHMCounter("Vertical Axis", "Trip 1", robot);
			library.resetHHMCounter("Vertical Axis", "Trip 2", robot);
		}
		
	}

	public void prepareSlotIQ(String partition, int max_moves, String output_format, boolean printToShell)
	{
		ArrayList<Move> move_list;
		log.log("Preparing library for SlotIQ", 1);

		if(printToShell)
		{
			System.out.println("Preparing library for SlotIQ...");
		}
		
		String mediaType = getMediaType(partition, true);
		TeraPack[] magazines = magazineContents(partition, false); 
		magazines = SortMagazines.sort(magazines, false, true);
		
		if(SlotIQ.isPossible(magazines, mediaType, true))
		{
			log.log("SlotIQ preparation is possible", 1);
			
			int slots_per_mag = Inventory.findMagazineSize(mediaType);

			move_list = SlotIQ.prepareMoves(library, partition, magazines, slots_per_mag, max_moves, log, printToShell);
			
		}
		else
		{
			log.log("Unable to prepare library for SlotIQ", 3);

			if(printToShell)
			{
				System.out.println("There are not enough available slots to perform SlotIQ preparation.");
			}
		}

	}

	public String getLibraryType(boolean printToShell)
	{
		String libraryType = "none";
		boolean typeFound = false;
		int itr = 0;

		XMLResult[] libraryProfile = library.libraryStatus();
		
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

		XMLResult[] partitionProfile = library.listPartitionDetails(partition);

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
	
	private boolean sendMove(String partition, String sourceSlot, String destSlot)
	{
		// Send the move to the library.
		// Wait until the move is complete before exiting function.

		library.moveTape(partition, "SLOT", sourceSlot, "SLOT", destSlot);

		return true;	
	}

}
