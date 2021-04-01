//============================================================================
// XMLResult.java
// 	Description:
// 		This is a collection of results from the XML document.
// 		We're storing 3 pieces of information for parsing:
// 		- level of in the document.
// 		- the tag associated with the value.
// 		- the string value of the tag.  
//============================================================================

public class XMLResult
{
	public int docLevel;
	public String headerTag;
	public String value;

	public XMLResult()
	{
		docLevel = 0;
		headerTag = "none";
		value = "no results";
	}
}
