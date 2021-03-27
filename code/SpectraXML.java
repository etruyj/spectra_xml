//============================================================================
// SpectraXML.java
// 	Description:
// 		This is the main class in the Spectra_XML Tape Library Manager. 
//============================================================================


public class SpectraXML
{
	public static void main(String[] args)
	{
		Controller conn = new Controller("10.85.41.7", false);
	
		System.out.println(args[0]);

		conn.login("su", "");
		conn.listPartitions();
	}

}

