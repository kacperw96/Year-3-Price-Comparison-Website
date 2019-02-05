package webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webscraping.Hibernate.HibernateAddRecords;

/**
 * ScanScraper Class scrapes data from www.scan.co.uk website.
 */
public class ScanScraper extends Thread {
    private String productName;

    @Override
    public void run() {
        try {
            scrapeScan();
        } catch (Exception e) {
            // Throwing an exception 
            System.out.println("Exception is caught");
            e.printStackTrace();
        }
    }

    /**
     * Method scraping websites content.
     */
    void scrapeScan() throws Exception {
        //Name of item that we want to scrape
        String itemName = this.productName;

        //Download HTML document from website
        Document doc = Jsoup.connect("https://www.scan.co.uk/search?q=" + itemName).get();

        Elements searchProductGroup = doc.select("div.searchProductGroup");
        Elements productColumns = searchProductGroup.select("ul.productColumns");
        Elements product = productColumns.select("li.product");
        Elements price_lvl_1 = product.select("div.leftColumn");
        Elements price_lvl_2 = product.select("span.price");
        Elements link = product.select("span.description");
        Elements a_link = product.select("a");
        Elements img = product.select("div.image");
                 img = img.select("img");
        
        HibernateAddRecords hibernateConnection = new HibernateAddRecords();
                            hibernateConnection.init();

        for (int i = 0; i < product.size(); ++i) {
            /**@param productManofacturer stores information about manufacturer*/           
            String productManofacturer = product.get(i).attr("data-manufacturer");
                   productManofacturer = productManofacturer.replace(" ", "");
                   productManofacturer = productManofacturer.toUpperCase();

            /**@param model stores information about model*/            
            String model = product.get(i).attr("data-description");
            model = model.substring(0, model.indexOf(","));

            /**@param finalPrice stores price*/ 
            String price = price_lvl_2.get(i).text();
                   price = price.replace("£", "");
                   price = price.replace(",", "");
            double finalPrice = Double.parseDouble(price);

            /**@param url stores url link to website*/           
            String url = a_link.get(i).attr("abs:href");
            
            String imgSrc = img.get(i).attr("abs:src");
            System.out.println(imgSrc);
            
            hibernateConnection.addRecord(url, productManofacturer, model, finalPrice, imgSrc);
            Thread.sleep(250);
        }
    }
    
    /**@param name set name of the product, which will be scraped*/
    public void setProductName(String name){
       this.productName = name;
       run();
    }
}
