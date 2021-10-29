package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.spectraxml.structures.TeraPack;
import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class EjectListedTapes
{
	public static String getTeraPackOffset(TeraPack[] mags, String barcode)
	{
		boolean searching = true;
		int itr = 0;
		String offset = "";

		while(searching && itr < mags.length)
		{
			if(mags[itr].getMagazineBarcode().equals(barcode))
			{
				searching = false;
				offset = mags[itr].getOffset();
			}

			itr++;
		}
		
		return offset;
	}

	public static String getTeraPackOffset(TeraPack[] mags, String barcode, int slots_per_terapack)
	{
		for(int i=mags.length-1; i>=0; i--)
		{
			for(int j=0; j<slots_per_terapack; j++)
			{
				if(mags[i].getTapeBarcode(j).equals(barcode))
				{
					return mags[i].getOffset();
				}
			}
		}

		return "none";
	}

	public static ArrayList<Move> prepareMoveToTeraPacks(String partition, String filename, int max_moves, int slots_per_terapack, BasicXMLCommands library, boolean printToShell, Logger log)
	{
		ArrayList<String> tape_list = LoadFile.tapeList(filename);
		ArrayList<String> empty_slots = Inventory.findEmptyTeraPacks(library, partition, slots_per_terapack);
		ArrayList<Move> move_list = new ArrayList<Move>();
		Move move;
		
		int i=0;

		if(tape_list.size()==0)
		{
			log.log("Restore file was empty.", 3);
		}
		if(empty_slots.size()==0)
		{
			log.log("No empty terapacks available.", 3);
		}


		while(i<max_moves && i<tape_list.size() && i<empty_slots.size())
		{
			move = new Move();

			move.barcode = tape_list.get(i);
			move.target_type = "SLOT";
			move.target_slot = empty_slots.get(i);

			move_list.add(move);

			i++;
		}

		// Clean out the old data.
		exportCompletedTapes(tape_list, i, "../output/completed_tapes.txt");
		exportRemainingTapes(tape_list, i, filename);

		for(int j=0; j<i; j++)
		{
			tape_list.remove(0);
		}

		return move_list;
	}

	public static String prepareImportExportList(String partition, TeraPack[] mags, int slots_per_terapack, String filename, Logger log)
	{
		ArrayList<String> tape_list = LoadFile.tapeList(filename);
		ArrayList<Integer> offset_list = new ArrayList<Integer>();
		String offset;
		String response = "";

		for(int i=0; i<tape_list.size(); i++)
		{
			offset = getTeraPackOffset(mags, tape_list.get(i), slots_per_terapack);

			if(!offset.equals("none"))
			{
				offset_list.add(Integer.valueOf(offset));
			}
			else
			{
				log.log("Couldn't find TeraPack with barcode (" + tape_list.get(i) + ")", 2);
			}
		}

		Collections.sort(offset_list);
		offset = "none";

		for(int i=0; i<offset_list.size(); i++)
		{
			if(!offset.equals(Integer.toString(offset_list.get(i))))
			{
				offset = Integer.toString(offset_list.get(i));
				response = response + Integer.toString(offset_list.get(i)-1) + ",";		
			}
		}

		return response.substring(0, response.length()-1);
	}

	private static void exportCompletedTapes(ArrayList<String> tapes, int toStore, String fileName)
	{
		FileManager fm = new FileManager();

		for(int i=0; i<toStore; i++)
		{
			fm.appendToFile(fileName, tapes.get(i));
		}
	}

	private static void exportRemainingTapes(ArrayList<String> tapes, int toStore, String fileName)
	{
		FileManager fm = new FileManager();

		fm.createFileDeleteOld(fileName, true);

		for(int i=toStore; i<tapes.size(); i++)
		{
			fm.appendToFile(fileName, tapes.get(i));
		}
	}

}
