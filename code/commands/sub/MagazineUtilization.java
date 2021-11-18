//===================================================================
// MagazineUtilization.java
// 	Description: Takes a physical inventory and responds with how
// 	full each of the magazines are.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.structures.TeraPack;
import com.socialvagrancy.utils.Logger;

public class MagazineUtilization
{
	public static int[][] calculate(int[][] summary, TeraPack[] magazines, int max_size)
	{
		int section; // which section.
		int utilization; 
		int storage_mags = 0;
		int entry_exit_mags = 0;

		for(int i=0; i<magazines.length; i++)
		{
			if(magazines[i].getLocation().equalsIgnoreCase("storage"))
			{
				section = 0;
				storage_mags++;
			}
			else
			{
				// Entry Exit
				section = 1;
				entry_exit_mags++;
			}

			summary[section][magazines[i].getCapacity()]++;
		}

		summary[0][max_size+1] = storage_mags;
		summary[1][max_size+1] = entry_exit_mags;

		return summary;
	}
	
	public static void displayResults(int[][] summary, int max_size)
	{
		System.out.println("\nStorage Magazines: " + summary[0][max_size+1]);
		System.out.println("Entry/Exit Magazines: " + summary[1][max_size+1] + "\n");
		System.out.println("Summary of occupied slots:");
		System.out.println("\t\tStorage\t\tEntry/Exit");

		for(int i=max_size; i>=0; i--)
		{
			if(summary[0][i]>0 || summary[1][i]>0)
			{
				if(i == max_size)
				{
					System.out.println("Full:\t\t" + summary[0][i] + "\t\t" 
							+ summary[1][i]);
				}
				else if(i == 0)
				{
					System.out.println("Empty:\t\t" + summary[0][i] + "\t\t" 
							+ summary[1][i]);
				}
				else
				{
					System.out.println("(" + i + ") tapes:\t" + summary[0][i] 
							+ "\t\t" + summary[1][i]);
				}
			}
		}
	}

	public static int[][] generateSummary(TeraPack[] magazines, int max_size, Logger log, boolean printToShell)
	{
		log.log("Initializing summary array...", 1);

		// Tracking the variables.
		// [0][] storage.
		// [1][] entry/exit.
		int[][] magazine_summary = initializeSummary(max_size);
		int storage_mags = 0;
		int entry_exit_mags = 0;

		log.log("Calculating magazine contents...", 1);
		magazine_summary = calculate(magazine_summary, magazines, max_size);

		log.log("Found (" + magazine_summary[0][max_size] + ") storage TeraPacks", 2);
		log.log("Found (" + magazine_summary[1][max_size] + ") entry/exit TeraPacks", 2);

		if(printToShell)
		{
			displayResults(magazine_summary, max_size);
		}

		return magazine_summary;
	}

	public static int[][] initializeSummary(int max_size)
	{
		// The last index will be used to store the total values.
		int[][] magazine_summary = new int[2][max_size+2];

		for(int i=0; i<2; i++)
		{
			for(int j=0; j<max_size+2; j++)
			{
				magazine_summary[i][j] = 0;
			}
		}

		return magazine_summary;
	}

}
