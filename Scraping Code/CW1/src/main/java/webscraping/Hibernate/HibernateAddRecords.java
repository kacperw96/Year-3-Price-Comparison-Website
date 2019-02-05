package webscraping.Hibernate;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import webscraping.AppConfig;

/** 
 * Adds and modifies data in a table with foreign keys. 
 */
public class HibernateAddRecords {
    int counter = 0;
    ApplicationContext factory = new AnnotationConfigApplicationContext(AppConfig.class);
    //Creates new Sessions when we need to interact with the database 
    SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();     
    
    /** Sets up the SessionFactory. Call this method first.
     * @return  */
    public SessionFactory init(){
        if(sessionFactory == null){//Build sessionFatory once only
            try {
                //Create a builder for the standard service registry
                StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();

                //Load configuration from hibernate configuration file.
                //Here we are using a configuration file that specifies Java annotations.
                standardServiceRegistryBuilder.configure("resources/hibernate.cfg.xml"); 

                //Create the registry that will be used to build the session factory
                StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
                try {
                    //Create the session factory - this is the goal of the init method.
                    sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
                }
                catch (Exception e) {
                        /* The registry would be destroyed by the SessionFactory, 
                            but we had trouble building the SessionFactory, so destroy it manually */
                        System.err.println("Session Factory build failed.");
                        e.printStackTrace();
                        StandardServiceRegistryBuilder.destroy( registry );
                }
                //Ouput result
                System.out.println("Session factory built.");
            }
            catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("SessionFactory creation failed." + ex);
                ex.printStackTrace();
            }
        }
        return sessionFactory;
    }

    /** 
     * Adds records to database. 
     * @param url Link to the product
     * @param manufacturer Product manufacturer name
     * @param modelName Product model name
     * @param productPrice Product price
     * @param imgUrl Link to the product picture
     */
public void addRecord(String url, String manufacturer, String modelName, double productPrice, String imgUrl){
        int websiteId, brandId, modelId, productId, priceId;

        Session session = sessionFactory.getCurrentSession();
        //Start transaction
        session.beginTransaction(); 
        
        //-------------------------- Brands --------------------------
        Brands brand = factory.getBean(Brands.class);
        List<Integer> brandsList = session.createQuery("select id from Brands where UPPER(manufacturer)='" + manufacturer.toUpperCase()+ "'").getResultList();
        if(!brandsList.isEmpty()){
            brandId = brandsList.get(0);
            brand.setId(brandId);
            brand.setManufacturer(manufacturer);
            session.update(brand);
        } else { 
            brand.setManufacturer(manufacturer);
            session.save(brand);
        }
        
        
        //-------------------------- Models --------------------------
        Models model = factory.getBean(Models.class);
        List<Integer> modelsList = session.createQuery("select id from Models where lower(name)='" + modelName.toLowerCase() + "'").getResultList();
        if(!modelsList.isEmpty()){
            modelId = modelsList.get(0);
            model.setId(modelId);
            model.setName(modelName);
            model.setBrandId(brand);
            model.setFullName(manufacturer + " " + modelName);
            session.update(model);
        } else { 
            model.setName(modelName);
            model.setBrandId(brand);
            model.setFullName(manufacturer + " " + modelName);
            session.save(model);
        }
        
        
        //-------------------------- Websites --------------------------
        Websites website = factory.getBean(Websites.class);
        List<Integer> websiteList = session.createQuery("select id from Websites where url='" + url + "'").getResultList();
        if(!websiteList.isEmpty()){
            websiteId = websiteList.get(0);
            website.setId(websiteId);
            website.setProduct_url(url);
            website.setImg(imgUrl);
            website.setModel(model);
            session.update(website);
        } else { 
            website.setProduct_url(url);
            website.setImg(imgUrl);
            website.setModel(model);
            session.save(website);
        }
        
        //-------------------------- Prices --------------------------
        Prices price = factory.getBean(Prices.class);
        websiteList = session.createQuery("select id from Websites where url='" + url + "'").getResultList();
        websiteId = websiteList.get(0);
        
        List<Integer> priceList = session.createQuery("select id from Prices where website_id='" + websiteId + "'").getResultList();
        if(!priceList.isEmpty()){
            priceId = priceList.get(0);
            price.setId(priceId);
            price.setPrice(productPrice);
            price.setWebsite(website);
            session.update(price);
        } else { 
            price.setPrice(productPrice);
            price.setWebsite(website);
            session.save(price);
        }
        

        //Commit transaction to save it to database
        session.getTransaction().commit();
        
        //Close the session and release database connection
        System.out.println("Record added to database");
    }

    /** 
     * Close SessionFactory  
     */
    public void closeSession(){
        //Session session = sessionFactory.getCurrentSession();
        SessionFactory session = init();
                       session.close();
    }
}   




