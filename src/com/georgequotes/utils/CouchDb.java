package com.georgequotes.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.georgequotes.utils.Http.Params;

import android.net.Uri;
import android.util.Pair;

public class CouchDb 
{
	private static final String GET = "GET";
	
	private class CreateUriParams
	{
		public ArrayList<Pair<String,String>> queryParameters = new ArrayList<Pair<String,String>>();
		public ArrayList<String> path = new ArrayList<String>();
		
		public void addQueryParameter(String key,String value)
		{
			queryParameters.add(new Pair<String,String>(key,value));
		}
	}
	
	private String fUrl;
	private String fDatabase;
	private String fLogin;
	private String fPassword;
	private int fTimeout;
	
	public CouchDb(String url,String database)
	{
		this(url,database,null,null,0);
	}
	
	public CouchDb(String url,String database,String login,String password,int timeout)
	{
		fUrl = url;
		fDatabase = database;
		fLogin = login;
		fPassword = password;
		fTimeout = timeout;
	}
	
	public JSONObject document(String document) throws MalformedURLException, JSONException, IOException
	{
		CreateUriParams p = new CreateUriParams();
		p.path.add(document);

		String uri = createUri(p);
		
		return new JSONObject(Http.execute(uri, GET, createParams()));
	}
	
	public JSONObject allDocs(boolean includeDocs) throws MalformedURLException, IOException, JSONException
	{
		CreateUriParams p = new CreateUriParams();
		p.path.add("_all_docs");

		if(includeDocs)
		{
			p.addQueryParameter("include_docs","true");
		}
		
		String uri = createUri(p);
		
		return new JSONObject(Http.execute(uri, GET, createParams()));
	}
	
	private Params createParams()
	{
		Params params = new Http.Params();
		params.readTimeout = fTimeout;
		params.userName = fLogin;
		params.password = fPassword;
		return params;		
	}
	
	private String createUri(CreateUriParams p)
	{
		Uri.Builder b= Uri.parse(fUrl).buildUpon();
		b.appendPath(fDatabase);
		for(String s:p.path)
			b.appendPath(s);
		
		for(Pair<String,String> kvp:p.queryParameters)
			b.appendQueryParameter(kvp.first, kvp.second);
		
		return b.toString();
	}
	
}
