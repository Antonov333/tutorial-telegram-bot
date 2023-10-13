package pro.sky.telegrambot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.sky.telegrambot.service.CommandProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CommandProcessorTest {
    @Test
    void considerCommandTest() {
        assertEquals(CommandProcessor.Command.UNCLEAR, CommandProcessor.considerCommand("unclear"));
    }
}
