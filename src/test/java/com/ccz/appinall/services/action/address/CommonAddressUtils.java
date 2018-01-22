package com.ccz.appinall.services.action.address;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import org.bson.Document;

import com.ccz.appinall.services.entity.elasticsearch.EntrcInfo;

public class CommonAddressUtils {
	static public void updateCoordination(List<EntrcInfo> list) {
		ProcessBuilder pb = new ProcessBuilder("/Users/1100177/projects/land-registry/proj-4.9.3/bin/cs2cs", "+proj=tmerc", "+lat_0=38", "+lon_0=127.5", "+k=0.9996", "+x_0=1000000", "+y_0=2000000", 
				"+ellps=GRS80", "+units=m", "+no_defs");
		try {
			Process p =  pb.start();
			InputStream is = p.getInputStream();
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
			PrintWriter stdin = new PrintWriter(p.getOutputStream());
			for(EntrcInfo item : list) {
				stdin.println(item.x + " " + item.y);
				System.out.printf(".");
			}
			System.out.println("@");
			stdin.close();
			String line;
			int index = 0;
		    while ((line = br.readLine()) != null) {
		    		EntrcInfo item = list.get(index++);
		    		item.setCoordinate(line);
		    }
			p.waitFor();
			if(list.size() != index) {
				System.out.println("mismatch coordinate count");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public Document makeDocument(String[] sp) {
		Document doc = new Document();
		doc.put("zip", sp[0]);	//우편번호 
		doc.put("sido", sp[1]);	//시
		doc.put("sigu", sp[3]);	//시군구
		doc.put("eub", sp[5]);	//읍면 
		doc.put("rcode", sp[7]); //도로명코드 
		doc.put("rname", sp[8]);	//도로명 
		doc.put("buildmgr", sp[13]);	//건물관리번호 
		doc.put("delivery", sp[14]);	//다량배달처명 
		doc.put("buildname", sp[15]);	//시군구용건물명 
		doc.put("dongname", sp[17]);	//법정동명 
		doc.put("liname", sp[18]);	//리명 
		doc.put("hjdongname", sp[19]);	//행정동명 
		doc.put("buildno", sp[11]);	//건물번호본번 
		doc.put("buildsubno", sp[12]);	//건물번호부번 
		doc.put("dongcode", sp[16]);	//법정동코드 
		doc.put("jino", sp[21]);	//지번본번 
		doc.put("eubseq", sp[22]);	//읍면동일련번호 
		doc.put("jisubno", sp[23]);	//지번부번 
		doc.put("base", sp[10]); //지하유무 
		doc.put("mnt", sp[20]);	//산유무 
		doc.put("id", sp[16] + sp[7] +"-"+ sp[11] +"-"+ sp[12] +"-"+ sp[10]);
		return doc;
	}
	
	static public EntrcInfo makeGRS80Map(String[] sp) {
		//dongcode + rcode + buildno + buildsub + base
		EntrcInfo ent = new EntrcInfo();
		ent.dongcode = sp[2]; 
		ent.rcode = sp[6];
		ent.buildno = sp[9];
		ent.buildsubno = sp[10];
		ent.base = sp[8];
		ent.x = sp[16].length()>0?Double.parseDouble(sp[16]) : 0L;
		ent.y = sp[17].length()>0?Double.parseDouble(sp[17]) : 0L;
		ent.sicode = sp[0];
		ent.entrance = sp[1];
		ent.rname = sp[7];
		ent.buildname = sp[11];
		ent.makeId();
		//System.out.println(ent.getId()+ "," +ent.sicode + "," + ent.entrance + "," + ent.rname );
		return ent;
	}
}
