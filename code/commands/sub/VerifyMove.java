//============================================================================
// VerifyMove.java
// 	Description:
//		This class holds the code necessary to validate a move before
//		it is sent to the library.
//============================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.spectraxml.utils.XMLParser;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class VerifyMove
{
	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================

	//Shifting functions to take XMLResult[] instead of BasicXMLCommands to reduce the number of calls to the 
	// LCM and speed up execution.
	public static String findDestinationSlot(String partition, int destSlot, int magSlot, String barcode, BasicXMLCommands library)
	{
		// Finds the slot string of the destination slot
		// by locating the slot of a tape within the same
		// TeraPack and adjusting the slot based on the difference.

		// destSlot = target
		// magSlot = slot occupied by passed barcode.
		int difference = destSlot - magSlot;
		String anchor = "none";
		anchor = findSlotString(partition, barcode, library);

		if(!anchor.equals("none"))
		{
			difference = difference + Integer.valueOf(anchor);
			return Integer.toString(difference);
		}

		return "none";		
	}

	public static String findDestinationSlot(String partition, int destSlot, int magSlot, String barcode, XMLResult[] inventory)
	{
		// Finds the slot string of the destination slot
		// by locating the slot of a tape within the same
		// TeraPack and adjusting the slot based on the difference.

		// destSlot = target
		// magSlot = slot occupied by passed barcode.
		int difference = destSlot - magSlot;
		String anchor = "none";
		anchor = findSlotString(partition, barcode, inventory);

		if(!anchor.equals("none"))
		{
			difference = difference + Integer.valueOf(anchor);
			return Integer.toString(difference);
		}

		return "none";		
	}

	public static ArrayList<String> findEmptyTeraPacks(String partition, int magazine_size, BasicXMLCommands library, boolean printToShell)
	{
		ArrayList<String> empty_slots = findEmptySlots(partition, library);
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

	public static ArrayList<String> findEmptyTeraPacks(String partition, int magazine_size, XMLResult[] inventory, boolean printToShell)
	{
		ArrayList<String> empty_slots = findEmptySlots(partition, inventory);
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

	public static ArrayList<String> findEmptySlots(String partition, BasicXMLCommands library)
	{
		ArrayList<String> empty_slots = new ArrayList<String>();
		boolean searching = true;
		int itr = 0;
		String checkSlot = "0";

		XMLResult[] response = library.listInventory(partition);
		
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

	public static ArrayList<String> findEmptySlots(String partition, XMLResult[] inventory)
	{
		ArrayList<String> empty_slots = new ArrayList<String>();
		boolean searching = true;
		int itr = 0;
		String checkSlot = "0";
		
		// Build a list of all available slots.
		while(searching)
		{
			// Grab a slot number.
			if(inventory[itr].headerTag.equalsIgnoreCase("partition>storageSlot>Offset"))
			{
				checkSlot = inventory[itr].value;
			}
			
			// Determine if that slot is occupied.
			// If not store the number for verificatation.
			if(inventory[itr].headerTag.equalsIgnoreCase("partition>storageSlot>full") && inventory[itr].value.equalsIgnoreCase("No"))
			{
				empty_slots.add(checkSlot);
			}

			// Determine the end of the search.
			if(inventory[itr].headerTag.equalsIgnoreCase("partition>entryExitSlot"))
			{
				searching = false;
			}

			itr++;
		}

		return empty_slots;
	}	

	public static String findSlotString(String partition, String barcode, BasicXMLCommands library)
	{
		// Search the inventory for the barcode.
		// Export the Slot number of the barcode.

		boolean slotFound = false;
		String slot = "none";
		int itr = 0; // using while with an iterator to save cycles.

		XMLResult[] response = library.listInventory(partition);

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
	
	public static String findSlotString(String partition, String barcode, XMLResult[] inventory)
	{
		// Search the inventory for the barcode.
		// Export the Slot number of the barcode.

		boolean slotFound = false;
		String slot = "none";
		int itr = 0; // using while with an iterator to save cycles.

		while(!slotFound)
		{
			if(inventory[itr].headerTag.equalsIgnoreCase("partition>storageSlot>Offset"))
			{
				slot = inventory[itr].value;
			}
			
			// have to use trim() as there's whitespace in the barcode
			// for some reason.
			if(inventory[itr].headerTag.equalsIgnoreCase("partition>storageSlot>barcode") && inventory[itr].value.trim().equalsIgnoreCase(barcode))
			{
				slotFound = true;
			}

			itr++;
		}

		return slot;
	}
	
	public static boolean validate(String partition, String sourceSlot, String sourceBarcode, String destSlot, String destSlot2, String destBarcode, BasicXMLCommands library, Logger log, boolean printToShell)
	{
		// The purpose of this function is to validate the source and destination slot
		// against the library inventory before initiating the move.
		// I'm guessing the formula for slot number is (10 * TeraPack Offset) - (10 - TeraPack[i].tape's array index) + 1.
		// I'm also assuming the TeraPack offsets will update along with the slot numbers if a 
		// TeraPack is exported or imported into the library.
		// As there is a lot of guessing.... there's a validateMove.
		

		// Sort the three slots into ascending order
		// Needed to compare against the order the results come in.
		ArrayList<String> slotOrder = new ArrayList<String>();
		
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

		XMLResult[] response = library.listInventory(partition);

		
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
