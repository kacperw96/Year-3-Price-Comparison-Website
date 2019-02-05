package webscraping;
import webscraping.Hibernate.*;


//JUnit 5 imports
import org.hibernate.SessionFactory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;



@DisplayName("Test set name")
public class TestingClass {
    @BeforeAll
    static void initAll() {
    }

    @BeforeEach
    void init() {
    }
    
    @Test
    @DisplayName("Test get scraping beans")
    void testGetBean() {
        ApplicationContext factory = new AnnotationConfigApplicationContext(AppConfig.class);
        try{
            EbuyerScraper       t1 = factory.getBean(EbuyerScraper.class);
            OverclockersScraper t2 = factory.getBean(OverclockersScraper.class);
            ScanScraper         t3 = factory.getBean(ScanScraper.class);
            NovatechScraper     t4 = factory.getBean(NovatechScraper.class);
            NeweggScraper       t5 = factory.getBean(NeweggScraper.class);
        }
        catch(Exception ex){
            fail("Failed to get all scraping beans. Exception thrown: " + ex.getMessage());
        }
    }  
    
    @Test
    @DisplayName("Test set name for NovatechScraper class")
    void testSetNameNovotech() {
        //Create an instance of the class that we want to test
        NovatechScraper testNovatech= new NovatechScraper();
        try{
            testNovatech.setProductName("Nvidia GTX 1070");
        }
        catch(Exception ex){
            fail("Failed to set product name. Exception thrown: " + ex.getMessage());
        }
        
        assertEquals(testNovatech.getProductName(), "Nvidia GTX 1070");
    }  
    
    @Test
    @DisplayName("Test hibernate connection")
    void testConnection() {
        //Create an instance of the class that we want to test
        HibernateAddRecords testClass= new HibernateAddRecords();
        Boolean result = false;
        try{
          SessionFactory session =  testClass.init();
          if(session != null){
              result = true;
          }
        }
        catch(Exception ex){
            fail("Failed to create hibernate connection. Exception thrown: " + ex.getMessage());
        }
        assertEquals(result, true);
    }
    
    
    @Test
    @DisplayName("Test setting Brand name")
    void testAddRecords() {
        Brands testBrand= new Brands();
        try{
            testBrand.setManufacturer("test");
        }
        catch(Exception ex){
            fail("Failed to close the connection. Exception thrown: " + ex.getMessage());
        }
        String result = testBrand.getManufacturer();
        assertEquals(result, "test");
    }
    

    @Test
    @DisplayName("Test hibernate close connection")
    void testCloseConnection() {
        //Create an instance of the class that we want to test
        HibernateAddRecords testClass= new HibernateAddRecords();
        Boolean result = false;
        try{
          SessionFactory session =  testClass.init();
          if(session == null){
              result = false;
          }else{
              result = true;
          }
        }
        catch(Exception ex){
            fail("Failed to close the connection. Exception thrown: " + ex.getMessage());
        }
        assertEquals(result, true);
    }
    
}
