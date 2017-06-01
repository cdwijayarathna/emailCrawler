package corpus.sinhala.crawler;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumCrawler {

    public static void main (String[] args) {
        WebDriver driver=new ChromeDriver();
        driver.get("www.google.lk");
    }


}
