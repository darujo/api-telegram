package ru.daru_jo.telegram_bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.TelegramOkHttpClientFactory;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.daru_jo.model.ChatInfo;
import ru.daru_jo.service.FileService;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Configuration
@EnableJpaRepositories(basePackages = {
        "ru.daru_jo.repository"
})
@EntityScan("ru.daru_jo.model")
@ComponentScan("ru.daru_jo.service")
public class TelegramBotConfig {
        @Bean(name = "telegramScheduledExecutorService", defaultCandidate = false) // <-- 2
    public ScheduledExecutorService telegramScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean // <-- 3
    @Primary
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication(
            @Qualifier("telegramScheduledExecutorService") ScheduledExecutorService scheduledExecutorService // <-- 4
    ) {
        return new TelegramBotsLongPollingApplication(
                ObjectMapper::new, // <-- 5
                new TelegramOkHttpClientFactory.DefaultOkHttpClientCreator(), // <-- 6
                () -> scheduledExecutorService
        );

    }

    @Bean
    @ConditionalOnMissingBean(MenuService.class)
    public MenuService menuServiceImp(){
            return new MenuService() {
                @Override
                public void openMainMenu(ChatInfo chatInfo) {

                }

                @Override
                public void getMenu(ChatInfo chatInfo, String command, File picture){

                }

                @Override
                public void openCancel(ChatInfo chatInfo, String text) {

                }
            };
    }

    @Value("${telegram-bot.token}")
    private String botToken;

    //
    @Bean
    public TelegramClient telegramClient() {
        System.out.println(botToken);
        return new OkHttpTelegramClient(botToken);
    }

    @Bean
    public TelegramBotRequest telegramBotRequest() {
        return new TelegramBotRequest();
    }

    @Bean
    public TelegramBotSend telegramBotSend() {
        return new TelegramBotSend();
    }


    @Bean
    public FileService fileService() {
        return new FileService();

    }

}
