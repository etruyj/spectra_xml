//===================================================================
//	FindPowerSupplyInfo.java
//		Description:
//			Parses the libraryStatus.xml output to 
//			load the PowerSupply java variable for further
//			processing.
//===================================================================

package com.socialvagrancy.spectraxml.commands.filter;

import com.socialvagrancy.spectraxml.structures.components.Fan;
import com.socialvagrancy.spectraxml.structures.components.PowerStatus;
import com.socialvagrancy.spectraxml.structures.components.PowerSupply;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class FindPowerSupplyInfo
{
	public static ArrayList<PowerSupply> fromLibraryStatusXML(XMLResult[] status)
	{
		ArrayList<PowerSupply> powersupply_list = buildSupplyList(status);
		powersupply_list = addFaultStatus(powersupply_list, status);

		return powersupply_list;	
	}

	//=======================================
	// Private Functions
	//=======================================

	public static ArrayList<PowerSupply> addFaultStatus(ArrayList<PowerSupply> powersupply_list, XMLResult[] status)
	{
		HashMap<String, Boolean> ps_fault_map = mapPowerSupplyFaults(status);

		for(int i=0; i<powersupply_list.size(); i++)
		{
			powersupply_list.get(i).faulted = ps_fault_map.get(powersupply_list.get(i).id);
		}

		return powersupply_list;
	}

	private static ArrayList<PowerSupply> buildSupplyList(XMLResult[] status)
	{
		ArrayList<PowerSupply> powersupply_list = new ArrayList<PowerSupply>();
		PowerSupply ps = null;
		PowerStatus volt_info = null;
		Fan fan = null;
		boolean searching = true;
		int itr = 0;

		while(itr < status.length && searching)
		{
			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>ID"))
			{
				if(ps != null)
				{
					powersupply_list.add(ps);
				}

				ps = new PowerSupply();
			
				ps.id = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>inputPowerOkay"))
			{
				if(status[itr].value.trim().equalsIgnoreCase("yes"))
				{
					ps.input_power_ok = true;
				}
				else
				{
					ps.input_power_ok = false;
				}
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>outputPowerOkay"))
			{
				if(status[itr].value.trim().equalsIgnoreCase("yes"))
				{
					ps.output_power_ok = true;
				}
				else
				{
					ps.output_power_ok = false;
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
				ps.manufacturer = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>countryOfManufacturer"))
			{
				ps.country_of_manufacturer = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>temperatureInCelsius"))
			{
				ps.temperature_in_celcius = Integer.valueOf(status[itr].value.trim());
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>communicatingWithPCM"))
			{
				if(status[itr].value.trim().equalsIgnoreCase("yes"))
				{
					ps.communicating_with_pcm = true;
				}
				else
				{
					ps.communicating_with_pcm = false;
				}
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>fanInPowerSupplyFRU>number"))
			{
				fan = new Fan();
				fan.number = status[itr].value.trim();
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>fanInPowerSupplyFRU>okay"))
			{
				if(status[itr].value.trim().equalsIgnoreCase("okay"))
				{
					fan.okay = true;
				}
				else
				{
					fan.okay = false;
				}

				ps.fans.add(fan);
				
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>powerSupplyInPowerSupplyFRU>nominalVoltage"))
			{
				volt_info = new PowerStatus();
				volt_info.nominal_voltage = Float.valueOf(status[itr].value.trim());
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>powerSupplyInPowerSupplyFRU>actualVoltage"))
			{
				volt_info.actual_voltage = Float.valueOf(status[itr].value.trim());
			}

			if(status[itr].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerSupplyFRU>powerSupplyInPowerSupplyFRU>actualCurrentInAmps"))
			{
				volt_info.actual_current_in_amps = Float.valueOf(status[itr].value.trim());
				ps.power_supplies.add(volt_info);
			}
			
			itr++;
		}
		
		return powersupply_list;
	}

	public static HashMap<String, Boolean> mapPowerSupplyFaults(XMLResult[] status)
	{
		HashMap<String, Boolean> ps_fault_map = new HashMap<String, Boolean>();
		String frame = "";
		String power_supply = "";
		boolean is_faulted = false;

		for(int i=0; i<status.length; i++)
		{
			if(status[i].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerControlModule>ID"))
			{
				frame = status[i].value.trim();
			}

			if(status[i].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerControlModule>powerSupplyInPCM>position"))
			{
				power_supply = frame + "/PowerSupply" + status[i].value.trim();
			}

			if(status[i].headerTag.equalsIgnoreCase("controllerEnvironmentInfo>powerControlModule>powerSupplyInPCM>faulted"))
			{
				if(status[i].value.trim().equalsIgnoreCase("yes"))
				{
					is_faulted = true;
				}
				else
				{
					is_faulted = false;
				}

				ps_fault_map.put(power_supply, is_faulted);
			}
		}

		return ps_fault_map;
	}

	public static HashMap<String, Integer> mapPowerSupplyPositions(ArrayList<PowerSupply> powersupply_list)
	{
		HashMap<String, Integer> ps_index_map = new HashMap<String, Integer>();
		
		for(int i=0; i < powersupply_list.size(); i++)
		{
			ps_index_map.put(powersupply_list.get(i).id, i);
		}

		return ps_index_map;
	}
}
