package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Noty;
import pro.sky.telegrambot.repository.NotyRepository;
import pro.sky.telegrambot.service.CommandProcessor;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotyRepository noties;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    final private TelegramBot telegramBot;

    final private CommandProcessor commandProcessor;

    public TelegramBotUpdatesListener(NotyRepository noties, TelegramBot telegramBot, CommandProcessor commandProcessor) {
        this.noties = noties;
        this.telegramBot = telegramBot;
        this.commandProcessor = commandProcessor;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {

        updates.forEach(update -> {

            // Process your updates here
            logger.info("Processing update: {}", update);

            long chatId = update.message().chat().id();
            String userInput = update.message().text();
            logger.info("User has just replied us with command: " + CommandProcessor.considerCommand(userInput).toString());
            commandProcessor.parseAndDo(chatId, userInput);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
