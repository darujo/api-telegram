package ru.daru_jo.telegram_bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

//@Slf4j
@Component
public class TelegramBotConfig {
//    @Bean(name = "telegramScheduledExecutorService", defaultCandidate = false) // <-- 2
//    public ScheduledExecutorService telegramScheduledExecutorService() {
//        return Executors.newSingleThreadScheduledExecutor();
//    }
//
//    @Bean // <-- 3
//    @Primary
//    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication(
//            @Qualifier("telegramScheduledExecutorService") ScheduledExecutorService scheduledExecutorService // <-- 4
//    ) {
//        return new TelegramBotsLongPollingApplication(
//                ObjectMapper::new, // <-- 5
//                new TelegramOkHttpClientFactory.DefaultOkHttpClientCreator(), // <-- 6
//                () -> scheduledExecutorService
//        );
//
//    }
    @Value("${telegram-bot.token}")
    private String botToken;
//
    @Bean
    public TelegramClient telegramClient() {
        System.out.println(botToken);
        return new OkHttpTelegramClient(botToken);
    }
@Bean
    public TelegramBotRequest telegramBotRequest(){
        return new TelegramBotRequest();
}

}
