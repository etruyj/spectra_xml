//============================================================================
// Connector.java
//	Description:
//		This class handles the URL connection to websites. It'll be
//		responsible for properly formatting the http header based on
//		Spectra Logic's XML documention. All responses from the 
//		library will be returned in XML format.
//============================================================================

import java.lang.StringBuilder;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
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
		StringBuilder output = new StringBuilder();

		try
		{
			tapeLibrary = new URL(httpRequest);

			tapeConn = tapeLibrary.openConnection();
			// Five second connection timeout.
			tapeConn.setConnectTimeout(5000);
			// Ten second read timeout
			tapeConn.setReadTimeout(20000);
			if(!cookies.getName(0).equals("none"))
			{
				// If a cookie exists, pass it.
				tapeConn.setRequestProperty("Cookie", cookies.getName(0) + "=" + cookies.getValue(0));
			}
			
			cookies.parseCookies(tapeConn.getHeaderFields());

			BufferedReader in = new BufferedReader(new InputStreamReader(tapeConn.getInputStream()));

			while((response = in.readLine()) != null)
			{
				//System.out.println(response);
				output.append(response);
			}

			in.close();
		}
		catch(Exception e)
		{
			// For some reason we're throwing a 
			// java.net.socketexception: network is down
			// I'm not sure what this means or what to 
			// do to correct it. It doesn't seem to have
			// any effect on the program itself.
			// My other URLConnection programs don't have this
			// issue, but I am querying a library behind a firewall
			// so something may be filtering the TCP connection.
			// I'm just ignoring it for now instead of deleting
			// the println(e).
			if(!e.toString().equals("java.net.SocketException: Network is down"))
			{
				System.out.println(e);
			}	
		}

		return output.toString();
	}

	public void downloadFromLibrary(String httpRequest, String path, String filename)
	{
		try
		{
			URL tapeLibrary = new URL(httpRequest);
			tapeConn = tapeLibrary.openConnection();

			if(!cookies.getName(0).equals("none"))
			{
				// If a cookie exists, pass it.
				tapeConn.setRequestProperty("Cookie", cookies.getName(0) + "=" + cookies.getValue(0));
			}

			ReadableByteChannel readChannel = Channels.newChannel(tapeConn.getInputStream());
			FileOutputStream outFile = new FileOutputStream(path+filename);
			FileChannel writeChannel = outFile.getChannel();

			writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);

		}
		catch(IOException e)
		{
			System.out.println(e);
		}
	}
	
}
