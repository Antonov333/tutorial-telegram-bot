package pro.sky.telegrambot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.NotificationTask;

import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM noty as n WHERE n.chat_Id= :chatId")
    List<NotificationTask> findAllByChatId(@Param("chatId") long chatId);

    @Query(nativeQuery = true, value = "SELECT * FROM noty as n WHERE n.time_to_notify= :time")
    List<NotificationTask> findAllByTimeToNotify(@Param("time") String time);
}
