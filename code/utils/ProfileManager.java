//===================================================================
// ProfileManager.java
// 	Description: Handles console inputs for creating a profile.
//===================================================================

package com.socialvagrancy.spectraxml.utils;

import com.socialvagrancy.spectraxml.structures.LibraryProfile;
import java.io.Console;
import java.util.Scanner;

public class ProfileManager
{
	public static void createProfile(String file_path)
	{
		LibraryProfile profile = new LibraryProfile();
		
		profile.name = getSingleWordInput("Library name:");
		profile.url = getSingleWordInput("Library address:");
		profile.username = getSingleWordInput("Username:");
		profile.password_required = getYesNoInput("Password required [y/n]:");
		profile.http_connection = getYesNoInput("HTTP connection [y/n]:");
		
		if(!profile.http_connection)
		{
			profile.ignore_ssl_certificate = getYesNoInput("Ingore SSL certificate [y/n]:");
		}

		// Test to see if the profile already exists.
		// Can't create what already exists.
		LibraryProfile test = Load.profile(profile.name, file_path);

		if(test == null)
		{
			System.err.println("Warning: Profile " + profile.name + " already exists.");
			System.err.println("Pleasue use update-profile to edit this profile.");
		}
		else
		{
			if(Save.profile(profile, file_path))
			{
				System.err.println("Profile created successfully.");
			}
			else
			{
				System.err.println("Unable to create profile.");
			}
		
		}
	}

	public static void deleteProfile(String file_path)
	{
		String name = getSingleWordInput("Library name:");

		if(Delete.profile(name, file_path))
		{
			System.err.println("Profile deleted successfully.");
		}
		else
		{
			System.err.println("Unable to delete profile.");
		}
	}

	public static String getPassword()
	{
		Console con = System.console();
		StringBuilder password = new StringBuilder();
		System.err.print("Enter password: ");
		char[] passwd = con.readPassword();
		
		for(int i=0; i<passwd.length; i++)
		{
			password.append(passwd[i]);
		}		

		return password.toString();
	}

	public static void updateProfile(String file_path)
	{
		LibraryProfile profile = new LibraryProfile();
		
		profile.name = getSingleWordInput("Library name:");
		profile.url = getSingleWordInput("Library address:");
		profile.username = getSingleWordInput("Username:");
		profile.password_required = getYesNoInput("Password required [y/n]:");
		profile.http_connection = getYesNoInput("HTTP connection [y/n]:");
		
		if(!profile.http_connection)
		{
			profile.ignore_ssl_certificate = getYesNoInput("Ingore SSL certificate [y/n]:");
		}

		if(Save.profile(profile, file_path))
		{
			System.err.println("Profile updated successfully.");
		}
		else
		{
			System.err.println("Unable to update profile.");
		}
	}

	private static String getSingleWordInput(String prompt)
	{
		Scanner console = new Scanner(System.in);
		String input;
		String[] test;

		System.err.print(prompt + " ");
		input = console.nextLine();

		// Test for valid input.
		test = input.split(" ");

		if(test.length != 1)
		{
			System.err.println("Invalid input: please don't use spaces.");

			input = getSingleWordInput(prompt);
		}

		return input;
	}

	private static boolean getYesNoInput(String prompt)
	{
		Scanner console = new Scanner(System.in);
		String input;
		String[] test;

		System.err.print(prompt + " ");
		input = console.nextLine();
		
		// Clean input
		switch(input)
		{
			case "yes":
			case "YES":
			case "y":
			case "Y":
				return true;
			case "no":
			case "NO":
			case "n":
			case "N":
				return false;
			default:
				System.err.println("Invalid input: please select [y/n]");
				return getYesNoInput(prompt);
		}
	}
}
