package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import pro.sky.telegrambot.service.CommandProcessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CommandProcessorTest {

    @Mock
    TelegramBot telegramBotMock;

    @InjectMocks
    CommandProcessor commandProcessorMocked;

//    private final CommandProcessor processor;
//
//    CommandProcessorTest(CommandProcessor processor){
//        this.processor=processor;
//    }

    @Test
    void considerCommandTest() {
        assertEquals(CommandProcessor.Command.UNCLEAR, CommandProcessor.considerCommand("it is not a valid bot command"));
        assertEquals(CommandProcessor.Command.START, CommandProcessor.considerCommand("/start"));
        assertEquals(CommandProcessor.Command.HELP, CommandProcessor.considerCommand("/help"));
        assertEquals(CommandProcessor.Command.VIEW, CommandProcessor.considerCommand("/view"));
        assertEquals(CommandProcessor.Command.DELETE, CommandProcessor.considerCommand("/delete"));
        assertEquals(CommandProcessor.Command.SET,
                CommandProcessor.considerCommand("/set 13.10.2023 11:15 Checking /set command"));
        assertEquals(CommandProcessor.Command.SET_IMPLICIT,
                CommandProcessor.considerCommand("13.10.2023 11:15 Checking SET IMPLICIT command"));
        assertEquals(CommandProcessor.Command.HELP, CommandProcessor.considerCommand("/help"));
    }

}
