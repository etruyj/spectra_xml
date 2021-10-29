//===================================================================
// Inventory.java
// 	A collection of commands for operating on the inventory
// 	of the library such as identifying specific slots.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.spectraxml.structures.XMLResult;

import java.util.ArrayList;

public class Inventory
{
	public static ArrayList<String> findEmptySlots(BasicXMLCommands library, String partition)
	{
		ArrayList<String> empty_slots = new ArrayList<String>();
		boolean searching = true;
		int itr = 0;
		String checkSlot = "0";

		XMLResult[] response = library.listInventory(partition);

		// Build a list of all available slots
		while(searching)
		{
			// Grab a slot number
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>Offset"))
			{
				checkSlot = response[itr].value;
			}

			// Determine if that slot is occupied.
			// If not store the number for verification.
			if(response[itr].headerTag.equalsIgnoreCase("partition>storageSlot>full") && response[itr].value.equalsIgnoreCase("No"))
			{
				empty_slots.add(checkSlot);
			}

			// Determine the end of the search
			if(response[itr].headerTag.equalsIgnoreCase("partition>entryExitSlot"))
			{
				searching = false;
			}
			
			itr++;

			if(itr >= response.length)
			{
				searching = false;
			}
		}
	
		return empty_slots;	
	}

	public static ArrayList<String> findEmptyTeraPacks(BasicXMLCommands library, String partition, int magazine_size)
	{
		ArrayList<String> empty_slots = findEmptySlots(library, partition);
		ArrayList<String> terapack_slots = new ArrayList<String>();

		int firstSlot = Integer.valueOf(empty_slots.get(0));

		int terapack = firstSlot % magazine_size;
		terapack = (firstSlot - terapack) + 1;

		boolean searching = true;
		int itr = 0;

		while(searching)
		{
			// If in the last slot of the Tp increment the tp
			if(Integer.valueOf(empty_slots.get(itr)) > (terapack + magazine_size - 1))
			{
				terapack = Integer.valueOf(empty_slots.get(itr)) - Integer.valueOf(empty_slots.get(itr)) % magazine_size + 1;
			}
			
			// If the slot is the first in the terapack
			// check the index + mag size to determine
			// if all the slots are in the list.
			if(Integer.valueOf(empty_slots.get(itr)) == terapack)
			{
				if(Integer.valueOf(empty_slots.get(itr + magazine_size-1)) == (terapack + magazine_size -1))
				{
					// add slots to array
					for(int i = itr; i<itr+magazine_size; i++)
					{
						terapack_slots.add(empty_slots.get(i));
					}

				}
			}

			
			// Check to see if we got tot he end of the array.
			// if the last slot in the terapack is higher than
			// the last value of the list, we're done.
			if((terapack + magazine_size - 1) > Integer.valueOf(empty_slots.get(empty_slots.size()-1)))
			{
				searching = false;
			}
			
			itr++;

			if(itr>=empty_slots.size())
			{
				searching = false;
			}
		}

		return terapack_slots;
	}

	public static int findMagazineSize(String libraryType)
	{
		if(libraryType.equalsIgnoreCase("LTO"))
		{
			return 10;
		}
		else
		{
			return 9;
		}
	}
}
