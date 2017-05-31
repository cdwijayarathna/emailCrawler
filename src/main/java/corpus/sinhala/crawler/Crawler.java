package corpus.sinhala.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.stream.XMLStreamException;

public class Crawler {
	
	Connection conn = null;
	String saveLocation;
	
	public Crawler(String saveLocation) {
		this.saveLocation = saveLocation;

	}
	

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, XMLStreamException{

		
		Crawler crawler = new Crawler("");
		crawler.crawl();
	}
	
	public void crawl() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, XMLStreamException{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		int counter=1;
//		String startingUrl = "https://github.com/search?l=Java&p=1&q=java&type=Repositories&utf8=%E2%9C%93";
		String startingUrl = "https://github.com/search?l=Java&p=2&q=java&type=Repositories&utf8=%E2%9C%93";
		String nextUrl=startingUrl;
		XMLFileWriter writer = new XMLFileWriter(saveLocation);
		while(nextUrl!=null){
			URL url = new URL(nextUrl);
			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			uc.setRequestProperty("Authorization", "Basic Y2R3aWpheWFyYXRobmFAZ21haWwuY29tOmNoYW1pbGExIQ==");
			uc.connect();

			String line = null;
			StringBuffer tmp = new StringBuffer();
			try{
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream(), "UTF-8"));
			while ((line = in.readLine()) != null) {
				tmp.append(line);
			}
			}catch(FileNotFoundException e){
				
			}
			//System.out.println(String.valueOf(tmp));
			//System.exit(0);
			Document doc = Jsoup.parse(String.valueOf(tmp));
			doc.setBaseUri(nextUrl);
			
			Elements h3=doc.select("h3");
			for (int i = 0; i < h3.size(); i++) {
				String pageLink = h3.get(i).select("a").attr("href");

				System.out.println("aaa           " + pageLink);
				url = new URL("https://github.com" + pageLink + "//graphs/contributors");
				uc = (HttpURLConnection) url.openConnection();
				uc.connect();
				tmp = new StringBuffer();
				try{
				BufferedReader in = new BufferedReader(new InputStreamReader(
						uc.getInputStream(), "UTF-8"));
				while ((line = in.readLine()) != null) {
					tmp.append(line);
				}
				}catch(FileNotFoundException e){

				}
				Document docPage = Jsoup.parse(String.valueOf(tmp));
				Elements autags=docPage.select("h3");
				System.out.println(docPage);
				for (int j = 0; j < autags.size(); j++) {
					String aupage = autags.get(j).select("a").attr("href");
					System.out.println("bbb           " + aupage);
				}

			}
			System.exit(0);
			Elements navElements=doc.select("div[class=mw-allpages-nav]").first().select("a");
			nextUrl=null;
			
			for (int i = 0; i < navElements.size(); i++) {
				Element element=navElements.get(i);
				if(element.text().startsWith("මීළඟ පිටුව")){
					nextUrl="http://si.wikipedia.org"+ element.attr("href");
				}
			}
			
			
		}
		
		
	}
	
	boolean checkDB(String url){
		String query = "SELECT * FROM wikipedia_content WHERE url = ?";
		ResultSet rs;

		PreparedStatement stmt3;
		try {
			stmt3 = conn.prepareStatement(query);
			stmt3.setString(1, url);
			rs = stmt3.executeQuery();
			if (rs.first()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void writeDB(String url){
		String query = "INSERT INTO wikipedia_content (`url`) VALUES (?);";

		PreparedStatement stmt3;
		try {
			stmt3 = conn.prepareStatement(query);
			stmt3.setString(1, url);
			stmt3.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
