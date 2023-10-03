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
    Long id;

    Long chatId;

    String content;

    public Noty(Long id, Long chatId, String content) {
        this.id = id;
        this.chatId = chatId;
        this.content = content;
    }

    public Noty(){}

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
                ", content='" + content + '\'' +
                '}';
    }
}
