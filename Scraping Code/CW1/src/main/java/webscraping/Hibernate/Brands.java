package webscraping.Hibernate;
import javax.persistence.*;

@Entity
@Table(name = "brands")
public class Brands {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "manufacturer")
    private String manufacturer;
    
    /** Empty constructor */
    public Brands(){ }

    //------------------- Getters and setters ------------------- 
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getManufacturer() {
        return manufacturer;
    }
}
