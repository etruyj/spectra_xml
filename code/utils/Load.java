//===================================================================
//	Load.java
//		Decription: Loads configuration and library profile
//		files.
//===================================================================

package com.socialvagrancy.spectraxml.utils;

import com.socialvagrancy.spectraxml.structures.Configuration;

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

			}
			catch(IOException e)
			{
				System.err.println(e.getMessage());
				
			}
		}

		return config;
	}
}

