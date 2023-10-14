package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.utils.ReplyText;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.sky.telegrambot.utils.ReplyText.defaultText;
import static pro.sky.telegrambot.utils.ReplyText.startText;

@Service
public class CommandProcessor {

    private final NotificationTaskRepository notificationTaskRepository;

    private final TelegramBot telegramBot;

    static Logger logger = LoggerFactory.getLogger("CommandProcessor Logger");

    public CommandProcessor(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }

    public enum Command {START, HELP, VIEW, SET, SET_IMPLICIT, EDIT, DELETE, UNCLEAR}

    ;


    public String doAndPrepareReply(String userInput, long chatId) {

        /*String helpText = "/help : get command list\n"
                + "send a string like to set up a notification";
        String startText = "Hi! ) \n"
                + "You can use this bot to set up and receive notifications regarding your events and things to do\n"
                + helpText;

        String defaultText = "Please use commands as follows\n" + helpText;*/

        switch (considerCommand(userInput)) {
            case START: {
                return startText;
            }
            case HELP: {
                return ReplyText.helpText;
            }
            case VIEW: {
                return notificationTaskRepository.findAllByChatId(chatId).toString();
            }

            case SET: {
                if (userInput.length() > 5) {
                    userInput = userInput.substring(5);
                }
                if (!setImplicitRecognize(userInput).isSetImplicit()) {
                    return "wrong data provided with /set command";
                }
            }

            case SET_IMPLICIT: {
                NotificationTask n = new NotificationTask(0L,
                        chatId,
                        takeTiming(setImplicitRecognize(userInput)),
                        setImplicitRecognize(userInput).getTaskDescription());
                logger.info("Set command received with data as follows: " + n.toString());
                n = notificationTaskRepository.save(n);
                return "Notification regarding: " + n.getContent()
                        + " appointed at " + n.getTimeToNotify();
            }

            case DELETE: {
                return "Command DELETE is under construction yet, sorry...";
            }

            case EDIT: {
                return "Command EDIT is under construction yet, sorry...";
            }

            case UNCLEAR:
            default: {
                return defaultText;
            }
        }

    }

    public Command considerCommand(String userInput) {

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

    public List<NotificationTask> findNotificationTasks(String moment) {
        logger.info("findNotificationTasks: moment = " + moment);
        List<NotificationTask> thisMinuteNotifications = notificationTaskRepository.findAllByTimeToNotify(moment);
        logger.info(thisMinuteNotifications.toString());
        return thisMinuteNotifications;
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
