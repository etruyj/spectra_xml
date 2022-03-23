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

import com.socialvagrancy.spectraxml.commands.sub.ArrangeTapes;
import com.socialvagrancy.spectraxml.commands.sub.CalibrateDrives;
import com.socialvagrancy.spectraxml.commands.sub.EjectListedTapes;
import com.socialvagrancy.spectraxml.commands.sub.Inventory;
import com.socialvagrancy.spectraxml.commands.sub.LibraryProfile;
import com.socialvagrancy.spectraxml.commands.sub.LoadFile;
import com.socialvagrancy.spectraxml.commands.sub.MagazineCompaction;
import com.socialvagrancy.spectraxml.commands.sub.MagazineUtilization;
import com.socialvagrancy.spectraxml.commands.sub.MoveQueue;
import com.socialvagrancy.spectraxml.commands.sub.SlotIQ;
import com.socialvagrancy.spectraxml.commands.sub.SortMagazines;
import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.spectraxml.structures.SlotPair;
import com.socialvagrancy.spectraxml.structures.TeraPack;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.spectraxml.utils.XMLParser;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

	public void arrangeTapes(String partition, String file_path, int max_moves, String output_format, boolean printToShell)
	{
		// Arrange the tapes in ascending order in the library.

//		log.log("Organizing library inventory...", 1);

		if(printToShell)
		{
			System.err.println("Organizing library inventory.");
		}

		XMLResult[] inv = library.listInventory(partition);

		//===============================
		// Algorithm 2
		//===============================

		ArrayList<Move> move_list;

		if(file_path.equals("none"))
		{
			move_list = ArrangeTapes.alphabetizeInventory(inv, max_moves, log);
		}
		else
		{
			String mediaType = getMediaType(partition, false);
			int slots_per_terapack = Inventory.findMagazineSize(mediaType);

			move_list = ArrangeTapes.groupListed(inv, file_path, slots_per_terapack, max_moves, log);
		}

		if(move_list.size()>0)
		{
			log.INFO("(" + move_list.size() + ") moves queued.");
				
			if(output_format.equals("move-queue"))
			{
				log.log("Generating move queue...", 1);
				MoveQueue.storeMoves("../output/MoveQueue.txt", move_list);
			}
			else
			{
				log.log("Sending moves to library...", 1);
				sendMoves(partition, move_list, printToShell);
			}
		}
		else
		{
			log.WARN("No moves queued.");
		}
	

/*		log.log("Finding empty slots...", 1);
		ArrayList<String> empty_slots = Inventory.findEmptySlots(inv);

		log.log("Found (" + empty_slots.size() + ") empty slots.", 1);

		if(printToShell)
		{
			System.err.println("Found (" + empty_slots.size() + ") empty slots in partition " + partition);
		}

		if(empty_slots.size()==0)
		{
			log.log("Unable to proceed. At least 1 slot is required.", 2);
			if(printToShell)
			{
				System.err.println("Unable to move tapes. At least one empty slot is required.");
			}
		}
		else
		{
			log.log("Retrieving ordered list of tapes...", 1);
			ArrayList<SlotPair> ordered_tapes = ArrangeTapes.getOrderedBarcodeList(inv);
			
			log.log("There are (" + ordered_tapes.size() + ") tapes in this partition.", 1);	
			log.log("Queuing moves...", 1);
			ArrayList<Move> move_list = ArrangeTapes.queueMoves(ordered_tapes, empty_slots, max_moves, log);
		
			if(move_list.size() == 0)
			{
				log.log("No moves queued.", 2);

				if(printToShell)
				{
					System.err.println("No moves queued.");
				}
			}
			else
			{
				if(move_list.size() == max_moves)
				{
					log.log("Queued the maximum number of moves specified (" + max_moves + ")", 2);
				}
				else
				{
					log.log("Queued " + move_list.size() + " moves of the " + max_moves + " moves allowed.", 2);
				}

				if(output_format.equals("move-queue"))
				{
					log.log("Generatiing move queue...", 1);
					MoveQueue.storeMoves("../output/MoveQueue.txt", move_list);
				}
				else
				{
					log.log("Sending moves to library...", 1);
					sendMoves(partition, move_list, printToShell);
				}
			}
		}
*/		
	}

	public void calibrateDrives(String partition, String output_format, boolean printToShell)
	{
		// Calibrate the partitions drives by moving a tape to each
		// partition.
		log.log("Calibrating drives for partition " + partition, 1);
		ArrayList<Move> move_list = CalibrateDrives.prepareMoves(library, partition, log);
		

		if(move_list.size()>0)
		{
			if(output_format.equals("move-queue"))
			{
				MoveQueue.storeMoves("../output/MoveQueue.txt", move_list);		
			}
			else
			{
				sendMoves(partition, move_list, printToShell);
			}
		}
		else
		{
			log.log("No moves queued.", 2);
		}
	}

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

	public void ejectListedTapes(String partition, String file_name, boolean printToShell)
	{
	
		String mediaType = getMediaType(partition, false);
		int slots_per_terapack = Inventory.findMagazineSize(mediaType);
		
		TeraPack mags[] = magazineContents(partition, false);
		mags = SortMagazines.sort(mags, false, true);
		String offset_list = EjectListedTapes.prepareImportExportList(partition, mags, slots_per_terapack, file_name, log); 
	
		library.prepareImportExportList(partition, "storage", offset_list);

		log.log("Export list created for TeraPack offsets " + offset_list, 2);

		if(printToShell)
		{
			System.out.println("\nImport export list uploaded to library. To export the specified TeraPacks, navigated to the Advanced menu from the General > Import/Export menu. Choose partition " + partition + " and then click on the populate button.\n");
		}
	}

	public void ejectTeraPack(String partition, String terapack, String tape, boolean printToShell)
	{

		String offset;


		TeraPack[] mags = magazineContents(partition, false);

		if(!terapack.equals("none"))
		{
			log.log("Ejecting Terapack [" + terapack + "]", 1);
			offset = EjectListedTapes.getTeraPackOffset(mags, terapack);
		}
		else
		{
			log.log("Ejecting Terapack with specified tape [" + tape + "]", 1);
			String mediaType = getMediaType(partition, false);
			int slots_per_terapack = Inventory.findMagazineSize(mediaType);
			offset = EjectListedTapes.getTeraPackOffset(mags, tape, slots_per_terapack);
		}

		if(offset.length()>0)
		{
			log.log("TeraPack found (" + offset + ")", 1);
			
			if(printToShell)
			{
				System.out.println("TeraPack found.");
			}

			library.prepareImportExportList(partition, "storage", offset);
		
			log.log("Export list created for TeraPack offset " + offset, 2);

			if(printToShell)
			{
				System.out.println("\nImport export list uploaded to library. To export the specified TeraPacks, navigated to the Advanced menu from the General > Import/Export menu. Choose partition " + partition + " and then click on the populate button.\n");
			}
		}
		else
		{
			log.log("TeraPack not found (" + offset + ")", 1);

			if(printToShell)
			{
				System.out.println("Unable to locate specified TeraPack.");
			}
		}
	}

	public void groupListedTapes(String partition, String file_name, int max_moves, String output_format, boolean printToShell)
	{
		// Groups the listed tapes in the specified TeraPacks.
		String mediaType = getMediaType(partition, true);
		int slots_per_terapack = Inventory.findMagazineSize(mediaType);
		
		ArrayList<Move> move_list = EjectListedTapes.prepareMoveToTeraPacks(partition, file_name, max_moves, slots_per_terapack, library, printToShell, log);

		// Send moves
		if(output_format.equals("move-queue"))
		{
			MoveQueue.storeMoves("../output/MoveQueue.txt", move_list);
		}
		else
		{
			sendMoves(partition, move_list, printToShell);
		}
	}

	public int[][] magazineCapacity(String partition, boolean printToShell)
	{
		String mediaType = getMediaType(partition, false);
		int slots_per_terapack = Inventory.findMagazineSize(mediaType);

		TeraPack[] magazines = magazineContents(partition, false);

		int[][] magazine_summary = MagazineUtilization.generateSummary(magazines, slots_per_terapack, log, printToShell);
		
		return magazine_summary;
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
				System.err.println("No TeraPacks can be freed by magazine compaction.");
			}
		}
		else if(requirements[0] < 0)
		{
			log.log("Not enough moves allowed. Maximum of " + maxMoves 
					+ " specified when " + -requirements[0] 
					+ "moves are required to free 1 magazine.", 2);

			if(printToShell)
			{
				System.err.println("Not enough moves allowed. Maximum of " + maxMoves 
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

			if(false) // replace with printToShell
			{
				System.out.println("\nMaximum moves: " + maxMoves);
				System.out.println("Available moves: " + requirements[0]);
				System.out.println("Available target slots: " + requirements[1]);
				System.out.println("TeraPacks that can be freed: " + requirements[2]);
			}

			ArrayList<Move> move_list = MagazineCompaction.prepareMoves(magazines, maxMoves, partition, library, log, printToShell);
	
			if(output_type.equals("move-queue"))
			{
				// Save moves to move queue.
				MoveQueue.storeMoves("../output/MoveQueue.txt", move_list);
			}
			else
			{
				// send moves to library
				sendMoves(partition, move_list, printToShell);
			}
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
			
			if(output_format.equals("move-queue"))
			{
				// Save the moves to a move-queue file.
				MoveQueue.storeMoves("../output/MoveQueue.txt", move_list);
			}
			else
			{
				// Or send the moves directly to the library.
				sendMoves(partition, move_list, printToShell);
			}

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

	public void profileLibrary(boolean printToShell)
	{
		LibraryProfile.fullSystem(library);
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

	private boolean readyForMove(boolean printToShell)
	{
		XMLResult[] response = library.checkProgress("inventory");

		for(int i=0; i<response.length; i++)
		{
			if(response[i].headerTag.equalsIgnoreCase("message"))
			{
				if(response[i].value.equalsIgnoreCase("No Pending actions"))
				{
					if(printToShell)
					{
						System.out.println("\t[READY]");
					}

					return true;
				}
				else
				{
					System.out.print(".");
					
					try
					{
						TimeUnit.SECONDS.sleep(15);

						return readyForMove(printToShell);
					}
					catch(Exception e)
					{
						if(printToShell)
						{
							System.out.println("\t[FAILED]");
						}

						return false;
					}
				}
			}
			
		}

		System.out.println("ERROR checking library status");
		return false;
	}

	private void sendMoves(String partition, ArrayList<Move> move_list, boolean printToShell)
	{
		// Send the move to the library.
		// Wait until the move is complete before exiting function.

		XMLResult[] response;

		for(int i=0; i< move_list.size(); i++)
		{
/*
 *		In case we need to give the LCM a break after so many moves.
 *		Testing without and removing comments if needed.
 			if(i>0 && (i%1500==0))
			{
				log.INFO(i + " moves have been processed. Pausing for 1 hour.");

				if(printToShell)
				{
					System.out.println(i + " moves have been processed. Pausing for 1 hour.");
				}

				try { TimeUnit.HOURS.sleep(1); }
				catch(Exception e) { System.err.println(e.getMessage()); }
			}
*/
			if(printToShell)
			{
				System.out.print("Waiting for library");
			}

			if(readyForMove(printToShell))
			{
				log.log("Sending move " + i + ": (" + move_list.get(i).barcode 
						+ ") " + move_list.get(i).source_type + " " 
						+ move_list.get(i).source_slot + " to " 
						+ move_list.get(i).target_type + " " 
						+ move_list.get(i).target_slot, 2);

				if(printToShell)
				{
					System.out.println("Sending move " + i + ": (" + move_list.get(i).barcode 
						+ ") " + move_list.get(i).source_type + " " 
						+ move_list.get(i).source_slot + " to " 
						+ move_list.get(i).target_type + " " 
						+ move_list.get(i).target_slot);
				}

				// Reference by slot if available otherwise reference the source by barcode.
				if(!move_list.get(i).source_slot.equals("none"))
				{
					response = library.moveTape(partition, move_list.get(i).source_type, move_list.get(i).source_slot, move_list.get(i).target_type, move_list.get(i).target_slot);
				}
				else
				{
					response = library.moveTape(partition, "BC", move_list.get(i).barcode, move_list.get(i).target_type, move_list.get(i).target_slot); 
				}

				// Try adding a delay to not hammer the LCM.
				try
				{
					TimeUnit.SECONDS.sleep(15);
				}
				catch(Exception e)
				{
					System.err.println(e.getMessage());
				}
		/*		if(response[0].headerTag.equalsIgnoreCase("message"))
				{
					log.log(response[0].value, 2);
					
					if(printToShell)
					{
						System.out.println(response[0].value);
					}
				}
	*/
			}
		}

	}

}
