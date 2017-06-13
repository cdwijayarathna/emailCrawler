package corpus.sinhala.crawler;

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

		System.setProperty("webdriver.chrome.driver", "C:\\DDDDDDDDDDDDDDDDDDDDDDDDDD\\myphdstuff\\paper1\\crawler\\chromedriver_win32\\chromedriver.exe");
		WebDriver driver=new ChromeDriver();
		driver.get("https://github.com/login");
		WebElement login = driver.findElement(By.name("login"));
		login.sendKeys("cdwijayarathna");
		WebElement pass = driver.findElement(By.name("password"));
		pass.sendKeys("zxxx");
		WebElement loginbutton = driver.findElement(By.name("commit"));
		loginbutton.click();
		int count = 0;
		for (int k=94; k<101; k++) {
			String startingUrl = "https://github.com/search?l=Java&o=desc&p=" + k + "&q=java&s=forks&type=Repositories";
			String nextUrl = startingUrl;
			XMLFileWriter writer = new XMLFileWriter(saveLocation);
				URL url = new URL(nextUrl);
				HttpURLConnection uc = (HttpURLConnection) url.openConnection();
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

				}
				//System.out.println(String.valueOf(tmp));
				//System.exit(0);
				Document doc = Jsoup.parse(String.valueOf(tmp));
				doc.setBaseUri(nextUrl);

				Elements h3 = doc.select("h3");
			Document docPage;
			Elements autags;
				for (int i = 1; i < h3.size(); i++) {
					String pageLink = h3.get(i).select("a").attr("href");
					System.out.println("aaa           " + pageLink);
					//url = new URL("https://github.com" + pageLink + "//graphs/contributors");
					do {
						driver.get("https://github.com" + pageLink + "//graphs/contributors");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//System.out.println(String.valueOf(driver.getPageSource()));
						docPage = Jsoup.parse(String.valueOf(driver.getPageSource()));
						autags = docPage.select("h3");
					}while (autags.size()<=1);


					for (int j = 1; j < autags.size(); j++) {
						String aupage = autags.get(j).select("a").attr("href");
						driver.get("https://github.com" + aupage);
						Document auDoc = Jsoup.parse(String.valueOf(driver.getPageSource()));
						Elements emailTag = auDoc.select("li[itemprop=email]");
						if (emailTag.size() > 0) {
							String email = emailTag.get(0).select("a").text();
							String name = auDoc.select("span[itemprop=name]").text();
							System.out.println(name + " : " + email);
							writer.addDocument(name, email, "https://github.com" + aupage);
							count ++;
						}
					}

			}
			writer.update(k);
			System.out.println("Count after page " + k + " : " + count);
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
