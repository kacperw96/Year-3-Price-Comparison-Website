package webscraping;

import javax.swing.JOptionPane;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import webscraping.Hibernate.HibernateAddRecords;


public class Main {
    public static void main(String[] args) {
        ApplicationContext factory = new AnnotationConfigApplicationContext(AppConfig.class);
        
        /** JOptionPane.showInputDialog allows user to enter product name to scrape, stores it in @param product variable, then pass it to the scraper classes. */
        String product = JOptionPane.showInputDialog(null, "What product are you looking for?");

        EbuyerScraper       t1 = factory.getBean(EbuyerScraper.class);
        OverclockersScraper t2 = factory.getBean(OverclockersScraper.class);
        ScanScraper         t3 = factory.getBean(ScanScraper.class);
        NovatechScraper     t4 = factory.getBean(NovatechScraper.class);
        NeweggScraper       t5 = factory.getBean(NeweggScraper.class);
        
        /** Start threads */
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        
        
        t1.setProductName(product);
        t2.setProductName(product);
        t3.setProductName(product);
        t4.setProductName(product);
        t5.setProductName(product);

        
        /** Close hibernate session */
        HibernateAddRecords hibernate = new HibernateAddRecords();
                            hibernate.closeSession();
        
        System.exit(0);
    }
}
