//===================================================================
// CalibrateDrives.java
// 	These class contains all the commands relative to the library's
// 	hardware.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class CalibrateDrives
{
	public static ArrayList<Move> prepareMoves(BasicXMLCommands library, String partition, Logger log)
	{
		log.log("Gathering drive list", 1);
		ArrayList<String> drive_list = getDrives(library, partition);
		
		log.log("Pulling library inventory", 1);		
		XMLResult[] inventory = library.listInventory(partition);

		log.log("Mapping moves", 1);
		ArrayList<Move> move_list = calibrationMoves(drive_list, inventory);		
	
		log.log("Finding home slots of the tapes.", 1);
		move_list = reverseMoves(library, partition, move_list);

		return move_list;
	}

	public static ArrayList<Move> calibrationMoves(ArrayList<String> drive_list, XMLResult[] inv)
	{
		// Assign tapes to slots.
		// If tapes < drives restart with the drive count.

		ArrayList<Move> move_list = new ArrayList<Move>();
		Move move;
	
		int drive = 0;
		int tape = 0;
		int itr = 0;

		while(drive < drive_list.size())
		{
			if(tape < inv.length)
			{
				// Run through the inventory list first
				if(inv[tape].headerTag.equalsIgnoreCase("partition>storageSlot>barcode"))
				{
					move = new Move();
					move.barcode = inv[tape].value.trim();
					move.source_type = "BC";
					move.target_type = "Drive";
					move.target_slot = drive_list.get(drive);

					move_list.add(move);

					drive++;
				}

				tape++;
			}
			else
			{
				// If all tapes have been selected and more
				// drives are needed, move them from drive
				// to drive.
				if(drive>0)
				{
					move = new Move();

					move.source_type = "Drive";
					move.source_slot = drive_list.get(itr);
					move.target_type = "Drive";
					move.target_slot = drive_list.get(drive);

					itr++;
					drive++;
				}
				else
				{
					// No tapes in partition;
					// break the loop
					drive = drive_list.size();
				}
			}
		}

		return move_list;
	}	

	public static ArrayList<String> getDrives(BasicXMLCommands library, String partition)
	{
		XMLResult[] drives = library.listDrives();
		ArrayList<String> drive_list = new ArrayList<String>();

		boolean correctPartition = false;

		for(int i=0; i<drives.length; i++)
		{
			if(drives[i].headerTag.equalsIgnoreCase("drive>partition") && drives[i].value.equalsIgnoreCase(partition))
			{
				correctPartition = true;
			}

			if(correctPartition && drives[i].headerTag.equalsIgnoreCase("drive>partitionDriveNumber"))
			{
				drive_list.add(drives[i].value);
				correctPartition = false;
			}
		}

		return drive_list;
	}

	public static ArrayList<Move> reverseMoves(BasicXMLCommands library, String partition, ArrayList<Move> move_list)
	{
		ArrayList<String> source_slots = new ArrayList<String>();
		
		Move move;
		int move_count = move_list.size();
		String slot;

		for(int i=move_count-1; i>=0; i--)
		{
			slot = VerifyMove.findSlotString(partition, move_list.get(i).barcode, library);

			if(!isDuplicate(source_slots, slot, 0, source_slots.size()))
			{
				move = new Move();
		
				move.source_type = move_list.get(i).target_type;
				move.source_slot = move_list.get(i).target_slot;
				move.target_type = "Slot";
				move.target_slot = slot;

				move_list.add(move);
				
				source_slots.add(slot);
				Collections.sort(source_slots);
			}	
		}

		return move_list;
	}

	public static boolean isDuplicate(ArrayList<String> destinations,String target, int min, int max)
	{
		int index = min + max / 2;

		// Check
		if(index < min || index >= max)
		{
			return false;
		}
		else if(destinations.get(index).equals(target))
		{
			return true;
		}
		else
		{
			int slot = Integer.valueOf(target);
			int comparison = Integer.valueOf(destinations.get(index));
			if(slot > comparison)
			{
				return isDuplicate(destinations, target, index, max);
			}
			else // if(slot < comparison) : slot == comparision is above
			{
				return isDuplicate(destinations, target, min, index);
			}
		}
	}	
	
}
