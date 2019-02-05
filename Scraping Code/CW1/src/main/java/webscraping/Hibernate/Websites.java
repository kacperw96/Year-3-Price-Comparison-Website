package webscraping.Hibernate;
import javax.persistence.*;

/**
 *
 * @author Kacper
 */

@Entity
@Table(name = "websites")
public class Websites{ 
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "url")
    private String url;
    
    @ManyToOne()
    @JoinColumn(name="model_id", referencedColumnName="id", nullable=false)
    private Models model;
    
    @Column(name = "img")
    private String img;

    
    /** Empty constructor */
    public Websites(){ }
    
    //Getters and setters
    public void setId(int id) {
        this.id = id;    
    }
    public int getId() {    
        return id;
    }
      
    public void setProduct_url(String url) {
        this.url = url;    
    }
    public String getProduct_url() {    
        return url;
    }

    public Models getModel() {
        return model;
    }

    public void setModel(Models model) {
        this.model = model;
    }
    
     public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
    
}
