//============================================================================
// SpectraXML.java
// 	Description:
// 		This is the main class in the Spectra_XML Tape Library Manager. 
//============================================================================


public class SpectraXML
{
	public static void main(String[] args)
	{
		ArgParser aparser = new ArgParser();

		if(args.length>0)
		{
			aparser.parseArguments(args);	
		}

		if(aparser.checkValidInput())
		{
			SpectraController conn = new SpectraController(aparser.getIPAddress(), false);
	
			if(conn.login(aparser.getUsername(), aparser.getPassword()))
			{
				performCommand(conn, aparser.getCommand(), aparser.getCmdOption());
			}
		}
	}

	public static void performCommand(SpectraController conn, String command, String option)
	{
		switch(command)
		{
			case "list-partitions":
				conn.listPartitions();
				break;
		}
	}
}

