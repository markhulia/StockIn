package com.georgequotes.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;



public class Http 
{
	public static class Params
	{
		public int readTimeout = 1000;
		public boolean auth;
		public String userName;
		public String password;
	}
	
	
	public static String execute(String url,String method,final Params params) throws MalformedURLException, IOException
	{
		 HttpURLConnection c = (HttpURLConnection)new URL(url).openConnection();
         c.setRequestMethod(method);

         if(null != params)
         {
        	 c.setReadTimeout(params.readTimeout);
        	 
        	 if(params.userName != null && params.password != null)
        	 {
               Authenticator.setDefault(new Authenticator() 
               {
                   protected PasswordAuthentication getPasswordAuthentication() {
                     return new PasswordAuthentication(params.userName, params.password.toCharArray());
                   }
                   
               });
        	 }
         }

         
         c.connect();
         
     
         InputStream is = null;
         
         try
         {
        	 is = c.getInputStream();
        	 
        	 return inputStreamToString(is);
         }
         finally
         {
        	 if(null != is)
        		 is.close();
         }
         
	}
	
	private static String inputStreamToString(InputStream is) throws IOException
	{
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	    }
	    
	    // Return full string
	    return total.toString();
	}
}
