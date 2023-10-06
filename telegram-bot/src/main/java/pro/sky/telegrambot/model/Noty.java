package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

// Noty is a model for notification
@Entity
public class Noty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long chatId;

    private String timeToNotify;

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getTimeToNotify() {
        return timeToNotify;
    }

    public void setTimeToNotify(String timeToNotify) {
        this.timeToNotify = timeToNotify;
    }

    private String content;

//    here must be field for scheduled time

// Whether more fields required?


    public Noty(Long id, Long chatId, String timeToNotify, String content) {
        this.id = id;
        this.chatId = chatId;
        this.timeToNotify = timeToNotify;
        this.content = content;
    }

    public Noty() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Noty)) return false;
        Noty noty = (Noty) o;
        return Objects.equals(getId(), noty.getId()) && Objects.equals(getChatId(), noty.getChatId()) && Objects.equals(getContent(), noty.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getChatId(), getContent());
    }

    @Override
    public String toString() {
        return "Noty{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", timeToNotify=" + timeToNotify +
                ", content='" + content + '\'' +
                '}';
    }
}
