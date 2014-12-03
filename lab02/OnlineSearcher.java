package lab02;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * get到神器jsoup
 * 可以百度到jar包然后import一下嗯
 * 最后还是没有用多线程来实现【我太弱了Orz
 * 
 * 注意：
 * '#'来分开两种发音、词性和解释
 * 例如：查 hello
 * 音标: 美 [heˈləʊ]#英 [hə'ləʊ]
 * 词性: int.#n.
 * 解释: 你好；喂；您好；哈喽#你好；嘿；（表示惊讶）嘿
 * 保证词性个数和解释个数一一对应
 * */
class OnlineSearcher {
	private String baidu;
	private String youdao;
	private String bing;
	
	Entry search(String keyword) {
		Entry result = new Entry(keyword);
		try {
			baidu = "http://cidian.baidu.com/s?wd=" + keyword;
			youdao = "http://dict.youdao.com/search?le=eng&q=" + keyword + "&keyfrom=dict.top";
			bing = "http://cn.bing.com/dict/search?q=" + keyword + "&go=&qs=bs&form=CM";
			
			String src = null, phonetic = null, attribute = null, explanation = null;
			
			src = "baidu";
			Document baidudoc = Jsoup.connect(baidu).get();
			//System.out.println("baidu");
			
			/* 获取音标 */
			Elements temp = baidudoc.getElementsByTag("b");
			for (Element i:temp) {
				if (i.hasAttr("lang")) {
					if (phonetic == null) phonetic = i.text();
					else phonetic = phonetic + '#' + i.text();
				}
			}
			/* 获取词性 */
			temp = baidudoc.getElementById("en-simple-means").getElementsByTag("strong");
			for (Element i:temp) {
				if (attribute == null) attribute = i.text();
				else attribute = attribute + '#' + i.text();
			}
			/* 获取解释 */
			temp = baidudoc.getElementById("en-simple-means").getElementsByTag("span");
			for (int i = 0; i < attribute.split("#").length; i ++) {
				if (explanation == null) explanation = temp.get(i).text();
				else explanation = explanation + '#' + temp.get(i).text();
			}
			/*System.out.println(phonetic);
			System.out.println(attribute);
			System.out.println(explanation);*/
			result.setInformation(src, new Information(src, phonetic, attribute, explanation));
			
			src = "youdao";
			phonetic = null;
			attribute = null;
			explanation = null;
			//System.out.println("youdao");
			Document youdaodoc = Jsoup.connect(youdao).get();
			/* 获取音标 */
			temp = youdaodoc.getElementsByClass("phonetic");
			for (int i = 0; i < temp.size() && i < 2; i ++) {
				if (phonetic == null) phonetic = temp.get(i).text();
				else phonetic = phonetic + '#' + temp.get(i).text();
			}
			/* 获取词性和解释 */
			temp = youdaodoc.getElementsByClass("trans-container").get(0).getElementsByTag("li");
			for (Element i:temp) {
				if (attribute == null) {
					if (i.text().contains(". ")){
						attribute = i.text().split(". ")[0];
						explanation = i.text().split(". ")[1];
					} else {
						explanation = i.text();
					}
				}
				else {
					if (i.text().contains(". ")){
						attribute = attribute + '#' + i.text().split(". ")[0];
						explanation = explanation + '#' + i.text().split(". ")[1];
					} else {
						explanation = explanation + '#' + i.text();
					}
				}
			}
			/*System.out.println(phonetic);
			System.out.println(attribute);
			System.out.println(explanation);*/
			result.setInformation(src, new Information(src, phonetic, attribute, explanation));
			
			src = "bing";
			phonetic = null;
			attribute = null;
			explanation = null;
			//System.out.println("bing");
			Document bingdoc = Jsoup.connect(bing).get();
			/* 获取音标 */
			temp = bingdoc.getElementsByClass("hd_prUS");
			if (temp.size() > 0) phonetic = temp.text();
			temp = bingdoc.getElementsByClass("hd_pr");
			if (temp.size() > 0) phonetic = phonetic + '#' + temp.text();
			/* 获取词性和解释 */
			temp = bingdoc.getElementsByClass("qdef").get(0).getElementsByTag("ul").get(0).getElementsByTag("li");
			for (Element i:temp) {
				Element j = i.getElementsByClass("pos").get(0);
				if (j.className().equals("pos")){
					if (attribute == null) attribute = j.text();
					else attribute = attribute + '#' + j.text();
					Element k = i.getElementsByClass("def").get(0);
					if (explanation == null) explanation = k.text();
					else explanation = explanation + '#' + k.text();
				}
			}
			/*System.out.println(phonetic);
			System.out.println(attribute);
			System.out.println(explanation);*/
			result.setInformation(src, new Information(src, phonetic, attribute, explanation));
			
		} catch (IOException e) {
			System.out.println("url wrong or url open wrong!");
			e.printStackTrace();
		}
		return result;
	}
	
	/*public static void main(String[] args) {
		OnlineSearcher oser = new OnlineSearcher();
		oser.search("hello");
		oser.search("Amy");
		oser.search("Amily");
	}*/
}
