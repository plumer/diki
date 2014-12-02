package lab02;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 百度的查词好了, 但是速度太慢..
 * */
class OnlineSearcher {
	private ExecutorService searchThreads;
	
	public OnlineSearcher() {
		 searchThreads = Executors.newFixedThreadPool(3);
	}
	
	private class getBaiduInformation implements Runnable {
		private URL baidu;
		private Information baiduInfo;
		private String keyword;
		
		getBaiduInformation(String keyword) {
			this.keyword = keyword;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				baidu = new URL("http://cidian.baidu.com/s?wd=" + keyword);
				InputStream urlInput = baidu.openStream();
				StringBuffer webtemp = new StringBuffer();
				Scanner input = new Scanner(urlInput);
				while (input.hasNext()) {
					webtemp.append(input.nextLine());
				}
				String weball = webtemp.toString();
				
				String src = "baidu";
				String phonetic = null;
				String[] temp = weball.split("<b lang=\"EN-US\" xml:lang=\"EN-US\">");
				if (temp.length < 1) System.out.println("baidu error");
				else  phonetic = temp[1].split("</b><a href=\"#\"")[0];
				
				String tp = ((weball.split("<p><strong>")).length < 1)? null:(weball.split("<p><strong>"))[1];
				String attribute = tp.split("</strong><span>")[0];
				String explanation = tp.split("</strong><span>").length < 1 ? null: tp.split("</strong><span>")[1].split("</span></p>")[0];
				
				baiduInfo = new Information(src, phonetic, attribute, explanation);
				input.close();
			} catch (IOException e) {
				System.out.println("baidu url wrong or url open wrong!");
			}
		}
		Information getInformation() {
			return baiduInfo;
		}
	}
	
	private class getYoudaoInformation implements Runnable {
		private URL youdao;
		private Information youdaoInfo;
		private String keyword;
		
		getYoudaoInformation(String keyword) {
			this.keyword = keyword;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				youdao = new URL("http://dict.youdao.com/search?le=eng&q=" + keyword + "&keyfrom=dict.top");
				InputStream urlInput = youdao.openStream();
				StringBuffer webtemp = new StringBuffer();
				Scanner input = new Scanner(urlInput);
				while (input.hasNext()) {
					webtemp.append(input.nextLine());
				}
				String weball = webtemp.toString();
				
				String src = "youdao";
				String phonetic = ((weball.split("<span class=\"phonetic\">")).length < 1)? null:(weball.split("<span class=\"phonetic\">"))[1].split("</span>")[0];
				String tp = (weball.split("<li>")).length < 1? null:(weball.split("<li>"))[1];
				String attribute = tp == null ? null: (tp.split(". ")[0] + '.');
				String explanation = tp == null ? null : (tp.split(". ").length < 1? null:tp.split(". ")[1].split("<")[0]);
				youdaoInfo = new Information(src, phonetic, attribute, explanation);
				input.close();
			} catch (IOException e) {
				System.out.println("youdao url wrong or url open wrong!");
			}
		}
		Information getInformation() {
			return youdaoInfo;
		}
	}
	private class getBingInformation implements Runnable {
		private URL bing;
		private Information bingInfo;
		private String keyword;
		
		getBingInformation(String keyword) {
			this.keyword = keyword;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				bing = new URL("http://cidian.baidu.com/s?wd=" + keyword);
				InputStream urlInput = bing.openStream();
				StringBuffer webtemp = new StringBuffer();
				Scanner input = new Scanner(urlInput);
				while (input.hasNext()) {
					webtemp.append(input.nextLine());
				}
				String weball = webtemp.toString();
				
				String src = "bing";
				String phonetic = (weball.split("<div class=\"hd_prUS\">"))[1].split("</div>")[0].split(";")[1];
				String tp = weball.split("<ul>")[1];
				String attribute = tp.split("<li><span class=\"pos\">")[1].split("</span>")[0];
				String explanation = tp.split("<span>")[1].split("<")[0];
				bingInfo = new Information(src, phonetic, attribute, explanation);
				input.close();
			} catch (IOException e) {
				System.out.println("baidu url wrong or url open wrong!");
			}
		}
		Information getInformation() {
			return bingInfo;
		}
	}
	Entry search(String keyword) {
		Entry result = new Entry(keyword);
		getBaiduInformation getbaidu = new getBaiduInformation(keyword);
		getYoudaoInformation getyoudao = new getYoudaoInformation(keyword);
		getBingInformation getbing = new getBingInformation(keyword);
		
		searchThreads.execute(getbaidu);
		searchThreads.execute(getyoudao);
		searchThreads.execute(getbing);
		
		searchThreads.shutdown();
		
		try {
			searchThreads.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println("threads error");
		}
		
		result.setInformation("baidu", getbaidu.getInformation());
		result.setInformation("youdao", getyoudao.getInformation());
		result.setInformation("bing", getbing.getInformation());
		return result;
	}
	
	public static void main(String[] args) {
		OnlineSearcher oser = new OnlineSearcher();
		//oser.search("hjhjhj");
		oser.search("hello");
		oser.search("world");
	}
}
