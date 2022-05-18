//===================================================================
// PowerSupply.java
// 	Description:
// 		Contains the power supply information from the 
// 		libraryStatus.xml command to filter down the result.
//===================================================================

package com.socialvagrancy.spectraxml.structures.components;

import java.util.ArrayList;

public class PowerSupply
{
	public String id;
	public boolean faulted;
	public boolean input_power_ok;
	public boolean output_power_ok;
	public boolean temperature_warning;
	public boolean temperature_alarm;
	public String model_number;
	public String manufacturer_part_number;
	public String serial_number;
	public String mod_level;
	public String manufacturer;
	public String country_of_manufacturer;
	public int temperature_in_celcius;
	public boolean communicating_with_pcm;
	public ArrayList<PowerStatus> power_supplies;
	public ArrayList<Fan> fans;

	public PowerSupply()
	{
		power_supplies = new ArrayList<PowerStatus>();
		fans = new ArrayList<Fan>();
	}

}
