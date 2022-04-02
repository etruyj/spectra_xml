//===================================================================

package com.socialvagrancy.spectraxml.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.StringBuilder;

public class Delete
{
	public static boolean profile(String profile_name, String file_path)
	{
		File file = new File(file_path);
		String input_line = "[" + profile_name + "]";
		String line = null;
		String[] value_pair;
		StringBuilder file_contents = new StringBuilder();
		boolean updating_credentials = false;
		boolean credentials_updated = false;
		int line_counter = 0;
		
		// If the file exists, import file.
		if(file.exists())
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(file));

				while((line = br.readLine()) != null)
				{
					// If the profile already exists, overwrire the 
					// values with new fields.
					if(line.equals(input_line))
					{
						updating_credentials = true;
					}
					
					if(updating_credentials)
					{
						value_pair = line.split("=");

						// Test to see if this is an http connection
						// if so, this will be the last line of the profile to load.
						if(value_pair[0].trim().equals("http_connection") && value_pair[1].trim().equals("yes"))
						{

							// Mark complete
							updating_credentials=false;
							credentials_updated=true;

						}		

						if(value_pair[0].trim().equals("ignore_ssl_cert"))
						{
							updating_credentials=false;
							credentials_updated=true;
						}

					}
					else
					{
						// Add the line to the contents.
						file_contents.append(line + "\n");
					}
				}

				br.close();
			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
				return false;
			}
		}
		
		try
		{
			// Save the file.
			FileOutputStream outFile = new FileOutputStream(file_path);
			outFile.write(file_contents.toString().getBytes());
			outFile.close();
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
			return false;
		}
		
		return true;
	}
}
