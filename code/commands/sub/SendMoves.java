//===================================================================
// SendMove.java
// 	Description: Parses move queue to send moves directly to the
// 	library. Currently only works with TFINITY.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.BasicXMLCommands;
import com.socialvagrancy.spectraxml.structures.Move;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class SendMoves
{
	public static void fromMoveList(BasicXMLCommands library, String partition, ArrayList<Move> move_list, Logger log, boolean printToShell)
	{
		// Send the move to the library.
		// Wait until the move is complete before exiting function.

		DateTimeFormatter output_format = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss");
		LocalDateTime timestamp;
		LocalDateTime first_move;
		LocalDateTime last_move;

		XMLResult[] response;

		first_move = LocalDateTime.now();

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

			if(readyForMove(library, printToShell))
			{
				log.INFO("Sending move " + i + ": (" + move_list.get(i).barcode 
						+ ") " + move_list.get(i).source_type + " " 
						+ move_list.get(i).source_slot + " to " 
						+ move_list.get(i).target_type + " " 
						+ move_list.get(i).target_slot);

				if(printToShell)
				{
					timestamp = LocalDateTime.now();

					System.out.println(timestamp.format(output_format) + " : Sending move " + i + ": (" + move_list.get(i).barcode 
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
			}
		}

		// Report on success.
		
		last_move = LocalDateTime.now();
		Duration dur = Duration.between(first_move, last_move);
		
		long seconds = dur.toSeconds() % 60;
		long minutes = dur.toMinutes() % 60;
		long hours = dur.toHours();

		log.INFO("Completed " + move_list.size() + " moves in " + hours + ":" + minutes + ":" + seconds);
		
		if(printToShell)
		{
			System.out.println("------------------------------------");
			System.out.println("Completed " + move_list.size() + " moves in " + hours + ":" + minutes + ":" + seconds);
		}
	}

	private static boolean readyForMove(BasicXMLCommands library, boolean printToShell)
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

						return readyForMove(library, printToShell);
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
}

