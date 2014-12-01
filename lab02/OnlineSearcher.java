package lab02;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

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
			
			InputStream urlInput = baidu.openStream();
			StringBuffer webtemp = new StringBuffer();
			Scanner input = new Scanner(urlInput);
			while (input.hasNext()) {
				webtemp.append(input.nextLine());
			}
			String[] tmp = webtemp.toString().split("<p>");
			for (int i = 0; i < tmp.length; i ++) {
				if (tmp[i].contains("</p>")){
					String phonetic;
					String explanation;
					
					String trans = new String(tmp[i].split("</p>")[0]);
					if (trans)
				}
			}
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
