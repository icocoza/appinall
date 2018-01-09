package com.ccz.appinall.library.module.morpheme.mecab;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EMorpheme {
	eNone("none"), eNNG("NNG"), eNNP("NNP"), eNNB("NNB"), eNR("NR"), eNP("NP"), eVV("VV"), eVA("VA"), eVX("VX"), eVCP("VCP"), 
	eVCN("VCN"), eMM("MM"), eMAG("MAG"), eMAJ("MAJ"), eIC("IC"), eJKS("JKS"), eJKC("JKC"), eJKG("JKG"), eJKO("JKO"), eJKB("JKB"),
	eJKV("JKV"), eJKQ("JKQ"), eJX("JX"), eJC("JC"), eEP("EP"), eEF("EF"), eEC("EC"), eETN("ETN"), eETM("ETM"), eXPN("XPN"), eXSN("SN"),
	eXSV("XSV"),	eXSA("XSA"),	eXR("XR"), eSF("SF"),	eSE("SE"), eSS("SS"),	eSP("SP"), eSO("SO"),	eSW("SW"), eSL("SL"),	eSH("SH"), eSN("SN"),
	eVAETM("VA+ETM");
	
	public final String value;
	
	private EMorpheme(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static final Map<String, EMorpheme> StrToAptCmdMap;
	
	static {
		StrToAptCmdMap = new ConcurrentHashMap<>();
		for(EMorpheme cmd : EMorpheme.values())
			StrToAptCmdMap.put(cmd.getValue(), cmd);
	}
	
	static public EMorpheme getType(String cmd) {
		EMorpheme ecmd = StrToAptCmdMap.get(cmd);
		if(ecmd != null)
			return ecmd;
		return eNone;
	}
}
