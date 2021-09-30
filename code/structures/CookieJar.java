//============================================================================
// CookieJar.java
// 	Description:
// 		A container class for cookies that handles parsing all the
// 		cookie data from the website. 
//============================================================================

package com.socialvagrancy.spectraxml.structures;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CookieJar
{
	Cookie[] sleeve = new Cookie[1];
	
	public CookieJar()
	{
		sleeve[0] = new Cookie();
	}

	//=====================================================================
	// Getters
	//=====================================================================

	public String getName(int cookie) { return sleeve[cookie].name; }
	public String getValue(int cookie) { return sleeve[cookie].value; }
	public String getDomain(int cookie) { return sleeve[cookie].domain; }
	public String getPath(int cookie) { return sleeve[cookie].path; }
	public String getExpires(int cookie) { return sleeve[cookie].expires; }
		

	//=====================================================================
	// Functions
	//=====================================================================

	public void addCookie(Cookie thinMint)
	{
		if(sleeve.length==1 && sleeve[0].name.equals("none"))
		{
			sleeve[0].name = thinMint.name;
			sleeve[0].value = thinMint.value;
			sleeve[0].domain = thinMint.domain;
			sleeve[0].path = thinMint.path;
			sleeve[0].expires = thinMint.expires;
		}
		else
		{
			// Expand the array.
			int newSize = sleeve.length + 1;
			Cookie[] newSleeve = new Cookie[newSize];
			
			// Copy over old values

			for(int i=0; i<sleeve.length; i++)
			{
				newSleeve[i] = new Cookie();
				newSleeve[i].name = sleeve[i].name;
				newSleeve[i].value = sleeve[i].value;
				newSleeve[i].domain = sleeve[i].domain;
				newSleeve[i].path = sleeve[i].path;
				newSleeve[i].expires = sleeve[i].expires;
			}
			
			int lastIndex = newSize-1;
			// Add the newest cookie to the sleeve.
			newSleeve[lastIndex] = new Cookie();
			newSleeve[lastIndex].name = thinMint.name;
			newSleeve[lastIndex].value = thinMint.value;
			newSleeve[lastIndex].domain = thinMint.domain;
			newSleeve[lastIndex].path = thinMint.path;
			newSleeve[lastIndex].expires = thinMint.expires;
			
			// Replace the old sleeve
			sleeve = newSleeve;
		}
	}

	public void parseCookies(Map<String, List<String>> headerFields)
	{
		Set<String> headerFieldSet = headerFields.keySet();
		Iterator<String> headerFieldsIter = headerFieldSet.iterator();
		String[] fields;
		Cookie thinMint = new Cookie();
		String[] keypair;
					
		while(headerFieldsIter.hasNext())
		{
			String headerFieldKey = headerFieldsIter.next();
			
			if("Set-Cookie".equalsIgnoreCase(headerFieldKey))
			{
				List<String> headerFieldValue = headerFields.get(headerFieldKey);

				for(String headerValue : headerFieldValue)
				{
					// Split the fields into lines.
					fields = headerValue.split(";");
					
					for(int j=0; j<fields.length; j++)
					{
						// Clear excess whitespace
						fields[j] = fields[j].trim();
						
						// Break appart vairable assignments.
						keypair = fields[j].split("=");
						
						
						// Assign the cookie value.
						// The name of the cookie is the variable so, we must handle index 0 differently than the rest.
						if(j==0)
						{
							thinMint.name=keypair[0];
							thinMint.value=keypair[1];
						}
						else
						{
							// The rest of the field values are handled normally.
							if("domain".equalsIgnoreCase(keypair[0]))
							{
								thinMint.domain = keypair[1];
							}
							else if("path".equalsIgnoreCase(keypair[0]))
							{
								thinMint.path = keypair[1];
							}
							else if("expires".equalsIgnoreCase(keypair[0]))
							{
								thinMint.expires = keypair[1];
							}
						}

					}
				}
			
				// Double check that we have a cookie.
				if(!thinMint.name.equals("none"))
				{
					addCookie(thinMint);
				}
			}
		}
	}
}
