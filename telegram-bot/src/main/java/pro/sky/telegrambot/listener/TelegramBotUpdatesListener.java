package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Noty;
import pro.sky.telegrambot.repository.NotyRepository;

import javax.annotation.PostConstruct;
import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotyRepository noties;

    private enum Command {START, HELP, VIEW, SET, UNCLEAR}

    ;

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotyRepository noties, TelegramBot telegramBot) {
        this.noties = noties;
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        String userInput;
        updates.forEach(update -> {
            String helpText = "/help : get command list\n"
                    + "send a string like to set up a notification";
            String startText = "Hi! ) \n"
                    + "You can use this bot to set up and receive notifications regarding your events and things to do\n"
                    + helpText;

            String defaultText = "Please use commands as follows\n" + helpText;
            // Process your updates here
            logger.info("Processing update: {}", update);

            long chatId = update.message().chat().id();
            System.out.println("noties.findAll() = " + noties.findAll());
            switch (update.message().text()) {
                case "/start": {
                    noties.save(new Noty(0L, chatId, "/start command received"));
                    sendMessage(chatId, startText);
                    break;
                }
                case "/help": {
                    noties.save(new Noty(0L, chatId, "/help command received"));
                    sendMessage(chatId, helpText);
                    break;
                }
                case "/view": {
                    sendMessage(chatId, noties.findAllByChatId(chatId).toString());
                    break;
                }
                default: {
                    noties.save(new Noty(1L, chatId, "some input received"));
                    sendMessage(chatId, defaultText);
                    break;
                }
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        telegramBot.execute(message);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void tryScheduled() {
        logger.info("tryScheduled()");
    }

    private List<String> userInputParsing(String userInput) {
        // (([01][0-9])|(2[0-3])):[0-5][0-9] pattern for hours in 24h format (00 - 23)and minutes (00 - 59)
        // separated by colon

        Pattern pattern = Pattern.compile("(([01][0-9])|(2[0-3])):[0-5][0-9]");
        return null;
    }

}
