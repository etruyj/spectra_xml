//============================================================================
// XMLParser.java
// 	Description:
// 		This class uses a SAX parser to handle parsing the xml
// 		responses received from the connector class. 
//============================================================================

import java.lang.StringBuilder;
import java.io.StringReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser
{
	private String xmlString = "none";

	//=====================================================================
	// Setters
	//=====================================================================

	public void setXML(String xml) { xmlString = xml; }

	//=====================================================================
	// Funtions
	//=====================================================================

	public String[] parseXML(String[] searchValues)
	{
		String[] results = {"none"};
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
		
			ResponseHandler handler = new ResponseHandler(searchValues);
			saxParser.parse(new InputSource(new StringReader(xmlString)), handler);
			//handler.printResults();

			results = handler.getResults();
		}
		catch(Exception e)
		{
			System.out.println("ERROR: " + e);
		}

		return results;
	}
}

class ResponseHandler extends DefaultHandler
{
	String[] searchTerms; // Results we're looking for.
	String[] resultsArray = {"none"}; // Results to be returned.
	StringBuilder tempAnswer = new StringBuilder(); // Temp answer from parsing.
	boolean[] markupFound; // Array of terms if the value was found.

	//=====================================================================
	// Constructor
	//=====================================================================

	public ResponseHandler(String[] terms)
	{
		// Add the list of headers we want returned.
		updateSearchTerms(terms);
	}

	//=====================================================================
	// Getters
	//=====================================================================

	public String[] getResults() { return resultsArray; }

	public void printResults()
	{
		for(int i=0; i<resultsArray.length; i++)
		{
			System.out.println(resultsArray[i]);
		}
	}

	//=====================================================================
	// Functions
	//=====================================================================

	public void addResult(String nextResult)
	{
		if(resultsArray.length==1 && resultsArray[0].equals("none"))
		{
			resultsArray[0] = nextResult;
		}
		else
		{
			int newSize = resultsArray.length+1;
			String[] newArray = new String[newSize];
			for(int i=0; i<resultsArray.length; i++)
			{
				newArray[i] = resultsArray[i];
			}
			newArray[newSize-1] = nextResult;

			resultsArray = newArray;
		}
	}

	public void resetMarkupFound()
	{
		// Reset the array to false.
		for(int i=0; i<markupFound.length; i++)
		{
			markupFound[i] = false;
		}
	}

	public void updateSearchTerms(String[] terms)
	{
		searchTerms = terms;
		markupFound = new boolean[searchTerms.length];

		for(int i=0; i<markupFound.length; i++)
		{
			markupFound[i] = false;
		}
	}

	//=====================================================================
	// The key overrides for the handler.
	//=====================================================================

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		// Reset the temp variable.
		tempAnswer.setLength(0);

		// Iterate through the list of searchTerms.
		for(int i=0; i<searchTerms.length; i++)
		{
			if(qName.equalsIgnoreCase(searchTerms[i]))
			{
				markupFound[i] = true;
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		for(int i=0; i<markupFound.length; i++)
		{
			if(markupFound[i])
			{
				addResult(tempAnswer.toString());
				resetMarkupFound();
			}
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException
	{
		for(int i=0; i<markupFound.length; i++)
		{
			if(markupFound[i])
			{
				tempAnswer.append(new String(ch, start, length));
			}
		}
	}


}
