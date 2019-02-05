package webscraping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import webscraping.Hibernate.*;

@Configuration
public class AppConfig {
    
    @Bean
    public EbuyerScraper getEbuyerScraper(){
        return new EbuyerScraper();
    }
    
    @Bean
    public OverclockersScraper getOverclockersScraper(){
        return new OverclockersScraper();
    }
    
    @Bean
    public ScanScraper getScanScraper(){
        return new ScanScraper();
    }
    
    @Bean
    public NovatechScraper getNovatechScraper(){
        return new NovatechScraper();
    }
    
    @Bean
    public NeweggScraper getNeweggScraper(){
        return new NeweggScraper();
    }
    
    @Bean
    public Brands getBrand(){
        return new Brands();
    }
    
    @Bean
    public Models getModel(){
        return new Models();
    }
    
    @Bean
    public Prices getPrice(){
        return new Prices();
    }
    
    @Bean
    public Websites getWebsite(){
        return new Websites();
    }
}