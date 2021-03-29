//============================================================================
// SpectraController.java
// 	Description:
// 		This class makes all the appropriate function calls to the tape
// 		library. 
//============================================================================

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
	// Get Functions
	//====================================================================
	
	public String getLoginURL(String user, String password)
	{
		return libraryAddress + "login.xml?username=" + user 
			+ "&password=" + password;
	}

	public String getPartitionListURL()
	{
		return libraryAddress + "partition.xml?action=list";
	}

	//====================================================================
	// Control Functions
	//====================================================================
	
	public boolean login(String user, String password)
	{
		String xmlOutput;
		String[] response;

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"status"};

		String url = getLoginURL(user, password);
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);
	
		if(response[0].equalsIgnoreCase("OK"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void listPartitions()
	{
		String xmlOutput;
		String[] response = {"No results"};

		XMLParser xmlparser = new XMLParser();
		String[] searchTerms = {"name"};

		String url = getPartitionListURL();
		xmlOutput = cxn.queryLibrary(url);

		xmlparser.setXML(xmlOutput);
		response = xmlparser.parseXML(searchTerms);

		printOutputWithoutHeaders(response);
	}

	public void printOutputWithoutHeaders(String[] response)
	{
		for(int i=0; i<response.length; i++)
		{
			System.out.println(response[i]);
		}
	}
}
