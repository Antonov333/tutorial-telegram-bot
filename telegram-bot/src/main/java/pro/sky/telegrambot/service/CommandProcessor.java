package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommandProcessor {

    private final NotificationTaskRepository noties;

    private final TelegramBot telegramBot;

    static Logger logger = LoggerFactory.getLogger("CommandProcessor Logger");

    public CommandProcessor(NotificationTaskRepository noties, TelegramBot telegramBot) {
        this.noties = noties;
        this.telegramBot = telegramBot;
    }

    public enum Command {START, HELP, VIEW, SET, SET_IMPLICIT, EDIT, DELETE, UNCLEAR}

    ;

    public void parseAndDo(long chatId, String userInput) {

        String helpText = "/help : get command list\n"
                + "send a string like to set up a notification";
        String startText = "Hi! ) \n"
                + "You can use this bot to set up and receive notifications regarding your events and things to do\n"
                + helpText;

        String defaultText = "Please use commands as follows\n" + helpText;

        boolean checkMessageSent;


        switch (considerCommand(userInput)) {
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
                if (userInput.length() > 5) {
                    userInput = userInput.substring(5);
                }
                if (!setImplicitRecognize(userInput).isSetImplicit()) {
                    sendMessage(chatId, "wrong data provided with /set command");
                    break;
                }
            }

            case SET_IMPLICIT: {
                NotificationTask n = new NotificationTask(0L,
                        chatId,
                        takeTiming(setImplicitRecognize(userInput)),
                        setImplicitRecognize(userInput).getTaskDescription());
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
        }
    }

    public static Command considerCommand(String userInput) {

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
    }

    public void sendMessage(long userChatId, String messageText) {
        SendMessage message = new SendMessage(userChatId, messageText);
        telegramBot.execute(message);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotifications() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String moment = localDateTime.toString();
        Boolean makeSureSentOk;
        moment = moment.replaceAll("[-:T]", "").substring(0, 12);
        logger.info("moment = " + moment);
        List<NotificationTask> thisMinuteNoties = noties.findAllByTimeToNotify(moment);
        logger.info(thisMinuteNoties.toString());

        thisMinuteNoties.forEach(notificationTask -> {
            logger.info(notificationTask.toString());
            sendMessage(notificationTask.getChatId(), notificationTask.getContent());
        });
    }

    public void logItsOk() {
        logger.info("it is OK");
    }

    private static SetImplicitRecognition setImplicitRecognize(String userInput) {

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

}
