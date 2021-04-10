//============================================================================
// SpectraController.java
// 	Description:
// 		This class makes all the appropriate function calls to the tape
// 		library. 
//============================================================================

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectraController
{
	private Connector cxn;
	private String libraryAddress;

	//====================================================================
	// Constructor
	//====================================================================
	
	public SpectraController(String server, boolean secure)
	{
		cxn = new Connector();
		
		if(secure)
		{
			libraryAddress = "https://";
		}
		else
		{
			libraryAddress = "http://";
		}
		
		libraryAddress = libraryAddress + server + "/gf/";

	}

	//====================================================================
	// Get URL Functions
	// 	These functions generate the URLs used to Query the library.
	//====================================================================

	private String getImportExportListURL(String partition, String location, String magazine_offsets)
	{
		return libraryAddress + "mediaExchange.xml?action=prepareImportExportList&partition=" + partition + "&slotType=" + location + "&TeraPackOffsets=" + magazine_offsets;
	}

	private String getInventoryListURL(String partition)
	{
		return libraryAddress + "inventory.xml?action=list&partition=" + partition;
	}	

	private String getLoginURL(String user, String password)
	{
		return libraryAddress + "login.xml?username=" + user 
			+ "&password=" + password;
	}

	private String getLogoutURL()
	{
		return libraryAddress + "logout.xml";
	}

	private String getMoveURL(String partition, String sourceID, String sourceNumber, String destID, String destNumber)
	{
		// Generates the move URL for the XML interface.
		// parititon - the partition in which the move will occur.
		// sourceID - What type of source is being specified.
		// 		valid inputs are SLOT, EE, DRIVE,
		// 		and BC (barcode)
		// sourceNumber - How the source is identified.
		// 		Slot (offset) or barcode.
		// destID - What type of destination is being specified
		// 		valid inputs are SLOT, EE, DRIVE
		// destNumber - What is the Slot (offset) of the destination.

		return libraryAddress + "inventory.xml?action=move&partition=" + partition 
			+ "&sourceID=" + sourceID + "&sourceNumber=" + sourceNumber 
			+ "&destinationID=" + destID + "&destinationNumber=" + destNumber;
	}

	private String getPartitionListURL()
	{
		return libraryAddress + "partition.xml?action=list";
	}

	private String getPhysicalInventoryURL(String partition)
	{
		return libraryAddress + "physInventory.xml?partition=" + partition;
	}

	//====================================================================
	// Control Functions
	// 	These are the public functions callable by the script.
	//====================================================================
	/*
	public XMLResult[] filterPartitionName(String header, String partitionName, XMLResult[] fullResult)
	{
		XMLResult[] filteredResult = new XMLResult[1];
		boolean inPartition = false;
		
		for(int i=0; i<fullResult.length; i++)
		{
			// Found the partition if headerTag equals search tag and value equals name.
			if(fullResult.headerTag.equalsIgnoreCase(header)&&fullResult.value.equalsIgnoreCase(partitionName))
			{
				inPartition = true;
			}
			
			// Mark the bool as false once we exit the partition.
			if(!(fullResult.headerTag.equalsIgnoreCase(header)&&fullResult.value.equalsIgnoreCase(partitionName)))
			{
				inPartition = true;
			}
			
			if(inPartition)
			{
				filteredResult.add(fullResult[i]);
			}
		}
		return filteredResult;
	}*/

	public int countMagazines(String partition)
	{
		// Queries the library for 
		// Counts the number of magazines in the partition.

		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"offset"};

		String url = getPhysicalInventoryURL(partition);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(response[0].value.equals("no results"))
		{
			return 0;
		}
		else
		{
			return response.length;
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

			if(emptyTeraPacks>0)
			{
				// Shave off the last comma.
				offset.setLength(offset.length()-1);
			
				String xmlOutput;

				XMLParser xmlparser = new XMLParser();
				String[] searchTerms = {"status",
							"message"};

				url = getImportExportListURL(partition, "storage", offset.toString());
				xmlOutput = cxn.queryLibrary(url);

				xmlparser.setXML(xmlOutput);
				response = xmlparser.parseXML(searchTerms);
			}

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

	public XMLResult[] listInventory(String partition, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"partition",
					"storageSlot",
					"entryExitSlot",
					"drive",
					"id",
					"offset",
					"barcode",
					"isQueued",
					"full"};
	
		String url = getInventoryListURL(partition);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "partition", true);
		}

		return response;
	}

	public XMLResult[] listPartitionDetails(String option, boolean printToShell)
	{
		// Get detailed information on the partitions.
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"name",
					"slotsPerChamber",
					"numStorageSlots",
					"numEESlots",
					"eeType",
					"includeDriveAndMediaGenerationInRES",
					"exporters",
					"exporter",
					"drives",
					"globalSpares",
					"MLMMediaVerification",
					"preScan",
					"postScan",
					"scanAfterDays",
					"scanAfterWrite",
					"scanAfterRead",
					"daysToScanAfter",
					"allowUsers",
					"cleaningPartition",
					"id",
					"type"};
			
		String url = getPartitionListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "name", true);
		}

		return response;
	}

	public void listPartitions(boolean printToShell)
	{
		// Print a list of partitions
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"name"};

		String url = getPartitionListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "name", false);
		}
	}

	public boolean login(String user, String password)
	{
		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status"};

		String url = getLoginURL(user, password);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(response[0].value.equalsIgnoreCase("OK"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean logout()
	{
		String xmlOutput;

		String url = getLogoutURL();
		xmlOutput = cxn.queryLibrary(url);
		
		return true;
	}

	public void magazineCapacity(String partition, boolean printToShell)
	{
		// This prints a summary of the magazine contents.
		float utilization = 0;
		TeraPack[] magazines = magazineContents(partition, false);

		int full = 0;
		int empty = 0;
		int almostEmpty = 0;
		int quarter = 0;
		int half = 0;
		int threeQuarter = 0;

		for(int i=0; i<magazines.length; i++)
		{
			utilization = (float)magazines[i].getCapacity() / magazines[i].getNumSlots();
			if(utilization == 1) { full++; }
			else if(utilization >= .75) { threeQuarter++; }
			else if(utilization >= .5) { half++; }
			else if(utilization >= .25) { quarter++; }
			else if(utilization > 0) { almostEmpty++; }
			else if(utilization == 0) { empty++; }
			
		}

		if(printToShell)
		{
			partition = partition.replace("%20", " ");
			System.out.println("There are " + magazines.length + " TeraPacks in partition " + partition);
			if(full>0) { System.out.println("Full: " + full); }
			if(threeQuarter>0) { System.out.println(">75%: " + threeQuarter); }
			if(half>0) { System.out.println(">50%: " + half); }
			if(quarter>0) { System.out.println(">25%: " + quarter); }
			if(almostEmpty>0) { System.out.println("<25%: " + almostEmpty); }
			if(empty>0) { System.out.println("Empty: " + empty); }
		}

	}

	public void magazineCompaction(String partition, int maxMoves, boolean printToShell)
	{
		TeraPack[] magazine = sortMagazines(partition, true);
		
		moveTape(partition, magazine, maxMoves, true);
	}

	public TeraPack[] magazineContents(String partition, boolean printToShell)
	{
		// This creates an array of terapacks for higher level work
		// and also performs a cleaner output than the physical inventory
		// option.

		// Get the library type, needed to know TeraPack size
		String libraryType = "none";
		XMLResult[] response = listPartitionDetails(partition, false);
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
		int magazineCount = countMagazines(partition);
		
		// Parse the magazine XML data to make an array of Terapacks.
		response = physicalInventory(partition, false);
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

	public XMLResult[] moveTape(String partition, String sourceID, String sourceNumber, String destID, String destNumber, boolean printToShell)
	{
		// Issue a move command on the library.
		// This feature isn't supported for BlueScale before 12.8
		// parititon - the partition in which the move will occur.
		// sourceID - What type of source is being specified.
		// 		valid inputs are SLOT, EE, DRIVE,
		// 		and BC (barcode)
		// sourceNumber - How the source is identified.
		// 		Slot (offset) or barcode.
		// destID - What type of destination is being specified
		// 		valid inputs are SLOT, EE, DRIVE
		// destNumber - What is the Slot (offset) of the destination.

		String xmlOutput;
		XMLResult[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status", "message"};

		String url = getMoveURL(partition, sourceID, sourceNumber, destID, destNumber);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "none", false);
		}
	
		return response;
	}

	public XMLResult[] physicalInventory(String partition, boolean printToShell)
	{
		String xmlOutput;
		XMLResult[] response;
		
		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"storage",
					"entryExit",
					"magazine",
					"offset",
					"barcode",
					"frameNumber",
					"tapeBayNumber",
					"drawerNumber",
					"slot",
					"number",
					"barcode",
					"barcodeValid"};

		String url = getPhysicalInventoryURL(partition);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		if(printToShell)
		{
			printOutput(response, "magazine", true);
		}

		return response;
	}

	public void printDebug(XMLResult[] response)
	{
		for(int i=0; i<response.length; i++)
		{
			System.out.println(i + ": level: " + response[i].docLevel 
					+ " path: " + response[i].headerTag 
					+ " value: " + response[i].value);
		}
	}

	public void printOutput(XMLResult[] response, String firstLine, boolean includeHeaders)
	{
		// Track the level in the doc of the previous element to determine
		// what to print. Start at 1 as this is the '0' value returned by
		// the xml parser.
		String[] headers = {"none"};
		String[] oldHeaders = {"none"};
		String[] tempHeaders;

		for(int i=0; i<response.length; i++)
		{
			// Print the headers and indents if desired.
			if(includeHeaders)
			{
				// Split out the headers.
				headers = response[i].headerTag.split(">");
				
				// Increment through the headers to print them.
				for(int j=0; j < headers.length; j++)
				{
					// Check to see if index exists in the old
					// header before comparing the values.
					// Also check header length as the final
					// header should be printed regardless.
					// If there are three items listed in a catagory
					// we'll want the header printed next to all
					// three and not just the first.
					if(j < oldHeaders.length && j < headers.length)
					{
						// Check to see if this header is the
						// same as the last one. If so, omit.
						if(!headers[j].equalsIgnoreCase(oldHeaders[j]))
						{
							// Values aren't the same. Follow
							// the same process printed in the
							// else statement from here.
						
							// Check to see if there is a value
							// on this line. Opening tags get built
							// into the header of the tag with a value.
							// They don't get their own. Closing tags
							// get a blank value. Parsing for this
							// blank value allows identification of
							// closing tags.
							if(response[i].value.length()>0)
							{
								// Response isn't blank.
								// Print indents
								// There is one indent per level
								// of the xml document.
								for(int k=0; k<j; k++)
								{
									System.out.print("\t");
								}
							
								// Print the header
								System.out.print(headers[j] + ":   ");
							
								// Print a new line character if this
								// is not the last header to print for
								// the value.
								if(j < headers.length-1)
								{
									System.out.print("\n");
								}
							}
							else
							{
								// If this is a closing array.
								// Delete the tag from the array
								// before saving it for comparison
								// on the next iteration.
								tempHeaders = new String[headers.length-1];
								for(int l=0; l < tempHeaders.length; l++)
								{
									tempHeaders[l] = headers[l];
								}
	
								headers = tempHeaders;
							}
						}
					}
					else // Just print the new header.
					{
						// Check to see if there is a value
						// on this line. Opening tags get built
						// into the header of the tag with a value.
						// They don't get their own. Closing tags
						// get a blank value. Parsing for this
						// blank value allows identification of
						// closing tags.
						if(response[i].value.length()>0)
						{
							// Response isn't blank.
							// Print indents
							// There is one indent per level
							// of the xml document.
							for(int k=0; k<j; k++)
							{
								System.out.print("\t");
							}
							
							// Print the header
							System.out.print(headers[j] + ":   ");

							// Print a new line character if this
							// isn't the last header to print for
							// the value.
							if(j < headers.length-1)
							{
								System.out.print("\n");
							}
						}
						else
						{
							// If this is a closing array.
							// Delete the tag from the array
							// before saving it for comparison
							// on the next iteration.
							tempHeaders = new String[headers.length-1];
							for(int l=0; l < tempHeaders.length; l++)
							{
								tempHeaders[l] = headers[l];
							}

							headers = tempHeaders;
						}
					}
				}
				
			}
			
			// Print the value if it exists.
			if(response[i].value.length()>0)
			{
				System.out.println(response[i].value);
			}

			// Store last value for comparison.
			oldHeaders = headers;
		}
		
		// Debug the input for testing.
		//printDebug(response);
	}
	
	//====================================================================
	// Internal Functions
	// 	Private functions used for the more complex query tasks
	// 	the code is capable of.
	//====================================================================


	private TeraPack[] filterEmptyFullEntryExit(TeraPack[] mags)
	{
		// Filter out empty, full, and entry exit terapacks.
		// The end result should only be partially full terapacks
		// in the Storage Partition.
		List<TeraPack> availableInventory = new ArrayList<>();
			
		for(int i=0; i<mags.length; i++)
		{
			if(mags[i].getCapacity()>0 && mags[i].getCapacity()<mags[i].getNumSlots() && mags[i].getLocation().equalsIgnoreCase("storage"))
			{
				availableInventory.add(mags[i]);
			}
		}
		
		// Convert list back into a TeraPack[] array to allow
		// for consistent information.
		TeraPack[] tempTeraPack = new TeraPack[availableInventory.size()];

		for(int i=0; i<availableInventory.size(); i++)
		{
			tempTeraPack[i] = availableInventory.get(i);
		}

		return tempTeraPack;

	}
	
	private String generateSlotString(String TeraPackOffset, int TapeSlot)
	{
		int librarySlot = (10 * Integer.valueOf(TeraPackOffset)) - (10 - TapeSlot) + 1;
		return Integer.toString(librarySlot);
	}

	private void moveTape(String partition, TeraPack[] mags, int maxMoves, boolean printToShell)
	{
		int source = 0; // Incrementor for source TP
		int destination = mags.length - 1; // Increment for destination TP
		int sourceTapes = mags[source].getCapacity(); // How many tapes are in the Source Magazine.
		int destSlots = mags[destination].getNumSlots() - mags[destination].getCapacity(); // How many slots are available in the destination TeraPack.

		if(source>=destination)
		{
			System.out.println("There are no moves to free up any TeraPacks.");
		}
		else if(sourceTapes > maxMoves)
		{
			System.out.println("There are " + sourceTapes + " tapes in this TeraPack and only " + maxMoves + " moves allowed with this operation. No TeraPacks will be freed.");
		}

		int tapeSlot = -1;
		int emptySlot = -1;
		int moves = 0;
		String sourceBarcode;
		String sourceSlotString;
		String destSlotString;

		// Move validation variables.
		int checkSlot = -1;
		String checkBarcode;
		String checkSlotString;
		boolean isValidMove = false;
	
		while((source < destination) && (moves < maxMoves))
		{
			tapeSlot = mags[source].getNextOccupiedSlot(tapeSlot);
			emptySlot = mags[destination].getNextEmptySlot(emptySlot);
		
			sourceBarcode = mags[source].getBarcodeAtPosition(tapeSlot);
			sourceSlotString = generateSlotString(mags[source].getOffset(), tapeSlot);
			destSlotString = generateSlotString(mags[destination].getOffset(), emptySlot);

			if(printToShell)
			{
				System.out.println("Move " + moves + ": " + sourceBarcode  + " at slot " + sourceSlotString + " moving to " + destSlotString);
			}
			// VALIDATION
			// The formula for the actual slot is a best-guess
			// we'll check to see if the barcode is in the source
			// slot, the destination slot is empty, and a barcode
			// in the same destination tp is in the slot we expect.

			checkSlot = mags[destination].getNextOccupiedSlot(checkSlot);
			if(checkSlot>=0)
			{
				checkBarcode = mags[destination].getBarcodeAtPosition(checkSlot);
				checkSlotString = generateSlotString(mags[destination].getOffset(), checkSlot);

				isValidMove = validateMove(partition, sourceSlotString, sourceBarcode, destSlotString, checkSlotString, checkBarcode, true);				
			}

			// Actually Perform the move
			if(isValidMove)
			{
				sendMove(partition, sourceSlotString, destSlotString);
			}
			else
			{
				System.out.println("Cannot verify slot information for move " + moves + ". Cancelling action.");
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

	}
	

	private int partition(TeraPack[] mags, int low, int high)
	{
		// Pivot
		int pivot = mags[high].getCapacity();

		// Index of smaller element and 
		// indicates the right position of the
		// pivot found so far
		int i = (low - 1);

		for(int j = low; j < high; j++)
		{
			// If the current element is smaller
			// than the pivot
			if(mags[j].getCapacity() < pivot)
			{
				i++;
				swapTeraPacks(mags, i, j);
			}
		}

		swapTeraPacks(mags, i+1, high);
		return (i + 1);
	}

	private TeraPack[] quickSort(TeraPack[] mags, int low, int high)
	{
		if(low < high)
		{
			int pi = partition(mags, low, high);

			// Separately sort elements before
			// partition and after partition
			quickSort(mags, low, pi - 1);
			quickSort(mags, pi + 1, high);
		}

		return mags;
	}

	private boolean sendMove(String partition, String sourceSlot, String destSlot)
	{
		// Send the move to the library.
		// Wait until the move is complete before exiting function.

		moveTape(partition, "SLOT", sourceSlot, "SLOT", destSlot, true);

		return true;	
	}

	private TeraPack[] sortMagazines(String partition, boolean printToShell)
	{
		// Gathering TeraPack from library.
		if(printToShell) 
		{ 
			System.out.println("Gathering TeraPack information from library..."); 
		}

		TeraPack[] magazines = magazineContents(partition, false);

		// Analyze TeraPacks
		if(printToShell)
		{
			System.out.println("Analyzing TeraPack Contents...");
		}
		// Remove the Empty and full TeraPacks if desired.
		magazines = filterEmptyFullEntryExit(magazines);

		/* Debug Code
		// Print before and after.
		for(int i=0; i<magazines.length; i++)
		{
			System.out.print(magazines[i].getCapacity() + " ");
		}

		System.out.print("\n");
		*/

		quickSort(magazines, 0, magazines.length-1);

		/* Debug Code
		for(int i=0; i<magazines.length; i++)
		{
			System.out.print(magazines[i].getCapacity() + " ");
		}

		System.out.print("\n");
		*/

		return magazines;
	}

	private void swapTeraPacks(TeraPack[] terapacks, int i, int j)
	{
		// Swaps the TeraPack at position i with the 
		// TeraPack at position j in the terapacks array.
		TeraPack temp;
		temp = terapacks[i];
		terapacks[i] = terapacks[j];
		terapacks[j] = temp;
	}

	private boolean validateMove(String partition, String sourceSlot, String sourceBarcode, String destSlot, String destSlot2, String destBarcode, boolean printToShell)
	{
		// The purpose of this function is to validate the source and destination slot
		// against the library inventory before initiating the move.
		// I'm guessing the formula for slot number is (10 * TeraPack Offset) - (10 - TeraPack[i].tape's array index) + 1.
		// I'm also assuming the TeraPack offsets will update along with the slot numbers if a 
		// TeraPack is exported or imported into the library.
		// As there is a lot of guessing.... there's a validateMove.
		

		// Sort the three slots into ascending order
		// Needed to compare against the order the results come in.
		List<String> slotOrder = new ArrayList<>();
		
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
		
		// Search for the slots in the library's inventory.
		boolean inSlot = false; // Parsing the correct slot
		boolean success = true; // Success default true. Any failed test results in false.
		int listIndex = 0; // The index for the linked list.
		String searchValue = "none";

		XMLResult[] response = listInventory(partition, false);

		
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
					}
					else if(slotOrder.get(listIndex).equals(destSlot))
					{
						if(printToShell)
						{
							System.out.print("Verifying destination...\t\t");
						}
						searchValue = "No";
					}
					else if(slotOrder.get(listIndex).equals(destSlot2))
					{	
						if(printToShell)
						{
							System.out.print("Verifying destination TeraPack...\t");
						}
						searchValue = destBarcode;
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

		return success;
	}
}


