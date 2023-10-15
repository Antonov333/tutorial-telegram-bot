package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TelegramBotApplication.class)
class TelegramBotApplicationTests {

    TelegramBot telegramBot;

    @Test
    void contextLoads() {
    }

}
