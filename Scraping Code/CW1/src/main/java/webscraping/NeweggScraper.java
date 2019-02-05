package webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webscraping.Hibernate.HibernateAddRecords;

/**
 * NeweggScraper class scrapes data from www.newegg.com website
 */
public class NeweggScraper extends Thread {
    /**
     * @param productName is holding the name of the product we want to scrape
     */
    private String productName;
    @Override
    public void run() {
        try {
            scrapeNewegg();
        } catch (Exception e) {
            System.out.println("Exception in Novotech Scraper caught");
            e.printStackTrace();
        }
    }

    /**
     * Method scraping websites content
     */
    void scrapeNewegg() throws Exception {

        //Name of searched product
        String itemName = this.productName;

        //Download HTML document from website
        Document doc = Jsoup.connect("https://www.newegg.com/global/uk-en/Product/ProductList.aspx?Submit=ENE&N=-1&IsNodeId=1&Description=" + itemName + "&bop=And&Page=1&PageSize=96&order=BESTMATCH").get();

        //Number of pages
        Elements sitePages = doc.select("div.list-tool-pagination");
        Elements tmpNoOfPages = sitePages.select("span.list-tool-pagination-text");
        String pagesStr = tmpNoOfPages.text();
               pagesStr = pagesStr.replace("Page 1/", "");
        int noOfPages = Integer.parseInt(pagesStr);

        Thread.sleep(3000);
        HibernateAddRecords hibernateConnection = new HibernateAddRecords();
                            hibernateConnection.init();
        
        for (int currentPage = 1; currentPage == 1 || currentPage < noOfPages; currentPage++) {
            doc = Jsoup.connect("https://www.newegg.com/global/uk-en/Product/ProductList.aspx?Submit=ENE&N=-1&IsNodeId=1&Description=" + itemName + "&bop=And&Page=" + currentPage + "&PageSize=36&order=BESTMATCH").get();

            //Get all details
            Elements itemContainer = doc.select("div.list-wrap");

            //Get products details
            Elements productDetails = itemContainer.select("div.item-container");

            //Get products title
            Elements productTitle = productDetails.select("a.item-title");

            //Get products price
            Elements productPrice = productDetails.select("li.price-current");

            //Get products link
            Elements productLink = productTitle.select("div.item-info");
                     productLink = productTitle.select("a.item-title");
                     
            //Get products image
            Elements productImgEl = productDetails.select("a.item-img");
                     productImgEl = productDetails.select("img");

            for (int i = 0; i < productDetails.size(); ++i) {
                /**
                   *@param manufacturer stores information about manufacturer
                   */
                Elements manufacturerEl = productTitle.get(i).select("a.item-title");
                String manufacturer= manufacturerEl.text();
                       manufacturer = manufacturer.substring(0, manufacturer.indexOf(" "));
                       manufacturer = manufacturer.replace(" ", "");
                       manufacturer = manufacturer.toUpperCase();
                       if("STARTECH.COM".equals(manufacturer) || "STARTECH.COM ".equals(manufacturer) || " STARTECH.COM".equals(manufacturer) )
                            manufacturer = manufacturer.replace("STARTECH.COM", "STARTECH");

                /**
                   *@param model stores information about model
                   */
                Elements modelEl = productTitle.get(i).select("a.item-title");
                String model = modelEl.text();
                       model = model.replace(manufacturer, "");

                /**
                   *@param price stores price value of product
                   */
                Elements price = productPrice.get(i).select("li.price-current");
                Elements pounds = productPrice.get(i).select("strong");
                Elements pence = productPrice.get(i).select("sup");
                String fPrice = pounds.text() + pence.text();
                       fPrice = fPrice.replace(",", "");
                
                double finalPrice = Double.parseDouble(fPrice);

                /**
                   *@param url stores redirecting link to the product
                   */
                String url = productLink.get(i).attr("abs:href");
                
                String imgUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/600px-No_image_available.svg.png";
                
                /**
                   *Send scraped data to the method, which will save it in database
                   */
                hibernateConnection.addRecord(url, manufacturer, model, finalPrice, imgUrl);
            
            }
        Thread.sleep(2000);
        }
    }
    
    /**
     *@param name set name of the product, which will be scraped
     */
    public void setProductName(String name){
       this.productName = name;
       run();
    }    
}
