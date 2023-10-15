package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.util.Objects;

// Noty is a model for notification
@Entity
@Table(name = "noty")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private long chatId;
    @Column(name = "time_to_notify")
    private String timeToNotify;
    @Column(name = "content")
    private String content;

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getTimeToNotify() {
        return timeToNotify;
    }

    public void setTimeToNotify(String timeToNotify) {
        this.timeToNotify = timeToNotify;
    }


    public NotificationTask(Long id, Long chatId, String timeToNotify, String content) {
        this.id = id;
        this.chatId = chatId;
        this.timeToNotify = timeToNotify;
        this.content = content;
    }

    public NotificationTask() {
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
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask notificationTask = (NotificationTask) o;
        return Objects.equals(getId(), notificationTask.getId()) && Objects.equals(getChatId(), notificationTask.getChatId()) && Objects.equals(getContent(), notificationTask.getContent());
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
