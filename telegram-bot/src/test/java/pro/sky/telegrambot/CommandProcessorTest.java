package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.SpyBeans;
import org.springframework.context.annotation.Bean;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.CommandProcessor;

//import static jdk.internal.joptsimple.internal.Messages.message;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@SpringBootTest(classes = TelegramBotApplication.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CommandProcessorTest {


    final private Logger logger = LoggerFactory.getLogger("CommandProcessorTest");

    @Mock
    private TelegramBot botMock;

    @Mock
    private NotificationTaskRepository repository;

    @InjectMocks
    private CommandProcessor commandProcessorMocked;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void considerCommandTest() {
        assertEquals(CommandProcessor.Command.UNCLEAR, commandProcessorMocked.considerCommand("it is not a valid bot command"));
        assertEquals(CommandProcessor.Command.START, commandProcessorMocked.considerCommand("/start"));
        assertEquals(CommandProcessor.Command.HELP, commandProcessorMocked.considerCommand("/help"));
        assertEquals(CommandProcessor.Command.VIEW, commandProcessorMocked.considerCommand("/view"));
        assertEquals(CommandProcessor.Command.DELETE, commandProcessorMocked.considerCommand("/delete"));
        assertEquals(CommandProcessor.Command.SET,
                commandProcessorMocked.considerCommand("/set 13.10.2023 11:15 Checking /set command"));
        assertEquals(CommandProcessor.Command.SET_IMPLICIT,
                commandProcessorMocked.considerCommand("13.10.2023 11:15 Checking SET IMPLICIT command"));
        assertEquals(CommandProcessor.Command.HELP, commandProcessorMocked.considerCommand("/help"));
    }

    @Test
    void MakeSureCommandProcessorMockedIsNotNull() {
        assertNotEquals(null, commandProcessorMocked);
    }

    @Test
    void sendMessageTest() {

        logger.info("botMock is not null: " + !(botMock == null));
        String expectedText = "It is OK";

        Long chatId = 1111L;
        SendMessage testSendMessage = new SendMessage(chatId, expectedText);
        Boolean expected = new Boolean(true);

        verify(botMock, times(0)).execute(any());

    }



}
