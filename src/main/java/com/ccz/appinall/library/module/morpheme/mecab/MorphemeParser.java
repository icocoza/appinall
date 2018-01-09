package com.ccz.appinall.library.module.morpheme.mecab;

import java.util.ArrayList;
import java.util.List;

import com.ccz.appinall.library.module.morpheme.mecab.MorphemeTagger.MPair;

public class MorphemeParser {
	static private MorphemeParser s_pThis;
	public static MorphemeParser getInst() {
		if(s_pThis==null)
			s_pThis = new MorphemeParser();
		return s_pThis;
	}
	
	public static void freeInst() {
		s_pThis = null;
	}
	
	public String parseStr(String str) {
		str = str.trim();
		MorphemeTagger tagger = new MorphemeTagger();
		if(tagger.parse(str)==false)
			return "";
		
		String[] units = str.split(" ");
		StringBuilder sbMorpheme = new StringBuilder();
		return analysis(0, units, tagger, sbMorpheme);
	}
	
	public List<String> parseNounType(String str) {
		str = str.trim();
		MorphemeTagger tagger = new MorphemeTagger();
		if(tagger.parse(str)==false)
			return null;
		return analysis(tagger);
	}
	
	private List<String> analysis(MorphemeTagger tagger) {
		List<String> moList = new ArrayList<>();
		List<MPair> pairList = tagger.getList();

		MPair[] pairArr = pairList.toArray(new MPair[pairList.size()]);
		int endPoint=0;
		for(int i=0; i<pairArr.length; i++) {
			if(isValidTag(pairArr[i].tag)==false) {
				endPoint = i+1;
				continue;
			}
			if(pairArr[i].tag==EMorpheme.eVAETM) {	//no more than 2 units
				endPoint=i;
				continue;
			}
			String adjStr = "";
			if(endPoint < i)
				adjStr = pairArr[endPoint].word;
			
			if(i<pairArr.length-1 &&  isNounTag(pairArr[i+1].tag)) {
				if(moList.contains(adjStr + pairArr[i].word + pairArr[i+1].word)==false) moList.add(adjStr + pairArr[i].word + pairArr[i+1].word);	//�ſ��ġ�
				if(moList.contains(adjStr + pairArr[i].word)==false)	moList.add(adjStr + pairArr[i].word);						//�ſ��ġ
				if(moList.contains(pairArr[i+1].word)==false) 	moList.add(pairArr[i+1].word);								//�
			}else if(moList.contains(adjStr + pairArr[i].word)==false)
				moList.add(adjStr + pairArr[i].word);	
			endPoint = i+1;
		}
		return moList;
	}
	
	private boolean isValidTag(EMorpheme tag) {
		if(tag == EMorpheme.eVAETM || tag == EMorpheme.eMM || tag == EMorpheme.eNNG || tag == EMorpheme.eNNP)
			return true;
		return false;
	}
	private boolean isNounTag(EMorpheme tag) {
		if(tag == EMorpheme.eMM || tag == EMorpheme.eNNG || tag == EMorpheme.eNNP)
			return true;
		return false;
	}
	
	private String analysis(int idx, String[] txt, MorphemeTagger tagger, StringBuilder sb) {
		if(txt[idx].trim().length()<1)
			return analysis(++idx, txt, tagger, sb);
		String word = "";
		StringBuilder sbTags = new StringBuilder();
		EMorpheme beforeTag = EMorpheme.eNone;
		while( word.equals(txt[idx])==false || word.length() < txt[idx].length() ) {
			MPair mp = tagger.next();
			if(mp == null)
				break;
			word +=  mp.word;
			if(sbTags.length() > 0) sbTags.append("+");
				sbTags.append(mp.tag);
			beforeTag = mp.tag;
		}
		
		if(tagger.isLast() || idx+1==txt.length)
			return sb.append(word).append("\t").append(sbTags).toString();
		return analysis(++idx, txt, tagger, sb.append(word).append("\t").append(sbTags).append("\n"));
	}
	
	private boolean isContinousTag(String beforeTag, String curTag) {
		if(beforeTag.length()<1)
			return false;
		if(beforeTag.charAt(0)=='V' && curTag.charAt(0)=='E' )	
			return true;
		else if(beforeTag.equals("NP") && curTag.charAt(0)=='J' )
			return true;
		return false;
	}
}
