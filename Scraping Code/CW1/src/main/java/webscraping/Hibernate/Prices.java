package webscraping.Hibernate;
import javax.persistence.*;

@Entity
@Table(name="prices")
public class Prices {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    
    @Column(name = "price")
    double price;
    
    @ManyToOne()
    @JoinColumn(name="website_id", referencedColumnName="id", nullable=false)
    private Websites website;
    
    /** Empty constructor */
    public Prices(){ }
    
    
   //Getters and setters
    public void setId(int id) {
        this.id = id;    
    }
    public int getId() {    
        return id;
    }
    
    public void setPrice(double price) {
        this.price = price;    
    }
    public double getPrice() {    
        return price;
    }
    
    public Websites getWebsite() {
        return website;
    }

    public void setWebsite(Websites website) {
        this.website = website;
    }
}
