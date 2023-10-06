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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotyRepository noties;

    private enum Command {START, HELP, VIEW, SET, SET_IMPLICIT, EDIT, DELETE, UNCLEAR}

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
            String string = update.message().text();
            System.out.println("considerCommand(string) = " + considerCommand(string));
            switch (string) {
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

    private Command considerCommand(String userInput) {
        if (userInput == null) {
            return Command.UNCLEAR;
        }
        if (userInput.isEmpty()) {
            return Command.UNCLEAR;
        }
        String input = userInput.toLowerCase();

        if (input.startsWith("/start")) {
            return Command.START;
        }

        if (input.startsWith("/help")) {
            return Command.HELP;
        }
        if (input.startsWith("/view")) {
            return Command.VIEW;
        }
        if (input.startsWith("/set") & input.length() > 19) {
            return Command.SET;
        }

        if (input.startsWith("/delete")) {
            return Command.DELETE;
        }

        if (setImplicitRecognize(input).isSetImplicit) {
            return Command.SET_IMPLICIT;
        }

        return Command.UNCLEAR;
    }

    private static class SetImplicitRecognition {

        private boolean isSetImplicit;
        private String dateString;
        private String timeString;
        private String taskDescription;

        SetImplicitRecognition() {
            isSetImplicit = false;

        }

        public boolean isSetImplicit() {
            return isSetImplicit;
        }

        public void setSetImplicit(boolean setImplicit) {
            isSetImplicit = setImplicit;
        }

        public String getDateString() {
            return dateString;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }

        public String getTimeString() {
            return timeString;
        }

        public void setTimeString(String timeString) {
            this.timeString = timeString;
        }

        public String getTaskDescription() {
            return taskDescription;
        }

        public void setTaskDescription(String taskDescription) {
            this.taskDescription = taskDescription;
        }

        @Override
        public String toString() {
            return "SetImplicitRecognition{" +
                    "isSetImplicit=" + isSetImplicit +
                    ", dateString='" + dateString + '\'' +
                    ", timeString='" + timeString + '\'' +
                    ", taskDescription='" + taskDescription + '\'' +
                    '}';
        }
    }

    private SetImplicitRecognition setImplicitRecognize(String userInput) {

        SetImplicitRecognition result = new SetImplicitRecognition();

        if (userInput == null || (userInput.length() < 16)) {
            return result;
        }

        Pattern hoursAndMinutes = Pattern.compile("(([01][0-9])|(2[0-3])):[0-5][0-9]?");

        Pattern dayMonthYear = Pattern.compile(
                "(([3][01])|([0-2]\\d))\\.\\d\\d\\.2\\d{3}"
        );

        String userInputHead = userInput.substring(0, 16);
        String date = "";
        String time = "";

        Matcher matcher = hoursAndMinutes.matcher(userInputHead);
        Boolean hasTime = matcher.find();
        if (hasTime) {
            time = matcher.group();
        }

        matcher = dayMonthYear.matcher(userInputHead);
        Boolean hasDate = matcher.find();
        if (hasDate) {
            date = matcher.group();
        }
        ;

        if (hasTime & hasDate) {
            result.setSetImplicit(true);
            result.setDateString(date);
            result.setTimeString(time);
            result.setTaskDescription(userInput.substring(16));
        }
        return result;
    }
}
