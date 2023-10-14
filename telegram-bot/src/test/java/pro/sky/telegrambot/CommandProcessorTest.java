package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.CommandProcessor;
import pro.sky.telegrambot.utils.ReplyText;

//import static jdk.internal.joptsimple.internal.Messages.message;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pro.sky.telegrambot.utils.ReplyText.*;

//@SpringBootTest(classes = TelegramBotApplication.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CommandProcessorTest {


    final private Logger logger = LoggerFactory.getLogger("CommandProcessorTest");

    @Mock
    private TelegramBot botMock;

    @Spy
    private NotificationTaskRepository notificationTaskRepositoryMock;

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
    void findNotificationTasksTest() {
        List<NotificationTask> expectedList = new ArrayList<>();
        expectedList.add(new NotificationTask(1L, 1L, "202314101000", "Notification1"));
        expectedList.add(new NotificationTask(2L, 2L, "202314101000", "Notification2"));
        expectedList.add(new NotificationTask(1L, 1L, "202314101000", "Notification3"));

        String moment = "202314101000";

        when(notificationTaskRepositoryMock.findAllByTimeToNotify(moment)).thenReturn(expectedList);

        assertEquals(expectedList, commandProcessorMocked.findNotificationTasks(moment));

    }

    @Test
    void doAndPrepareReplyWhenUnclear() {
        assertEquals(unclearText, commandProcessorMocked.doAndPrepareReply("unclear input", 1111L));
    }

    @Test
    void doAndPrepareReplyWhenStart() {
        assertEquals(startText, commandProcessorMocked.doAndPrepareReply("/start", 1111L));
    }

    @Test
    void doAndPrepareReplyWhenHelp() {
        assertEquals(helpText, commandProcessorMocked.doAndPrepareReply("/help", 1111L));
    }


}
