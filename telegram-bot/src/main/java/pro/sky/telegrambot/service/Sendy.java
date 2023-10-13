package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Service;

@Service
public class Sendy {

    private final TelegramBot telegramBot;

    public Sendy(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public boolean botExecute(long userChatId, String messageText) {
        SendMessage message = new SendMessage(userChatId, messageText);
        SendResponse response = telegramBot.execute(message);
        return response.isOk();
    }

}
