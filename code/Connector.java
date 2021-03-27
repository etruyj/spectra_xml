//============================================================================
// Connector.java
//	Description:
//		This class handles the URL connection to websites. It'll be
//		responsible for properly formatting the http header based on
//		Spectra Logic's XML documention. All responses from the 
//		library will be returned in XML format.
//============================================================================

import java.net.URLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Connector
{
	URL tapeLibrary;
	URLConnection tapeConn;
	CookieJar cookies = new CookieJar();

	public String queryLibrary(String httpRequest)
	{
		String response = "EMPTY";
		
		try
		{
			tapeLibrary = new URL(httpRequest);

			tapeConn = tapeLibrary.openConnection();
			// Five second connection timeout.
			tapeConn.setConnectTimeout(5000);
			// Ten second read timeout
			tapeConn.setReadTimeout(10000);
			if(!cookies.getName(0).equals("none"))
			{
				// If a cookie exists, pass it.
				tapeConn.setRequestProperty("Cookie", cookies.getName(0) + "=" + cookies.getValue(0));
			}
			
			cookies.parseCookies(tapeConn.getHeaderFields());

			BufferedReader in = new BufferedReader(new InputStreamReader(tapeConn.getInputStream()));

			while((response = in.readLine()) != null)
			{
				System.out.println(response);
			}

			in.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return "Yeah baby";
	}
	
}
