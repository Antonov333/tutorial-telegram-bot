package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TelegramBotUpdatesListenerTest extends Mockito {

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private TelegramBotUpdatesListener updatesListener;

    @Test
    void sendMessageTest() {
        Long chatId = -1L;
        String testText = " - Test text - ";
        SendMessage message = new SendMessage(chatId, testText);

//        SendResponse response =

//    String actual = updatesListener.sendMessage()

    }
}
