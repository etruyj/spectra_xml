//============================================================================
// TeraPack.java
// 	Description:
// 		This is a container to hold all the information associated
// 		with a TeraPack.
// 		- Mag_barcode
// 		- Offset
// 		- Number of slots.
//		- Full/Empty
// 		- String[] tapes. 
//
//============================================================================

public class TeraPack
{
	String magazine_barcode;
	String offset;
	String location; // Storage or entryExit
	int number_slots;
	int capacity;
	String[] tapes;

	//===================================================================
	// Constructor
	//===================================================================
	
	public TeraPack(String type)
	{
		if(type.equalsIgnoreCase("LTO"))
		{
			// LTO TeraPacks hold 10 tapes.
			number_slots = 10;
		}
		else
		{
			// Enterprise TeraPacks hold 9 tapes.
			number_slots = 9;
		}

		magazine_barcode = "none";
		offset = "none";
		capacity = 0;
		location = "none";
		tapes = new String[number_slots];
		emptyTeraPack();
	}
	
	//===================================================================
	// Getters
	//===================================================================

	public int getCapacity() { return capacity; }
	public String getMagazineBarcode() { return magazine_barcode; }
	public int getNumSlots() { return number_slots; }
	public String getOffset() { return offset; }
	public String getLocation() { return location; }
	public String getTapeBarcode(int index) { return tapes[index]; }

	//===================================================================
	// Internal Functions
	//===================================================================

	private void addTape(String barcode)
	{
		// Search for a pending tape and overwrite with the barcode.
		for(int i=0; i<number_slots; i++)
		{
			if(tapes[i].equals("pending"))
			{
				tapes[i] = barcode;
			}
		}
	}

	private void emptyTeraPack()
	{
		for(int i=0; i<number_slots; i++)
		{
			tapes[i] = "none";
		}
	}

	private void markPending(int position)
	{
		tapes[position-1] = "pending";
	}

	//===================================================================
	// Public Functions
	//===================================================================

	public void calculateCapacity()
	{
		int occupied_slots = 0;
		
		for(int i=0; i<number_slots; i++)
		{
			if(!tapes[i].equals("none"))
			{
				occupied_slots++;
			}
		}
		capacity = occupied_slots;
	}

	public void importXMLResult(XMLResult result)
	{
		String[] headers = result.headerTag.split(">");
		
		// Switch based on the last header tag.
		switch(headers[headers.length-1])
		{
			case "offset":
				location = headers[0];
				offset = result.value;
				break;
			case "number":
				markPending(Integer.parseInt(result.value));
				break;
			case "barcode":
				if(result.value.length()>0)
				{
					if(result.headerTag.equalsIgnoreCase("storage>magazine>barcode>barcode") || result.headerTag.equalsIgnoreCase("entryExit>magazine>barcode>barcode"))
					{
						// This is a magazine barcode
						magazine_barcode = result.value;
					}
					else
					{
						addTape(result.value);
					}
				}
				break;
		}
	}
}
