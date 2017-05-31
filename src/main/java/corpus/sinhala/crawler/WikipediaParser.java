package corpus.sinhala.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WikipediaParser {

	Document doc;
	String url;
	
	public WikipediaParser(String page, String url) {
		this.doc = Jsoup.parse(page);
		this.url = url;
	}

	public String getYear() {
		//System.out.println(doc.select("li[id=footer-info-lastmod]").first().text().split("මෙම පිටුව අවසන් වරට වෙනස් කරන ලද්දේ ")[1].split(" ")[2]);
		return doc.select("li[id=footer-info-lastmod]").first().text().split("මෙම පිටුව අවසන් වරට වෙනස් කරන ලද්දේ ")[1].split(" ")[2];
	
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		System.out.println(doc.select("h1[id=firstHeading]").first().text());
		return doc.select("h1[id=firstHeading]").first().text();
	}

	public String getContent() {
		String content = "";
		Elements el=doc.select("div[id=bodyContent]").first().select("div[id=mw-content-text]").first().select("p");
		for (int i = 0; i < el.size(); i++) {
			content +=el.get(i).text();
		}
		//System.out.println(content);
		return doc.select("div[id=bodyContent]").first().select("div[id=mw-content-text]").first().text();
	}

}
