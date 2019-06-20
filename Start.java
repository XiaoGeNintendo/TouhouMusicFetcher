package com.hhs.xgn.th;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Start {
	
	static void download(String from,String to) throws Exception{
		URL url=new URL("https:"+from);
		InputStream is=url.openStream();
		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream("song/"+to));
		BufferedInputStream bis=new BufferedInputStream(is);
		
		byte[] data=new byte[16384];
		
		long sum=0,tmpsum=0;
		
		long start=System.currentTimeMillis();
		
		long bck=start;
		
		while(true){
			
			
			int read=bis.read(data);

			if(read==-1){
				break;
			}
			
			bos.write(data,0,read);
			
			sum+=read;
			
			tmpsum+=read;
			
			if(System.currentTimeMillis()-bck>=1000){
				System.out.println("Downloaded:"+sum+"bytes. Speed:"+tmpsum);
				tmpsum=0;
				bck=System.currentTimeMillis();
			}
		}
		
		bis.close();
		bos.close();
		long delta=System.currentTimeMillis()-start;
		
		System.out.println("Done downloading in "+delta+" ms");
	}
	
	static Document tryParse(String s) throws Exception{
		for(int i=0;i<5;i++){
			try{
				return Jsoup.parse(new URL(s),10000);
			}catch(Exception e){
				System.out.println("Failed retrying!");
			}
		}
		
		throw new Exception("Try failed!");
	}
	
	static void rawFetch(Element e) throws Exception{
		System.out.println("Start fetching "+e.text());
		Document d=tryParse("https://thwiki.cc"+e.attr("href"));
		Elements ele=d.getElementsByClass("internal");
		String lnk=ele.get(0).attr("href");
		System.out.println("Link="+lnk);
		download(lnk,e.text());
	}
	public static void main(String[] args) throws Exception{
		if(!new File("song").exists()){
			new File("song").mkdirs();
		}
		Scanner s=new Scanner(System.in);
		String url=s.nextLine();
//		String url="https://thwiki.cc/%E5%88%86%E7%B1%BB:%E5%AE%98%E6%96%B9MIDI";
		Document d=tryParse(url);
		
		Elements ele=d.getElementsByClass("galleryfilename galleryfilename-truncate");
		
		boolean always=false;
		System.out.println("Total "+ele.size()+"songs are found!");
		for(Element e:ele){
			System.out.println(e.text()+"?");
			if(always){
				rawFetch(e);
				continue;
			}
			try{
				System.out.println("A continue B exit C skip D always continue");
				String str=s.nextLine();
				if(str.equalsIgnoreCase("A")){
					rawFetch(e);
					continue;
				}
				if(str.equalsIgnoreCase("B")){
					System.out.println("Stopped");
					System.exit(0);
				}
				if(str.equalsIgnoreCase("C")){
					System.out.println("Skipped");
				}
				if(str.equalsIgnoreCase("D")){
					always=true;
					rawFetch(e);
				}
			}catch(Exception er){
				er.printStackTrace();
				
			}
		}
		s.close();
	}
}
