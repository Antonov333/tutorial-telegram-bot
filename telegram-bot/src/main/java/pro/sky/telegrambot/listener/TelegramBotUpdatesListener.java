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

        /*String helpText = "/help : get command list\n"
                + "send a string like to set up a notification";
        String startText = "Hi! ) \n"
                + "You can use this bot to set up and receive notifications regarding your events and things to do\n"
                + helpText;

        String defaultText = "Please use commands as follows\n" + helpText;*/

        updates.forEach(update -> {

            // Process your updates here
            logger.info("Processing update: {}", update);

            long chatId = update.message().chat().id();
            String userInput = update.message().text();
            logger.info("User has just replied us with command: " + CommandProcessor.considerCommand(userInput).toString());
            commandProcessor.parseAndDo(chatId, userInput);
            /*switch (CommandProcessor.considerCommand(string)) {
                case START: {
                    sendMessage(chatId, startText);
                    break;
                }
                case HELP: {
                    sendMessage(chatId, helpText);
                    break;
                }
                case VIEW: {
                    sendMessage(chatId, noties.findAllByChatId(chatId).toString());
                    break;
                }

                case SET: {
                    if (string.length() > 5) {
                        string = string.substring(5);
                    }
                    if (!setImplicitRecognize(string).isSetImplicit()) {
                        sendMessage(chatId, "wrong data provided with /set command");
                        break;
                    }
                }

                case SET_IMPLICIT: {
                    Noty n = new Noty(0L,
                            chatId,
                            takeTiming(setImplicitRecognize(string)),
                            setImplicitRecognize(string).getTaskDescription());
                    logger.info("Set command received with data as follows: " + n.toString());
                    n = noties.save(n);
                    sendMessage(chatId, "Notification regarding: " + n.getContent()
                            + " appointed at " + n.getTimeToNotify());
                    break;
                }

                case DELETE: {
                    sendMessage(chatId, "Command DELETE is under construction yet, sorry...");
                    break;
                }

                default: {
                    sendMessage(chatId, defaultText);
                    break;
                }
            }*/

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public String sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage(chatId, messageText);
        SendResponse response = telegramBot.execute(message);
        return response.message().text();
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotifications() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String moment = localDateTime.toString();
        Boolean makeSureSentOk;
        moment = moment.replaceAll("[-:T]", "").substring(0, 12);
        logger.info("moment = " + moment);
        List<Noty> thisMinuteNoties = noties.findAllByTimeToNotify(moment);
        logger.info(thisMinuteNoties.toString());

        thisMinuteNoties.forEach(noty -> {
            logger.info(noty.toString());
            sendMessage(noty.getChatId(), noty.getContent());
        });
    }

    /*private Command considerCommand(String userInput) {

        if (userInput == null) {
            return Command.UNCLEAR;
        }

        if (userInput.isEmpty()) {
            return Command.UNCLEAR;
        }

        String input = userInput.toLowerCase();

        logger.info("userInput : " + input);

        if (input.startsWith("/start")) {
            return Command.START;
        }

        if (input.startsWith("/help")) {
            return Command.HELP;
        }
        if (input.startsWith("/view")) {
            return Command.VIEW;
        }
        if (input.startsWith("/set")) {
            return Command.SET;
        }

        if (input.startsWith("/delete")) {
            return Command.DELETE;
        }

        if (setImplicitRecognize(input).isSetImplicit) {
            return Command.SET_IMPLICIT;
        }

        return Command.UNCLEAR;
    }*/

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

    private String takeTiming(SetImplicitRecognition setCommandContent) {
        String dString = setCommandContent.getDateString().replace(".", "");
        String dd = dString.substring(0, 2);
        String mm = dString.substring(2, 4);
        String yyyy = dString.substring(4, 8);
        dString = yyyy + mm + dd;

        String tString = setCommandContent.getTimeString().replace(":", "");

        return dString.concat(tString);
    }


}
