package com.mftplus.telegram;

import org.apache.http.HttpHost;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramLongPollingBot telegramBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBot);
        return botsApi;
    }
//    @Bean
//    public DefaultBotOptions botOptions() {
//        DefaultBotOptions options = new DefaultBotOptions();
////        HttpHost proxy = new HttpHost("47.251.85.12", 80);
//        options.setProxyHost("47.251.85.12");
//        options.setProxyPort(80);
//        options.setProxyType(DefaultBotOptions.ProxyType.HTTP);
//        return options;
//    }

}
