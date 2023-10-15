package pro.sky.telegrambot.utils;

public class ReplyText {

    public static String helpText = "/help : get command list\n"
            + "send a string like to set up a notification";
    public static String startText = "Hi! ) \n"
            + "You can use this bot to set up and receive notifications regarding your events and things to do\n"
            + helpText;

    public static String defaultText = "Please use commands as follows\n" + helpText;

    public static String unclearText = defaultText;

    public static String setWrongText = "Wrong data provided with /set command";

    public static String setText1 = "Notification regarding: ";

    public static String setText2 = " appointed at ";

    public static String deleteUnderConstructionText = "Command DELETE is under construction yet, sorry...";

    public static String editUnderConstructionText = "Command EDIT is under construction yet, sorry...";

}
