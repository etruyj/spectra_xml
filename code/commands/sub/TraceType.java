//===================================================================
// TraceType.java
// 	Description:
//		This is a breakout of the traceType XML command. The
//		options vary based the option specified and the amount
//		of space required to code this function properly pushed
//		it into it's own class.
//	
//	Functions:
//		-- formatOption(String option) : converts the option to
//			the required case-sensitive value.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

public class TraceType
{
	public String formatOption(String option)
	{
		switch(option)
		{
			case "ACTION":
			case "Action":
			case "action":
				option = "Action";
				break;
			case "AUTODRIVECLEAN":
			case "AutoDriveClean":
			case "Autodriveclean":
			case "autodriveclean":
				option = "AutoDriveClean";
				break;
			case "AUTOSUPPORT":
			case "AutoSupport":
			case "Autosupport":
			case "autosupport":
				option = "AutoSupport";
				break;
			case "BACKGROUNDCLIENT":
			case "BackgroundClient":
			case "Backgroundclient":
			case "backgroundclient":
				option = "BackgroundClient";
				break;
			default:
				option = "invalid";
				break;
		}

		return option;
	}

	public String getResponseType(String option)
	{
		String responseType;
		
		switch(option)
		{
			case "Action":
			case "AutoDriveClean":
			case "AutoSupport":
			case "BackgroundClient":
				responseType = "download";
				break;
			default:
				responseType = "none";
				break;
		}
		
		return responseType;
	}
}
