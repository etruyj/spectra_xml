//============================================================================
// MagazineCompaction.java
// 	Description:
//		This class holds all the functions related to the magazine
//		compaction command.
//============================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.spectraxml.structures.TeraPack;
import com.socialvagrancy.spectraxml.structures.XMLResult;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class MagazineCompaction
{
	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================

	public static int[] checkMoves(TeraPack[] mags, int maxMoves)
	{

		int source = 0;
		int destination = mags.length - 1;
		int availMoves = 0; // how many moves can be made.
		int availSlots = 0; // how many slots can be used.
		int availTeraPacks = 0;
		int[] response = new int[3];

		while(source < destination)
		{
			availMoves += mags[source].getCapacity();
			availSlots += mags[destination].getNumSlots() - mags[destination].getCapacity();
			availTeraPacks++;	
		
			if(source==0 && availMoves > maxMoves)
			{
				// Checking to see if a higher maximum must be specified
				// to empty the first TeraPack.
				// Set the source to destination to end the loop.
				// Make availMoves negative for error handling.
				source = destination;
				availMoves = -1 * availMoves;
			}

			source++;
			destination--;
		}

		// Add the last TeraPack of slots to the library
		// for reporting purposes. Necessary for odd number
		// of TeraPacks.
		if(source == destination)
		{
			availSlots += mags[destination].getNumSlots() - mags[destination].getCapacity();
		}

		response[0] = availMoves;
		response[1] = availSlots;
		response[2] = availTeraPacks;

		return response;
	}

	public static ArrayList<Move> prepareMoves(TeraPack[] mags, int maxMoves, String partition, BasicXMLCommands library, Logger log, boolean printToShell)
	{
		int source = 0; // Incrementor for source TP
		int destination = mags.length - 1; // Increment for destination TP
		int sourceTapes = mags[source].getCapacity(); // How many tapes are in the Source Magazine.
		int destSlots = mags[destination].getNumSlots() - mags[destination].getCapacity(); // How many slots are available in the destination TeraPack.
/*
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
	*/
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
		XMLResult[] inventory = library.listInventory(partition);

		Move queued_move;
		ArrayList<Move> move_queue = new ArrayList<Move>();
	
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

				sourceSlotString = VerifyMove.findSlotString(partition, sourceBarcode, inventory);
				checkSlotString = VerifyMove.findSlotString(partition, checkBarcode, inventory);
				destSlotString = VerifyMove.findDestinationSlot(partition, emptySlot, checkSlot, checkBarcode, inventory);

				// Move validation.
				// Will be removed/commented out in a future release.
				// This was placed here when the slot was calculated
				// by generateSlotString() to verify the calculated
				// value. findSlotString() performs a similar task as
				// validateMove(), so this function validates the move
				// with by the same process that generates it. It's
				// redundant.
				isValidMove = true; // Marking this true for now to skip the move validation field.
				// isValidMove = VerifyMove.validate(partition, sourceSlotString, sourceBarcode, destSlotString, checkSlotString, checkBarcode, library, log, true);				
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
				queued_move = new Move();
				queued_move.barcode = sourceBarcode;
				queued_move.source_type = "SLOT";
				queued_move.source_slot = sourceSlotString;
				queued_move.target_type = "SLOT";
				queued_move.target_slot = destSlotString;

				move_queue.add(queued_move);

				log.log("Queueing move [" + sourceBarcode + "] from slot " + sourceSlotString
						+ " to slot " + destSlotString, 2);
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

		return move_queue;

	}
	
}
