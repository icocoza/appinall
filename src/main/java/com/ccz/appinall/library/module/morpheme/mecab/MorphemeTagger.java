package com.ccz.appinall.library.module.morpheme.mecab;

import java.util.ArrayList;
import java.util.List;

import org.chasen.mecab.Tagger;


class MorphemeTagger {
	int idx = 0;
	List<MPair> pairList = new ArrayList<MPair>();
	
	public boolean parse(String txt) {
		Tagger tagger = new Tagger();
		String tagStr = tagger.parse(txt);
		String[] lines = tagStr.split("\n");
		
		for(String line : lines) {
			if(line.startsWith("EOS"))
				break;
			pairList.add(new MPair(line));
		}
		return pairList.size()>0;
	}
	
	public MPair first() {
		return pairList.get(idx=0);
	}
	public MPair last() {
		return pairList.get(idx = pairList.size()-1);
	}
	
	public MPair next() {
		return (idx < pairList.size()) ? pairList.get(idx++) : null; 
	}
	
	public boolean isLast() {
		return idx >= pairList.size();
	}
	
	public List<MPair> getList() {
		return pairList;
	}

	public class MPair {
		public String word;
		public EMorpheme tag;
		
		public MPair(String word, String tag) {
			this.word = word;
			this.tag = EMorpheme.getType(tag);
		}

		public MPair(String word, EMorpheme tag) {
			this.word = word;
			this.tag = tag;
		}

		public MPair(String line) {
			String[] split = line.split("\t");
			if(split.length < 1)
				return;
			this.word = split[0];
			this.tag = EMorpheme.getType(split[1].substring(0, split[1].indexOf(',')));
		}
	}
}
