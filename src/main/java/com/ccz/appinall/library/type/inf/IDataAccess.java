package com.ccz.appinall.library.type.inf;

import com.fasterxml.jackson.databind.JsonNode;

public interface IDataAccess extends IDataStore {
	public String getFilePath();		//if EDataStoreType.eFile
	public boolean split(String s);		//if EDataStoreType.eString
	public String getAction();			//if EDataStoreType.eString
	public String getCommand();			//if EDataStoreType.eString
	public boolean isJson();				//if EDataStoreType.eString
	public String[] getSplitData();			//if EDataStoreType.eString
	public String getStringData();
	public JsonNode getJsonData();
}
