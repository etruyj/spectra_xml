//===================================================================
// Output.java
// 	Description:
// 		This code handles the output for the interface. It
// 		will output the final result of the basic function
// 		calls.
//
// 	Valid Formats: shell, xml.
//===================================================================

package com.socialvagrancy.spectraxml.ui;

import com.socialvagrancy.spectraxml.structures.XMLResult;

public class Output
{
	public void print(XMLResult[] results, String format, boolean includeHeaders)
	{
		// Check to make sure there is output to print
		if(!(results.length==1 && results[0].headerTag.equals("none")))
		{
			if(format.equalsIgnoreCase("shell"))
			{
				printShell(results, includeHeaders);
			}
			else if(format.equalsIgnoreCase("xml"))
			{
				printXML(results);
			}
			else if(format.equalsIgnoreCase("debug"))
			{
				printDebug(results);
			}
		}
	}

	private void printDebug(XMLResult[] results)
	{
		//=================================================================
		// printDebug
		//	This is a little diddy I like to call saving me the effort
		//	of putting System.out.println()s randomly through the code
		//	to figure out what I'm doing wrong, and then forgetting them
		//	and finding said System.out.println()s at embarrassing customer
		//	facing times.
		//=================================================================

		for(int i=0; i<results.length; i++)
		{
			System.out.println(results[i].headerTag + "\t\t\t" + results[i].value);
		}
	}

	private void printShell(XMLResult[] results, boolean includeHeaders)
	{
		//=================================================================
		// printShell
		// 	Prints normal output to the shell. Return values can include
		// 	headers if it's beneficial to the output. Return headers is
		// 	defined by the function call.
		//=================================================================
		
		// XML Document level values.
		int previous_level = 1;
		int level;
		int itr;
		
		String[] headers;

		for(int i=0; i<results.length; i++)
		{
			headers = results[i].headerTag.split(">");
			level = headers.length;

			//============================================
			// Format output
			// 	Format the output by printing the opening
			// 	headers on this line.
			//============================================
			//
			if(includeHeaders && (level > previous_level))
			{
				itr = previous_level - 1;

				while(itr < level - 1)
				{
					for(int j=0; j<itr; j++)
					{
							System.out.print("\t");
					}

					System.out.println(headers[itr] + ": ");

					itr++;
				}
			}
			//
			//============================================
			// END FORMATTING
			//============================================
		
			// ONLY PRINT NON-EMPTY FIELDS
			if(!(results[i].value.equals("")))
			{	
				for(int j=0; j<headers.length; j++)
				{
					if(j>0)
					{
						System.out.print("\t");
					}
				}
		
				if(includeHeaders)
				{
					System.out.print(headers[headers.length-1] + ": ");
				}

				System.out.println(results[i].value);
			}

			previous_level = level;	
		}
	}

	private void printXML(XMLResult[] results)
	{
		//=================================================================
		// printXML
		// 	Converts the XMLResult pair back into an XML output to be
		// 	printed on the screen. Added this feature to allow other
		// 	scripts to pick up the output of this code.
		//=================================================================
		// Document level
		int previous_level = 1;
		int level;
		int itr;
		String[] headers;
		String[] old_headers;
	
		System.out.println("-<library>");

		for(int i=0; i<results.length; i++)
		{
			headers = results[i].headerTag.split(">");
			level = headers.length;

			//============================================
			// PRINT OPENING AND CLOSING TAGS
			// 	The next two if/while statement pairs are used
			// 	to step up and down the XML document level by
			// 	printing the proper spacing and all tags up
			// 	to the lines value and tag pair.
			//
			// 	This occurs in two steps. Tags must be
			// 	stepped up from 0 to the first value.
			//
			// 	Tags must be stepped down to the next
			// 	level after the specified tag.
			//===========================================
			// Climb up from previous input.
			// this is only necessary if the current level > the previous doc level.
			// itr = previous_level - 1 as level 1 is actually an iterator of 0.
			//
			if(level > previous_level)
			{
				itr = previous_level-1;
				while(itr < level - 1)
				{
					// Print tabs to display out to the specific level.
					for(int j=0; j<=itr; j++)
					{
						System.out.print("\t");
					}

					// Print the opening tag that would otherwise be skipped
					// as there is no associated value.
					System.out.println("<" + headers[itr] + ">");
					
					itr++;
				}

			}
			//
			// Descend from previous input.
			// This code prints the closing tags for all the headers.
			// itr = previous_level - 1 as level 1 is actually an interator value of 0.
			// printing old_headers[itr-1] for the same reason, otherwise the subsequent
			// tag is printed.
			if(level < previous_level)
			{
				itr = previous_level - 1;
				old_headers = results[i-1].headerTag.split(">");

				while(itr >= level)
				{
					// print tabs
					for(int j=0; j<itr; j++)
					{
						System.out.print("\t");
					}	

					System.out.println("</" + old_headers[itr-1] + ">");

					itr--;
				}
			}
			//============================================
			// END FORMATTING
			// END XML FORMATTING PANEL
			//============================================
			// Print current input
			if(level >= previous_level)
			{
				for(int j=0; j<headers.length; j++)
				{
					System.out.print("\t");
				}

				if(results[i].value != null)
				{
					System.out.print("<" + headers[headers.length-1] + ">");
					System.out.print(results[i].value.trim());
					// Don't remeber why this was necessary. Removed it as it 
					// cleared all the spaces out of XML output.
					// System.out.print(results[i].value.replaceAll("\\s", ""));
					System.out.println("</" + headers[headers.length-1] + ">");
				}
			}
			
			previous_level = level; // Store this as the previous document level.
		}

		System.out.println("</library>");
	}
}
