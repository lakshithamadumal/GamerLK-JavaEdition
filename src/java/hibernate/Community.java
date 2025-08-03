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
@Table(name = "community_chat")
public class Community implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "message", nullable = false)
    String message;

    @Column(name = "sent_at", nullable = false)
    Date sent_at;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user_id;

    public Community() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSent_at() {
        return sent_at;
    }

    public void setSent_at(Date sent_at) {
        this.sent_at = sent_at;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

}
