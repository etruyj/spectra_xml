//===================================================================
// Configuration.java
// 	(For use with the ui conifguration file)
// 	Description: Holds the configuration values
//===================================================================

package com.socialvagrancy.spectraxml.structures;

public class Configuration
{
	// Logging;
	public String log_path = "../logs/slxml-main.log";
	public String min_log = null;
	public int log_level = 1;
	public int log_count = 3;
	public int log_size = 10240;

	public int getLogLevel()
	{
		if(min_log != null)
		{
			switch(min_log)
			{
				case "NONE":
					log_level = 0;
					break;
				case "INFO":
					log_level = 1;
					break;
				case "WARN":
					log_level = 2;
					break;
				case "ERROR":
					log_level = 3;
					break;
				default:
					log_level = 1;
					break;
			}
		}

		return log_level;
	}

}
