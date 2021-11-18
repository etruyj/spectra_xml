//===================================================================
// LibraryProfile.java
// 	This command produces a summary of the hardware.
// 		- Available Slots
// 		- Licenseds Slots
// 		- Total TeraPacks.
// 		- Total Tapes
// 		- Total Drives
// 		- Partition:
// 			# - Assigned Chambers
// 			# - Occupied TeraPacks
// 			# - Number of tapes
// 			# - Number of drives
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.XMLResult;

public class LibraryProfile
{
	public static void fullSystem(BasicXMLCommands library)
	{
		int chambers = calculateLicensedChambers(library);

		System.out.println("Licensed Chambers: " + chambers);
	}

	public static int calculateLicensedChambers(BasicXMLCommands library)
	{
		int chambers = 0;
		String[] breakdown;

		XMLResult[] result = library.listOptionKeys();

		for(int i=0; i<result.length; i++)
		{
			if(result[i].headerTag.equalsIgnoreCase("optionKey>description") && result[i].value.substring(0, 16).equalsIgnoreCase("Capacity License"))
			{
				breakdown = result[i].value.split(" ");
				
				chambers += Integer.valueOf(breakdown[2]);
			}
		}

		return chambers;
	}
}
