package webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webscraping.Hibernate.HibernateAddRecords;

/**NovatechScraper class scrapes data from www.novatech.co.uk website.*/
public class NovatechScraper extends Thread {
    public String productName;

    @Override
    public void run() {
        try {
            scrapeNovotech();
        } catch (Exception e) { 
            System.out.println("Exception in Novotech Scraper caught");
            e.printStackTrace();
        }
    }
    
    /** Method scraping websites content
     *@param manufacturer stores product brand
     *@param model stores product model
     *@param url stores product link
     *@param price stores product price */
   void scrapeNovotech() throws Exception {
       
       //Name of item that we want to scrape
        String itemName = this.productName;

        //Download HTML document from website
        Document doc = Jsoup.connect("https://www.novatech.co.uk/search.html?s=" + itemName).get();
       
        //Number of pages
        Elements sitePages = doc.select("div.row");
        Elements tmpNoOfPages = sitePages.select("p#showing-text");
        /*
        String NoOfPages_string = tmpNoOfPages.text();
               NoOfPages_string = NoOfPages_string.replace("Showing 1 to 24 of ", "");
               
        double noOfPages = Math.round(Double.parseDouble(NoOfPages_string)/24); */
        int noOfPages = 1;
        
        Thread.sleep(2500);
        HibernateAddRecords hibernateConnection = new HibernateAddRecords();
                            hibernateConnection.init();
        
        for (int current_page = 1; current_page == 1 || current_page < noOfPages; current_page++) {
            doc = Jsoup.connect("https://www.novatech.co.uk/search.html?s=" + itemName + "&pg=" + current_page).get();
            
            //Get all product details
            Elements itemContainer = doc.select("div.row");
            
            //Get a single product details
            Elements productDetails = itemContainer.select("div.search-box-liner.search-box-results.search-hover");
            
            Elements productTitle = productDetails.select("div.search-box-title");
            
            Elements productPrice = productDetails.select("p.newspec-price");
            
            Elements productLink = productDetails.select("div.search-box-title");
                     productLink = productLink.select("a");
                     
            Elements imgElement = productDetails.select("div.col-md-4.col-sm-4.mobile-bottom-padding");
                     imgElement = imgElement.select("img");
            
            for (int i = 0; i < productDetails.size(); ++i) {
                
            //Get the brand
            Elements manufacturerEl = productTitle.get(i).select("div.search-box-title");
            String manufacturer = manufacturerEl.text();
                   manufacturer = manufacturer.substring(0, manufacturer.indexOf(" "));
                   manufacturer = manufacturer.replace(" ", "");
                   manufacturer = manufacturer.toUpperCase();
                   if("STARTECH.COM".equals(manufacturer) || "STARTECH.COM ".equals(manufacturer) || " STARTECH.COM".equals(manufacturer) )
                            manufacturer = manufacturer.replace("STARTECH.COM", "STARTECH");
                   
            //Get model
            Elements modelEl = productTitle.get(i).select("div.search-box-title");
            String model = modelEl.text();
                   model = model.replace(manufacturer, "");       
                   
            //Get price
            Elements priceEl = productPrice.get(i).select("p.newspec-price");
            String  priceStr = priceEl.text(); 
                    priceStr = priceStr.replace("£", "");
                    priceStr = priceStr.replace(" inc vat", "");
                    
            double price = Double.parseDouble(priceStr);      
                  
            //Get url link
            String productUrl = productLink.get(i).attr("abs:href");
            
            String imgLink = imgElement.get(i).attr("abs:src");
            
            hibernateConnection.addRecord(productUrl, manufacturer, model, price, imgLink);
            }
        Thread.sleep(2000);
        }
    } 
    /**@param name set name of the product, which will be scraped*/
    public void setProductName(String name){
       this.productName = name;
       run();
    }
    
    public String getProductName(){
       return this.productName;
    }
}
