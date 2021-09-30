//============================================================================
// XMLParser.java
// 	Description:
// 		This class uses a SAX parser to handle parsing the xml
// 		responses received from the connector class. 
//============================================================================

package com.socialvagrancy.spectraxml.utils;

import com.socialvagrancy.spectraxml.structures.XMLResult;

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

	public XMLResult[] parseXML(String[] searchValues)
	{
		XMLResult[] results = new XMLResult[1];
		results[0] = new XMLResult();

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
	XMLResult[] resultsArray = new XMLResult[1]; // Results to be returned.
	StringBuilder tempAnswer = new StringBuilder(); // Temp answer from parsing.
	boolean[] markupFound; // Array of terms if the value was found.
	int[] markupLevel; // For parsing multi-layered XML files.

	//=====================================================================
	// Constructor
	//=====================================================================

	public ResponseHandler(String[] terms)
	{
		// Add the list of headers we want returned.
		updateSearchTerms(terms);
		resultsArray[0] = new XMLResult();
	}

	//=====================================================================
	// Getters
	//=====================================================================

	public XMLResult[] getResults() { return resultsArray; }

	public void printResults()
	{
		for(int i=0; i<resultsArray.length; i++)
		{
			System.out.println(resultsArray[i].value);
		}
	}

	//=====================================================================
	// Functions
	//=====================================================================

	public void addResult(int xmlLevel, String result)
	{
		if(resultsArray.length==1 && resultsArray[0].headerTag.equals("none"))
		{
			// Set first array value.
			resultsArray[0].docLevel = xmlLevel;
			resultsArray[0].headerTag = buildHeaderString();
			resultsArray[0].value = result;
		}
		else
		{
			// Set the rest of the arrays.
			int newSize = resultsArray.length+1;
			XMLResult[] newArray = new XMLResult[newSize];
			for(int i=0; i<resultsArray.length; i++)
			{
				newArray[i] = resultsArray[i];
			}
			newArray[newSize-1] = new XMLResult();
			newArray[newSize-1].docLevel = xmlLevel;
			newArray[newSize-1].headerTag = buildHeaderString();
			newArray[newSize-1].value = result;

			resultsArray = newArray;
		}
	}

	public String buildHeaderString()
	{
		// Build a list of headers to explode.
		// Since we're parsing XML content, we'll use '>' as the
		// delimiter, as this character shouldn't appear in any
		// xml tags.
		StringBuilder tags = new StringBuilder();
		int tagCount = 0; // track whether this is the first tag or not.

		for(int i=0; i<markupFound.length; i++)
		{
			if(markupFound[i])
			{
				// This isn't the first tag.
				if(tagCount>0)
				{
					// Add a delimiter to the string.
					tags.append(">");
				}

				// Add the string.
				tags.append(searchTerms[i]);
				
				// Increment tagCounter so if there is another
				// tag we can add the delimiter.
				tagCount++;
			}
		}

		return tags.toString();
	}

	public int checkDepth(int index)
	{
		// This function calculates what level of the XML doc
		// the tag is in. Originally planned for a simple way to
		// handle multi-layered docs, but the logic ended up being
		// a lot more difficult than I expected.
	
		if(markupFound[index])
		{
			return 1 + checkDepth(index+1);
		}
		else
		{
			return 0;
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

	public int sumOpenTags()
	{
		// Calculates the total number of opened tags.
		int openTags = 0;
		for(int i=0; i<markupFound.length; i++)
		{
			openTags += markupFound[i] ? 1 : 0;
		}

		return openTags;
	}

	public void updateSearchTerms(String[] terms)
	{
		searchTerms = terms;
		markupFound = new boolean[searchTerms.length];
		markupLevel = new int[searchTerms.length];

		for(int i=0; i<markupFound.length; i++)
		{
			markupFound[i] = false;
			markupLevel[i] = 0;
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
		// Determine what level of the XML document we are in.
		int openTags = sumOpenTags();

		if(openTags>0)
		{
			// Sort through the markup looking for searchTerms.
			// Export an array of objectLevel in doc (openTags), headers, and the value. 	
			for(int i=0; i<markupFound.length; i++)
			{
				if(qName.equalsIgnoreCase(searchTerms[i]))
				{
					// Clean the input.
					// For some reason, when the item is multiple levels into the doc,
					// the 'value' is repeated for each level. This fix divides the
					// length of the string by the level in the doc, and only prints
					// the first N characters, where n is 1/Levelth of the string length.
					String response = tempAnswer.toString();
					int responseLength = response.length()/openTags;
					
					
					// Store all values even blank ones.
					// We'll use blank values to handle output
					// parsing
					addResult(openTags, response.substring(0, responseLength));
					tempAnswer.setLength(0);
					markupFound[i] = false;
				}
			}
		}

	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException
	{
		int xmlLevel = 0;

		for(int i=0; i<markupFound.length; i++)
		{
			if(markupFound[i])
			{
				tempAnswer.append(new String(ch, start, length));
			}
		}
	}


}
