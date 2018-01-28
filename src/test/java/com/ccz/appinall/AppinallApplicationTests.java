package com.ccz.appinall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.proj4.PJ;
import org.proj4.PJException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ccz.appinall.library.util.CoordPoint;
import com.ccz.appinall.library.util.TransCoord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppinallApplicationTests {

	@Test
	public void contextLoads() {
		ObjectMapper mapper = new ObjectMapper();
		List<String> e = new ArrayList<String>();
		e.add("abcd");
		e.add("efgh");
		ArrayNode array = mapper.valueToTree(e);
		for(JsonNode node : array) {
			System.out.println(node.asText());
		}
		//Bessel정규화(36000 곱한 정수형)  to WGS84
		//double mCoordX = (double)4572443/36000;
		//double mCoordY = (double)1353791/36000;
		//CoordPoint pt = new CoordPoint(mCoordX, mCoordY);
		//testProj4Library();
		CoordPoint pt = new CoordPoint(968234.063446, 1950683.041385);
		
		CoordPoint besselPt = TransCoord.getTransCoord(pt, TransCoord.COORD_TYPE_UTM,TransCoord.COORD_TYPE_WGS84);
		System.out.println("WGS84 X 좌표 :: "+besselPt.x);
		System.out.println("WGS84 Y 좌표 :: "+besselPt.y);
		 
		CoordPoint pt2 = new CoordPoint(968234.063446, 1950683.041385);
		pt2.convertUTM2WGS(50000, 30000);
		System.out.println(pt2.x + ", " + pt2.y);
		//결과
		//WGS84 X 좌표 :: 127.01020216206658
		//WGS84 Y 좌표 :: 37.60809830471803
	}

	public void testProj4Library() {
		PJ sourcePJ = new PJ("+init=epsg:32632");                   // (x,y) axis order
        PJ targetPJ = new PJ("+proj=latlong +datum=WGS84");         // (λ,φ) axis order
        double[] coordinates = {
            500000,       0,   // First coordinate
            400000,  100000,   // Second coordinate
            600000, -100000    // Third coordinate
        };
        try {
			sourcePJ.transform(targetPJ, 2, coordinates, 0, 3);
		} catch (PJException e) {
			e.printStackTrace();
		}
        System.out.println(Arrays.toString(coordinates));
	}
}
