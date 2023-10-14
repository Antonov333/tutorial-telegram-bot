package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.CommandProcessor;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotificationTaskRepository noties;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    final private TelegramBot telegramBot;

    final private CommandProcessor commandProcessor;

    public TelegramBotUpdatesListener(NotificationTaskRepository noties, TelegramBot telegramBot, CommandProcessor commandProcessor) {
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
            logger.info("User has just replied us with command: " + commandProcessor.considerCommand(userInput).toString());
            sendMessage(chatId, commandProcessor.doAndPrepareReply(userInput, chatId));
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendMessage(long userChatId, String messageText) {
        SendMessage message = new SendMessage(userChatId, messageText);
        telegramBot.execute(message);
    }


}
