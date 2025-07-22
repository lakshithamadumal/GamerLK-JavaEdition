package hibernate;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Laky
 */
@Entity
@Table(name = "product")
public class Product implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "price", nullable = false)
    double price;

    @Column(name = "game_link", nullable = false)
    String game_link;

    @Column(name = "game_size", nullable = false)
    double game_size;

    @Column(name = "release_date", nullable = false)
    Date release_date;

    @Column(name = "offer", nullable = false)
    int offer;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category_id;

    @ManyToOne
    @JoinColumn(name = "mode_id")
    Mode mode_id;

    @Column(name = "tag", length = 20, nullable = false)
    String tag;

    @ManyToOne
    @JoinColumn(name = "developer_id")
    Developer developer_id;

    @ManyToOne
    @JoinColumn(name = "status_id")
    Status status_id;

    @ManyToOne
    @JoinColumn(name = "min_requirement_id")
    Requirement min_requirement_id;

    @ManyToOne
    @JoinColumn(name = "rec_requirement_id")
    Requirement rec_requirement_id;

    @Column(name = "created_at", nullable = false)
    Date created_at;

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGame_link() {
        return game_link;
    }

    public void setGame_link(String game_link) {
        this.game_link = game_link;
    }

    public double getGame_size() {
        return game_size;
    }

    public void setGame_size(double game_size) {
        this.game_size = game_size;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }

    public int getOffer() {
        return offer;
    }

    public void setOffer(int offer) {
        this.offer = offer;
    }

    public Category getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Category category_id) {
        this.category_id = category_id;
    }

    public Mode getMode_id() {
        return mode_id;
    }

    public void setMode_id(Mode mode_id) {
        this.mode_id = mode_id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Developer getDeveloper_id() {
        return developer_id;
    }

    public void setDeveloper_id(Developer developer_id) {
        this.developer_id = developer_id;
    }

    public Status getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Status status_id) {
        this.status_id = status_id;
    }

    public Requirement getMin_requirement_id() {
        return min_requirement_id;
    }

    public void setMin_requirement_id(Requirement min_requirement_id) {
        this.min_requirement_id = min_requirement_id;
    }

    public Requirement getRec_requirement_id() {
        return rec_requirement_id;
    }

    public void setRec_requirement_id(Requirement rec_requirement_id) {
        this.rec_requirement_id = rec_requirement_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

}
