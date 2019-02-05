package webscraping.Hibernate;
import javax.persistence.*;

@Entity
@Table(name="models")
public class Models {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @ManyToOne()
    @JoinColumn(name="brand_id", referencedColumnName="id", nullable=false)
    private Brands brands;
    
    @Column(name = "full_name")
    private String fullName;
    
    /** Empty constructor */
    public Models(){ }
    
    
    //------------------- Getters and setters ------------------- 
    public void setId(int id) {
        this.id = id;    
    }
    public int getId() {    
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }  
    public String getName() {
        return name;
    }

    public Brands getBrandId() {
        return brands;
    }
    
    public void setBrandId(Brands brands) {
        this.brands = brands;
    }

    public Brands getBrands() {
        return brands;
    }

    public void setBrands(Brands brands) {
        this.brands = brands;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    
}
