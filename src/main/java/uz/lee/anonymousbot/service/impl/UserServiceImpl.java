package uz.lee.anonymousbot.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.lee.anonymousbot.entity.User;
import uz.lee.anonymousbot.repo.UserRepo;
import uz.lee.anonymousbot.service.UserService;

import java.io.Serializable;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public SendMessage sendLinkToUser(Long chatId) {
        Optional<User> byChatId = userRepo.findByChatId(chatId);
        String username = null;
        if (byChatId.isPresent()) {
            username = byChatId.get().getUsername();
        }else{
            username = UUID.randomUUID().toString().substring(0, 8);
            User user = new User();
            user.setChatId(chatId);
            user.setUsername(username);
            userRepo.save(user);
        }
        System.out.println(chatId);
        SendMessage sendMessage = createSendMessage(chatId, username);
        InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = getInlineKeyboardButtons(username);
        rows.add(buttons);
        replyMarkup.setKeyboard(rows);
        sendMessage.setText("""
                @anonymous_anonym_bot Bu sizning shaxsiy havolangiz:
                https://t.me/anonymous_anonym_bot?start=%s
                                
                Ulashish orqali anonim suhbat quring!
                """.formatted(username));
        sendMessage.setReplyMarkup(replyMarkup);
        return sendMessage;
    }

    private static List<InlineKeyboardButton> getInlineKeyboardButtons(String username) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Ulashish ♻");
        button.setSwitchInlineQuery("""
                
                ✅ «Anonim savollar» rasmiy boti.
                                
                🤓 Men bilan anonim suhbat quring. Sizni kimligingiz qabul qiluvchiga ko'rinmaydi.
                                
                Quyidagi havola orqali xabaringizni yo'llang!⤵️
                https://t.me/anonymous_anonym_bot?start=""" +username);
        buttons.add(button);
        return buttons;
    }

    @Override
    public SendMessage sendAnonymousMessage(Long chatId, String text) {
        Optional<User> byChatId = userRepo.findByChatId(chatId);
        System.out.println(chatId);
        if (byChatId.isPresent()){
            User user = byChatId.get();
            if (user.getToUsername()!=null){
                Optional<User> receiver = userRepo.findByUsername(user.getToUsername());
                if (receiver.isPresent()){
                    user.setToUsername(null);
                    userRepo.save(user);
                    return createSendMessage(receiver.get().getChatId(), """
                            📨 Sizga yangi anonim xabar bor!
                            
                            %s
                            """.formatted(text));
                }
            }
        }
        return createSendMessage(chatId,"Xabaringiz jo'natildi!");
    }

    @Override
    public SendMessage setToUsername(Long chatId, String toUsername) {
        Optional<User> byChatId = userRepo.findByChatId(chatId);
        if (byChatId.isPresent()) {
            User user = byChatId.get();
            user.setToUsername(toUsername);
            userRepo.save(user);
            return createSendMessage(chatId,"<b>Murojaatingizni shu yerga yozing!</b>");
        }
        return null;
    }


    public SendMessage createSendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId + "");
        sendMessage.setText(text);
        sendMessage.enableHtml(true);
        return sendMessage;
    }

    public SendMessage sendButton(Long chatId, String toUsername) {
        SendMessage msg = createSendMessage(chatId, "Javob berish uchun bosing");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

        inlineKeyboardButton.setText("⚪");
        inlineKeyboardButton.setCallbackData("/sendTo %s".formatted(toUsername));

        row.add(inlineKeyboardButton);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        msg.setReplyMarkup(inlineKeyboardMarkup);
        return msg;
    }
}
