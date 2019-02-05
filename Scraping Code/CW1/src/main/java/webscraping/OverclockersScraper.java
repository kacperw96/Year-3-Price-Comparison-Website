package webscraping;
import webscraping.Hibernate.HibernateAddRecords;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**OverclockersScraper Class scrapes data from www.overclockers.co.uk website.*/
public class OverclockersScraper extends Thread {
    private String productName;
    
    @Override
    public void run() {
        try {
            scrapeOverclockers();
        } catch (Exception e) {
            System.out.println("Exception is caught");
            e.printStackTrace();
        }
    }

    /**
     * Method scraping websites content
     */
    void scrapeOverclockers() throws Exception {
        //Name of item that we want to scrape
        String itemName = this.productName;

        //Download HTML document from website
        Document doc = Jsoup.connect("https://www.overclockers.co.uk/search/index/sSearch/" + itemName + "/sPerPage/12/sPage/1").get();

        //Get number of sites
        Elements sitePages = doc.select("div.display_sites");
        Elements NoOfPages = sitePages.select("strong");
        int number = Integer.parseInt(NoOfPages.get(1).text());
        
        Thread.sleep(2000);
        HibernateAddRecords hibernateConnection = new HibernateAddRecords();
                            hibernateConnection.init();

        for (int current_page = 1; current_page <= number; current_page++) {
            doc = Jsoup.connect("https://www.overclockers.co.uk/search/index/sSearch/" + itemName + "/sPerPage/12/sPage/" + current_page).get();

            //Get all of the products on the page
            Elements artbox = doc.select("div.artbox");
            Elements producttitles = artbox.select("a.producttitles");
            Elements ProductSubTitle = artbox.select(".ProductSubTitle");
            Elements ProductTitle = artbox.select(".ProductTitle");
            Elements ProductPrice = artbox.select("span.price");
            Elements ProductLink = artbox.select("a.producttitles");
            Elements productImg = artbox.select("a.product_img");
                     productImg = productImg.select("img");
            

            for (int i = 0; i < artbox.size(); ++i) {
                
                /**@param manufacturer stores information about manufacturer*/
                Elements brand = ProductSubTitle.get(i).select(".ProductSubTitle");
                String manufacturer = brand.text();
                       manufacturer = manufacturer.replace(" ", "");
                       manufacturer = manufacturer.toUpperCase();
                
                /**@param productModel stores information about model*/
                Elements model = artbox.get(i).select(".ProductTitle");
                String productModel = model.text();
                
                /**@param finalPrice stores price of product*/
                Elements price = artbox.get(i).select("span.price");
                String price1 = price.text();
                       price1 =  price1.replace("£", "");
                       price1 =  price1.replace(",", "");
                double finalPrice = Double.parseDouble(price1);       

                /**@param finalURL stores price of product*/
                Elements link = artbox.get(i).select("a.producttitles");
                String finalURL = link.attr("abs:href");
                
                Elements productImg2 = productImg.get(i).select("img");
                
                String stringImg = productImg.get(i).attr("abs:src");
                
                hibernateConnection.addRecord(finalURL, manufacturer, productModel, finalPrice, stringImg);
            }
        Thread.sleep(2000);
        }
    }
    
    /**@param name set name of the product, which will be scraped*/
    public void setProductName(String name){
       this.productName = name;
       run();
    }
}
