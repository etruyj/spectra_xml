//===================================================================
//	Load.java
//		Decription: Loads configuration and library profile
//		files.
//===================================================================

package com.socialvagrancy.spectraxml.utils;

import com.socialvagrancy.spectraxml.structures.Configuration;
import com.socialvagrancy.spectraxml.structures.LibraryProfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Load
{
	public static Configuration config(String file_path)
	{
		Configuration config = new Configuration();

		File file = new File(file_path);

		if(file.exists())
		{
			String line = null;
			String[] param;

			try
			{
				BufferedReader br = new BufferedReader(new FileReader(file));

				while((line = br.readLine()) != null)
				{
					param = line.split(":");

					switch(param[0])
					{
						case "log_count":
							config.log_count = Integer.valueOf(param[1].trim());
							break;
						case "log_level":
							config.log_level = Integer.valueOf(param[1].trim());
							break;
						case "log_path":
							config.log_path = param[1].trim();
							break;
						case "log_size":
							config.log_size = Integer.valueOf(param[1].trim());
							break;
						case "min_log":
							config.min_log = param[1].trim();
							break;
					}
				}

				br.close();
			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
				
			}
		}

		return config;
	}

	public static LibraryProfile profile(String profile_name, String file_path)
	{
		LibraryProfile profile = new LibraryProfile();
		File file = new File(file_path);
		boolean profile_loaded = false;
		boolean profile_found = false;
		String line = null;
		String[] value_pair;

		if(file.exists())
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(file));

				while((line = br.readLine()) != null && !profile_loaded)
				{
					if(line.substring(0,1).equals("["))
					{
						if(line.equals("[" + profile_name + "]"))
						{
							profile_found = true;
							profile.name = profile_name;
						}
					}

					if(profile_found)
					{
						value_pair = line.split("=");
						switch(value_pair[0].trim())
						{
							case "url":
								profile.url = value_pair[1].trim();
								break;
							case "username":
								profile.username = value_pair[1].trim();
								break;
							case "password_required":
								if(value_pair[1].trim().equals("yes"))
								{
									profile.password_required = true;
								}
								else
								{
									profile.password_required = false;
								}
								break;
							case "http_connection":
								if(value_pair[1].trim().equals("yes"))
								{
									profile.http_connection = true;

									// If their isn't an TSL/SSL (https) 
									// connection, this will be the last
									// field in the config, so the profile
									// is loaded and we can exit the loop.
									profile_loaded = true;
								}
								else
								{
									profile.http_connection = false;
								}
								break;
							case "ignore_ssl_cert":
								if(value_pair[1].trim().equals("yes"))
								{
									profile.ignore_ssl_certificate = true;
								}
								else
								{
									profile.ignore_ssl_certificate = false;
								}

								//profile is loaded. Exit the loop.
								profile_loaded=true;
								break;
						}
					}
				}

				br.close();
			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
			}	
		}
		else
		{
			System.err.println("Profile file " + file_path + " does not exist.");
		}

		return profile;
	}
}

