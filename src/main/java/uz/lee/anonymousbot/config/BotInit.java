package uz.lee.anonymousbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.lee.anonymousbot.bot.MyTelegramBot;

@Configuration
public class BotInit {

    @Bean
    public TelegramBotsApi telegramBotsApi(MyTelegramBot myTelegramBot) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(myTelegramBot);
        return telegramBotsApi;
    }
}
