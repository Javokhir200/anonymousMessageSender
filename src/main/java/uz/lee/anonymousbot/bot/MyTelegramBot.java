package uz.lee.anonymousbot.bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.lee.anonymousbot.service.impl.UserServiceImpl;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final UserServiceImpl userServiceImpl;

    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.token}")
    private String token;

    public MyTelegramBot(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String text = null;
        Long chatId = null;
        if (update.hasMessage()&&update.getMessage().hasText()) {
            text = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
        }
        if (text!=null && text.equals("/start")){
            execute(userServiceImpl.sendLinkToUser(chatId));
        }else if (text!=null && text.startsWith("/start ")){
            String toUsername = text.split(" ")[1];
            execute(userServiceImpl.sendButton(chatId,toUsername));
            execute(userServiceImpl.sendLinkToUser(chatId));
        }else if (update.hasCallbackQuery()&&update.getCallbackQuery().getData().startsWith("/sendTo ")){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            chatId = callbackQuery.getFrom().getId();
            String toUsername = callbackQuery.getData().split(" ")[1];
            SendMessage method = userServiceImpl.setToUsername(chatId, toUsername);
            if (method!=null)
                execute(method);
        }
        else{
            if (chatId!=null) {
                execute(userServiceImpl.sendAnonymousMessage(chatId, text));
                execute(userServiceImpl.sendLinkToUser(chatId));
            }
        }
    }
}
