package webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webscraping.Hibernate.HibernateAddRecords;

/**
 * EbuyerScraper class scrapes data from www.ebuyer.com website 
 */
public class EbuyerScraper extends Thread{
    private String productName;

    @Override
    public void run() {
        try {
            scrapeEbuyer();
        } catch (Exception e) {
            // Throwing an exception 
            System.out.println("Exception in EbuyerScraper class caught");
            e.printStackTrace();
        }
    }

    /**
     * Method scraping websites content.
     */
    void scrapeEbuyer() throws Exception{
        //Name of the searched product 
        String itemName = this.productName;

        //Download HTML document from website
        Document doc = Jsoup.connect("https://www.ebuyer.com/search?q=" + itemName).get();

        //Get number of sites
        Elements sitePages = doc.select("div.filter-top");
        Elements tmpNoOfPages = sitePages.select("span.listing-count");
        String NoOfPagesString = tmpNoOfPages.text();
               NoOfPagesString = NoOfPagesString.substring(0, NoOfPagesString.indexOf(" "));
        int noOfPages = Integer.parseInt(NoOfPagesString);
        
        Thread.sleep(1000);
        HibernateAddRecords hibernateConnection = new HibernateAddRecords();
                            hibernateConnection.init();

        for (int currentPage = 1; currentPage == 1 || currentPage < noOfPages; currentPage++) {
            doc = Jsoup.connect("https://www.ebuyer.com/search?page=" + currentPage + "&q=" + itemName).get();

            //Get products details
            Elements itemContainer = doc.select("div.grid-item.js-listing-product");
            //Get products Manufacturer and model details
            Elements productManufacturer = itemContainer.select("h3.grid-item__title");
            //Get products Manufacturer and model details
            Elements productPrice_lvl_1 = itemContainer.select("div.grid-item__price");
            Elements productPrice_lvl_2 = productPrice_lvl_1.select("p.price");
            //Get product link
            Elements link_lvl_1 = itemContainer.select("h3.grid-item__title");
            Elements link_lvl_2 = link_lvl_1.select("a");
            
            Elements pictureElement = itemContainer.select("div.grid-item__img");
                     pictureElement = pictureElement.select("img");

            for (int i = 0; i < itemContainer.size(); ++i) {

                /**@param manufacturer stores information about manufacturer*/
                Elements manufacturer = productManufacturer.get(i).select("h3.grid-item__title");
                String manufacturerString = manufacturer.text();
                       manufacturerString = manufacturerString.substring(0, manufacturerString.indexOf(" "));
                       manufacturerString = manufacturerString.replace(",", "");
                       manufacturerString = manufacturerString.replace(" ", "");
                       manufacturerString = manufacturerString.toUpperCase();

                /**@param model stores information about model*/
                Elements model = productManufacturer.get(i).select("h3.grid-item__title");
                String modelString = model.text();
                       modelString = modelString.replace(manufacturerString, "");

                /**@param price stores price value of product*/
                Elements price = productPrice_lvl_2.get(i).select("p.price");
                String priceString = price.text();
                       priceString = priceString.replace("£ ", "");
                       priceString = priceString.replace("£", "");
                       priceString = priceString.replace("?", "");
                       priceString = priceString.replace(" inc. vat", "");
                       priceString = priceString.replace(" ex. vat", "");
                       priceString = priceString.replace(",", "");
                double finalPrice = Double.parseDouble(priceString);

                /**@param url stores redirecting link to the product*/
                String url = link_lvl_2.get(i).attr("abs:href");
                
                
                String pictureUrlStr = pictureElement.get(i).attr("abs:src");
                
                /**Send scraped data to the method, which will save it in database*/
                hibernateConnection.addRecord(url, manufacturerString, modelString, finalPrice, pictureUrlStr);
                
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
