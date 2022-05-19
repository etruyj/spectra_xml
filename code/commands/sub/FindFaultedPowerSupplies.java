//===================================================================
// FindFaultedPowerSupplies.java
// 	Description:
// 		Queries the libraryStatus.xml to determine which
// 		power supplies have a faulted status and to identify
// 		the reason the power supply is in a faulted status.
//===================================================================

package com.socialvagrancy.spectraxml.commands.sub;

import com.socialvagrancy.spectraxml.commands.filter.FindPowerSupplyInfo;
import com.socialvagrancy.spectraxml.structures.components.PowerSupply;
import com.socialvagrancy.spectraxml.structures.XMLResult;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class FindFaultedPowerSupplies
{
	public static XMLResult[] asXMLResult(XMLResult[] status, Logger log)
	{
		ArrayList<String> powersupply_list = fromLibraryStatus(status, log);

		return convertToXMLResult(powersupply_list);
	}

	public static XMLResult[] convertToXMLResult(ArrayList<String> messages)
	{
		XMLResult[] results = new XMLResult[messages.size()];

		for(int i=0; i<messages.size(); i++)
		{
			results[i] = new XMLResult();
			results[i].headerTag = "Message";
			results[i].value = messages.get(i);
		}

		return results;
	}
	
	public static ArrayList<String> fromLibraryStatus(XMLResult[] status, Logger log)
	{
		ArrayList<PowerSupply> powersupply_list = FindPowerSupplyInfo.fromLibraryStatusXML(status);

		log.INFO("Found (" + powersupply_list.size() + ") power supplies.");

		ArrayList<String> fault_messages = new ArrayList<String>();
		String fault_msg;
		int faulted_supply_counter = 0;

		for(int i=0; i<powersupply_list.size(); i++)
		{
			if(powersupply_list.get(i).faulted)
			{
				faulted_supply_counter++;

				fault_msg = powersupply_list.get(i).id;
				
				if(powersupply_list.get(i).temperature_warning)
				{
					fault_msg += " has temperature warning with current temp at " 
						+ powersupply_list.get(i).temperature_in_celcius 
						+ " C";
				}
				else if(powersupply_list.get(i).temperature_alarm)
				{
					fault_msg += " has temperature alarm with current temp at " 
						+ powersupply_list.get(i).temperature_in_celcius 
						+ " C";
				}
				else if(!powersupply_list.get(i).communicating_with_pcm)
				{
					fault_msg += " is not communicating with PCM";
				}

				for(int j=0; j<powersupply_list.get(i).fans.size(); j++)
				{
					if(!powersupply_list.get(i).fans.get(j).okay)
					{
						fault_msg += " fan " + powersupply_list.get(i).fans.get(j).number + " needs checked.";
					}
				}
				
				for(int j=0; j<powersupply_list.get(i).power_supplies.size(); j++)
				{
					fault_msg += "\n\t Actual vs Nominal Voltage: " 
						+ powersupply_list.get(i).power_supplies.get(j).actual_voltage
						+ "/"
						+ powersupply_list.get(i).power_supplies.get(j).nominal_voltage
						+ "\n\t Current (A): "
						+ powersupply_list.get(i).power_supplies.get(j).actual_current_in_amps;
				}

				fault_messages.add(fault_msg);
			}
		}

		if(fault_messages.size() == 0)
		{
			fault_msg = "No power supply faults detected.";
			fault_messages.add(fault_msg);
			log.INFO(fault_msg);
		}
		else
		{
			log.WARN("Identified (" + faulted_supply_counter + ") faulted power supplies.");
		}
		
		return fault_messages;
	}

}

