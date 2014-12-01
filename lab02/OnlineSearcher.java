package lab02;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 百度的查词好了, 但是速度太慢..
 * */
class OnlineSearcher {
	private URL baidu;
	private URL youdao;
	private URL bing;
	
	Entry search(String keyword) {
		Entry result = new Entry(keyword);
		try {
			baidu = new URL("http://cidian.baidu.com/s?wd=" + keyword);
			youdao = new URL("http://dict.youdao.com/search?le=eng&q=" + keyword + "&keyfrom=dict.top");
			bing = new URL("http://cn.bing.com/dict/search?q=" + keyword + "&go=&qs=bs&form=CM");
			
			//System.out.println("hello, world1");
			InputStream urlInput = baidu.openStream();
			//System.out.println("hello, world2");
			StringBuffer webtemp = new StringBuffer();
			//System.out.println("hello, world3");
			Scanner input = new Scanner(urlInput);
			//System.out.println("hello, world4");
			while (input.hasNext()) {
				webtemp.append(input.nextLine());
			}
			//System.out.println("hello, world5");
			String weball = webtemp.toString();
			//System.out.println("hello, world6");
			
			String src = "baidu";
			String phonetic = (weball.split("<b lang=\"EN-US\" xml:lang=\"EN-US\">"))[1].split("</b><a href=\"#\"")[0];
			String tp = (weball.split("<p><strong>"))[1];
			String attribute = tp.split("</strong><span>")[0];
			String explanation = tp.split("</strong><span>")[1].split("</span></p>")[0];
			/*System.out.println(src);
			System.out.println(phonetic);
			System.out.println(attribute);
			System.out.println(explanation);*/
			result.setInformation(src, new Information(src, phonetic, attribute, explanation));
			input.close();
		} catch (IOException e) {
			System.out.println("url wrong or url open wrong!");
		}
		return result;
	}
	
	public static void main(String[] args) {
		OnlineSearcher oser = new OnlineSearcher();
		oser.search("hello");
		oser.search("world");
	}
}
