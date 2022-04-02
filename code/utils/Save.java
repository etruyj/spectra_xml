//===================================================================

package com.socialvagrancy.spectraxml.utils;

import com.socialvagrancy.spectraxml.structures.LibraryProfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.StringBuilder;

public class Save
{
	public static boolean profile(LibraryProfile profile, String file_path)
	{
		File file = new File(file_path);
		String line = null;
		String[] value_pair;
		String input_line = "[" + profile.name + "]";
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

						// To simply output, all the lines will be appended here.
						if(credentials_updated)
						{
							file_contents.append(addProfileString(profile));
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
		else
		{
			// The profile doesn't exist and must be created.
			try
			{
				file.createNewFile();
			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
				return false;
			}
		}

		// Add the profile to the contents if it doesn't exist.
		if(!credentials_updated)
		{
			file_contents.append(addProfileString(profile));
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

	private static String addProfileString(LibraryProfile profile)
	{
		StringBuilder file_contents = new StringBuilder();
		
		file_contents.append("[" + profile.name + "]" + "\n");
		file_contents.append("url=" + profile.url + "\n");
		file_contents.append("username=" + profile.username + "\n");
						
		if(profile.password_required)
		{
			file_contents.append("password_required=yes" + "\n");
		}
		else
		{
			file_contents.append("password_required=no" + "\n");
		}
							
		if(profile.http_connection)
		{
			file_contents.append("http_connection=yes" + "\n");
		}
		else
		{
			file_contents.append("http_connection=no" + "\n");
		}
						
		// Check to see if the connection wasn't changed
		// to https.
		// If so, add the ignore_ssl_certificate field.
		if(!profile.http_connection)
		{
			if(profile.ignore_ssl_certificate)
			{
				file_contents.append("ignore_ssl_cert=yes" + "\n");
			}
			else
			{
				file_contents.append("ignore_ssl_cert=no" + "\n");
			}	
		}

		return file_contents.toString();
	}
}
