//============================================================================
// Connector.java
//	Description:
//		This class handles the URL connection to websites. It'll be
//		responsible for properly formatting the http header based on
//		Spectra Logic's XML documention. All responses from the 
//		library will be returned in XML format.
//============================================================================

package com.socialvagrancy.spectraxml.utils;

import com.socialvagrancy.spectraxml.structures.CookieJar;
import com.socialvagrancy.spectraxml.structures.XMLResult;

import com.socialvagrancy.utils.Logger;

import java.lang.StringBuilder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class Connector
{
	URL tapeLibrary;
	HttpURLConnection tapeConn;
	CookieJar cookies;
	Logger log;

	public Connector(boolean ignoreSSL, Logger logs)
	{
		cookies = new CookieJar();
		log = logs;
		log.checkLogs();

		// Ignore SSL certifications but maintain HTTPS connection
		// this is used if the SSL certificate on the library is
		// flagged by Java.
		if(ignoreSSL)
		{
			TrustManager[] trustAllCerts = new TrustManager[] { 
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {}
				}
			};

			// Install the all-trusting trust manager
			try
			{
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		
				// Create all-trusting host name verifier
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) { return true; }
				};

				// Install the all-trustng host verifier
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}

	public String queryLibrary(String httpRequest)
	{
		String response = "EMPTY";
		StringBuilder output = new StringBuilder();

		try
		{
			// Filter out the password so it isn't stored in logs.
			String[] tmp = httpRequest.split("&");
			if(tmp.length > 1 && tmp[1].substring(0,8).equals("password"))
			{
				log.log("Posting HTTP request: " + tmp[0], 2);
			}
			else
			{
				log.log("Posting HTTP request: " + httpRequest, 2);
			}

			tapeLibrary = new URL(httpRequest);

			tapeConn = (HttpURLConnection)tapeLibrary.openConnection();
			// Five second connection timeout.
			tapeConn.setConnectTimeout(50000);
			// Ten second read timeout
			tapeConn.setReadTimeout(50000);
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
				log.log("Error with HTTP Request: " + httpRequest, 3);
				log.log(e.getMessage(), 3);
			}	
		}

		return output.toString();
	}

	public String postPackageToLibrary(String httpRequest, String filename)
	{
		try
		{
			log.log("Posting package: " + filename, 2);
			log.log("HTTP Request: " + httpRequest, 2);

			URL tapeLibrary = new URL(httpRequest);
			HttpURLConnection conn = (HttpURLConnection) tapeLibrary.openConnection();
			conn.setConnectTimeout(5000); // 5 sec timeout
			conn.setReadTimeout(20000); // 20 sec read timeout
			conn.setDoOutput(true);

			if(!cookies.getName(0).equals("none"))
			{
				conn.setRequestProperty("Cookie", cookies.getName(0) + "=" + cookies.getValue(0));		
			}

			conn.setRequestMethod("POST");

			BufferedInputStream iStream = new BufferedInputStream(new FileInputStream(filename));
			BufferedOutputStream oStream = new BufferedOutputStream(conn.getOutputStream());

			// Read byte by byte until the end of the stream.
			int i;
			while((i=iStream.read())>0)
			{
				oStream.write(i);
			}

			iStream.close();
			oStream.close();

			return conn.getResponseMessage();
		
		}
		catch (Exception e)
		{
			log.log("Error with HTTP request: " + httpRequest, 3);
			log.log(e.getMessage(), 3);
			return e.getMessage();
		}
	}

	public String downloadFromLibrary(String httpRequest, String path, String filename)
	{
		try
		{
			log.log("Downloading " + filename + " to " + path, 2);
			log.log("HTTP Request: " + httpRequest, 2);	

			URL tapeLibrary = new URL(httpRequest);
			tapeConn = (HttpURLConnection)tapeLibrary.openConnection();

			if(!cookies.getName(0).equals("none"))
			{
				// If a cookie exists, pass it.
				tapeConn.setRequestProperty("Cookie", cookies.getName(0) + "=" + cookies.getValue(0));
			}

			ReadableByteChannel readChannel = Channels.newChannel(tapeConn.getInputStream());
			FileOutputStream outFile = new FileOutputStream(path+filename);
			FileChannel writeChannel = outFile.getChannel();

			writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);

			log.log("File " + filename + " successfully downloaded to " + path, 2);
			return "<message>File " + filename + " successfully downloaded to " 
				+ path + "</message>";
		}
		catch(IOException e)
		{
			log.log("Error with HTTP request: " + httpRequest, 3);
			log.log(e.getMessage(), 3);
			System.out.println(e);
			
			return "<status>FAILED</status>"; 
		}
	}
	
}
