package com.ccz.appinall;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRuntimeCommand {

	@Test
	public void runtimeCommand() {
		try {
			//String[] command ={ "/Users/1100177/projects/land-registry/proj-4.9.3/bin/cs2cs"};/// +proj=tmerc +lat_0=38 +lon_0=127.5 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=bessel +units=m +no_defs +towgs84=-145.907,505.034,685.756,-1.162,2.347,1.592,6.342", };
			//새주소지도 
			//ProcessBuilder pb = new ProcessBuilder("/Users/1100177/projects/land-registry/proj-4.9.3/bin/cs2cs", "+proj=tmerc", "+lat_0=38", "+lon_0=127.5", "+k=0.9996", "+x_0=1000000", "+y_0=2000000", 
			//		"+ellps=GRS80", "+units=m", "+no_defs", "+towgs84=-145.907,505.034,685.756,-1.162,2.347,1.592,6.342");
			//for naver
			ProcessBuilder pb = new ProcessBuilder("/Users/1100177/projects/land-registry/proj-4.9.3/bin/cs2cs", "+proj=tmerc", "+lat_0=38", "+lon_0=127.5", "+k=0.9996", "+x_0=1000000", "+y_0=2000000", 
							"+ellps=GRS80", "+units=m", "+no_defs");//, "+towgs84=-145.907,505.034,685.756,-1.162,2.347,1.592,6.342");
			Process p =  pb.start(); //Runtime.getRuntime().exec(command);
			//new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
			//new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
			InputStream is = p.getInputStream();
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
			PrintWriter stdin = new PrintWriter(p.getOutputStream());
			//stdin.println("dir c:\\ /A /Q");
			stdin.println("968234.063446 1950683.041385");
			// write any other commands you want here
			stdin.close();
			String line;
		    while ((line = br.readLine()) != null) {
		      System.out.println(line);
		    }
			int returnCode = p.waitFor();
			System.out.println("Return code = " + returnCode);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	class SyncPipe implements Runnable
	{
		public SyncPipe(InputStream istrm, OutputStream ostrm) {
		      istrm_ = istrm;
		      ostrm_ = ostrm;
		  }
		  public void run() {
		      try
		      {
		          final byte[] buffer = new byte[1024];
		          for (int length = 0; (length = istrm_.read(buffer)) != -1; )
		          {
		              ostrm_.write(buffer, 0, length);
		          }
		      }
		      catch (Exception e)
		      {
		          e.printStackTrace();
		      }
		  }
		  private final OutputStream ostrm_;
		  private final InputStream istrm_;
	}
}
