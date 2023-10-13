package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.sky.telegrambot.service.CommandProcessor;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TelegramBotApplicationTests {

    TelegramBot telegramBot;

    @Test
    void contextLoads() {
    }

}
