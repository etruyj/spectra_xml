//===================================================================
//	FindPowerSupplyInfo.java
//		Description:
//			Parses the libraryStatus.xml output to 
//			load the PowerSupply java variable for further
//			processing.
//===================================================================

package com.socialvagrancy.spectraxml.filter;

import com.socialvagrancy.spectraxml.structures.components.Fan;
import com.socialvagrancy.spectraxml.structures.components.PowerSupply;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class FindPowerSupplyInfo
{
	public static ArrayList<PowerSupply> fromLibraryStatusXML(XMLResult[] status)
	{
		ArrayList<PowerSupply> powersupply_list = buildSupplyList(status);

		return powersupply_list;	
	}

	//=======================================
	// Private Functions
	//=======================================

	private static ArrayList<PowerSupply> buildSupplyList(XMLResult[] status)
	{
		ArrayList<PowerSupply> powersupply_list = new ArrayList<PowerSupply>();
		PowerSupply ps = null;
		Fan fan;
		boolean searching = true;
		int itr = 0;

		while(itr < status.length && searching)
		{
			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU"))
			{
				if(ps != null)
				{
					powersupply_list.add(ps);
				}

				ps = new PowerSupply();
			}
			
			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>ID"))
			{
				ps.id = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>inputPowerOkay"))
			{
				if(status[itr].value.trim().equalsIgnoreCase("yes"))
				{
					ps.input_power_okay = true;
				}
				else
				{
					ps.input_power_okay = false;
				}
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>outputPowerOkay"))
			{
				if(status[itr].value.trim().equalsIgnoreCase("yes"))
				{
					ps.output_power_okay = true;
				}
				else
				{
					ps.output_power_okay = false;
				}
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>temperatureWarning"))
			{
				if(status[itr].value.trim().equalsIgnoreCase("yes"))
				{
					ps.temperature_warning = true;
				}
				else
				{
					ps.temperature_warning = false;
				}
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>temperatureAlarm"))
			{
				if(status[itr].value.trim().equalsIgnoreCase("yes"))
				{
					ps.temperature_alarm = true;
				}
				else
				{
					ps.temperature_alarm = false;
				}
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>modelNumber"))
			{
				ps.model_number = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>serialNumber"))
			{
				ps.serial_number = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>modLevel"))
			{
				ps.mod_level = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>manufacturer"))
			{
				ps.manufactuer = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>countryOfManufacturer"))
			{
				ps.country_of_manufacturer = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>temperatureInCelsius"))
			{
				ps.temperature_in_celcius = status[itr].vaue.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase(""))
			{
			}

			if(status[itr].headerTag.equalsIgnoreCase(""))
			{
			}

			if(status[itr].headerTag.equalsIgnoreCase(""))
			{
			}
			itr++;
		}
		
		return powersupply_list;
	}
}
