package pro.sky.telegrambot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.Noty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Repository
public interface NotyRepository extends JpaRepository<Noty, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM noty as n WHERE n.chat_Id= :chatId")
    List<Noty> findAllByChatId(@Param("chatId") long chatId);
}
