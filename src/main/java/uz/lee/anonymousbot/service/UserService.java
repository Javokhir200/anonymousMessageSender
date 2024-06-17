package uz.lee.anonymousbot.service;


import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public interface UserService {
    SendMessage sendLinkToUser(Long chatId);
    SendMessage sendAnonymousMessage(Long chatId,String text);

    SendMessage setToUsername(Long chatId, String toUsername);
}
