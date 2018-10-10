package corpus.sinhala.crawler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ThreadGuard;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

		System.setProperty("webdriver.chrome.driver", "C:\\DDDDDDDDDDDDDDDDDDDDDDDDDD\\myphdstuff\\paper1\\crawler\\chromedriver_win32_2\\chromedriver.exe");
		WebDriver driver=new ChromeDriver();
		driver.get("https://github.com/login");
		WebElement login = driver.findElement(By.name("login"));
		login.sendKeys("cdwijayarathna");
		WebElement pass = driver.findElement(By.name("password"));
		pass.sendKeys("xxxxxxxxxxxx");
		WebElement loginbutton = driver.findElement(By.name("commit"));
		loginbutton.click();
		int count = 0;
        URL url = null;
		int pageNumber = 3172;
        String nextId="1306585";
        while (!nextId.isEmpty()) {
			XMLFileWriter writer = new XMLFileWriter(saveLocation);
            url = new URL("https://api.github.com/repositories?access_token=XXXXXXXXXX&since=" + nextId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            //System.out.println("Output from Server .... \n");
            output = br.readLine();
            //System.out.print(output + "    ");
            JSONParser parser = new JSONParser();
			JSONArray json = null;
			try {
				json = (JSONArray) parser.parse(output);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			outer:
			for (int i = 0; i < json.size(); i++) {
				System.out.print(((JSONObject) json.get(i)).get("full_name").toString() + "   " + ((JSONObject) json.get(i)).get("id").toString());
				URL page = new URL("https://github.com/" + ((JSONObject) json.get(i)).get("full_name").toString());
				HttpURLConnection uc = (HttpURLConnection) page.openConnection();
				uc.connect();

				String line = null;
				StringBuffer tmp = new StringBuffer();
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(
							uc.getInputStream(), "UTF-8"));
					while ((line = in.readLine()) != null) {
						tmp.append(line);
					}
				} catch (FileNotFoundException e) {

				} catch (IOException e){
					continue ;
				}
				//System.out.println(String.valueOf(tmp));
				//System.exit(0);
				org.jsoup.nodes.Document doc = Jsoup.parse(String.valueOf(tmp));
				//doc.setBaseUri(nextUrl);
				Elements h3 = doc.select("span[class=language-color]");
				//System.out.println();
				if (h3.size()>0)
					System.out.println(h3.get(0).text());
				if (h3.size() > 0 && h3.get(0).text().equals("Java")) {
					Document docPage;
					Elements autags;
						long timeStart = System.currentTimeMillis();
						do {
							driver.get("https://github.com/" + ((JSONObject) json.get(i)).get("full_name").toString() + "/graphs/contributors");
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							//System.out.println(String.valueOf(driver.getPageSource()));
							docPage = Jsoup.parse(String.valueOf(driver.getPageSource()));
							autags = docPage.select("h3");
							long currTime = System.currentTimeMillis();
							if (currTime - timeStart > 7200000)
								continue outer;
						} while (autags.size() < 1);
//Chaneged <= with =

						for (int j = 0; j < autags.size(); j++) {
							String aupage = autags.get(j).select("a").attr("href");
							driver.get("https://github.com" + aupage);
							Document auDoc = Jsoup.parse(String.valueOf(driver.getPageSource()));
							Elements emailTag = auDoc.select("li[itemprop=email]");
							if (emailTag.size() > 0) {
								String email = emailTag.get(0).select("a").text();
								String name = auDoc.select("span[itemprop=name]").text();
								System.out.println(name + " : " + email);
								writer.addDocument(name, email, "https://github.com" + aupage);
								count++;
							}
						}

				}
			}
			writer.update(pageNumber);
			System.out.println("Count after page " + pageNumber + " : " + count);
			pageNumber ++;
			nextId = ((JSONObject) json.get(json.size() - 1)).get("id").toString();
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
