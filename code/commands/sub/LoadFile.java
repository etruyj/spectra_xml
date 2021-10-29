package com.socialvagrancy.spectraxml.commands.sub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class LoadFile
{
	public static ArrayList<String> tapeList(String filename)
	{
		ArrayList<String> tape_list = new ArrayList<String>();
		try
		{
			File ifile = new File(filename);

			BufferedReader br = new BufferedReader(new FileReader(ifile));

			String line = null;

			while((line = br.readLine()) != null)
			{
				tape_list.add(line);
			}
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}

		return tape_list;
	}
}
