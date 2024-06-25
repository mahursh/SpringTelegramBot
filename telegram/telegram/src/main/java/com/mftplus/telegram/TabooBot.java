package com.mftplus.telegram;


import com.mftplus.telegram.message.MessageBuilder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

@Component

public class TabooBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TabooBot.class);

    private final String username;
    private final MessageBuilder messageBuilder;
    public static final String PHOTO_URL = "https://en.wikipedia.org/wiki/New_York_City#/media/File:New_york_times_square-terabass_(cropped).jpg";
    public static final String PHOTO_CAPTION = "New-York City !";
    public static final String STICKER_FILE_ID = "CAACAgQAAxkBAAM5ZnnPwfSGBx3RexEITwABt2HcaYT7AALLBAACsYUCB2bJ02n3FvfHNQQ";

    public static final Map<String , String> GAME_BY_EMOJI =  Map.of(
            "dice" , "\uD83C\uDFB2",
            "darts" , "\uD83C\uDFAF",
            "basketball","\uD83C\uDFC0",
            "football" ,"⚽",
            "bowling" ,"\uD83C\uDFB3",
            "casino" ,"\uD83C\uDFB0"




    );

    public TabooBot(@Value("${app.telegram.username}") String username,
                    @Value("${app.telegram.token}") String botToken,
                    MessageBuilder messageBuilder) {
        super(botToken);
        this.username = username;
        this.messageBuilder = messageBuilder;
        logger.warn("TabooBot initialized with username: {}", username);
        logger.warn("TabooBot initialized with token: {}", botToken);
    }

    @Override
    public String getBotUsername() {

        return username;
    }

    @SneakyThrows
    private File readFile(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        return resource.getFile();
    }


    public void logMessageEntities(Message message){
        message.getEntities().forEach(entity ->{
            String logText =
                    switch(entity.getType()){
                        case "text_link" -> "text=%s, link=%s".formatted(entity.getText() , entity.getUrl());
                        case "text_mention" -> "text=%s , userId=%s".formatted(entity.getText() , entity.getUser().getId());
                        default -> entity.getText();
                    };
            logger.info("Message entity ={} ", logText);
        });
    }



    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        //changing name of thread for log.
        Thread.currentThread().setName("Telegram - onUpdateReceived");

        if (update.hasMessage()) {


            Message message = update.getMessage();
            Long chatId = message.getChatId();
            logger.warn("Telegram id ={}", message.getFrom().getId());

            if (message.hasText()) {
                if (message.hasEntities()) {
                    logMessageEntities(message);
                }
                String text = message.getText();
                if (GAME_BY_EMOJI.keySet().contains(text)) {
                    execute(messageBuilder.buildSendDice(chatId, GAME_BY_EMOJI.get(text)));
                    return;
                }


                switch (text) {
                    case "/text" -> execute(messageBuilder.buildTextMessage(chatId, "My Test Text !"));
                    case "/photo" -> execute(messageBuilder.buildPhotoMessage(chatId, PHOTO_URL, PHOTO_CAPTION));
                    case "/document" ->
                            execute(messageBuilder.buildDocumentMessage(chatId, "Here Is A File !", readFile("/files/New_york_times_square-terabass_(cropped).jpg")));
                    case "/sticker" -> execute(messageBuilder.buildStickerMessage(chatId, STICKER_FILE_ID));
                    case "/formattedText" -> execute(messageBuilder.buildFormattedTextMessage(chatId));
                    case "/play" -> execute(messageBuilder.buildReplyKeyboardMessage(chatId));
                    case "/endGame" -> execute(messageBuilder.buildDeleteKeyboardMessage(chatId));
                    case "/donate" -> execute(messageBuilder.buildInlineKeyboardMessage(chatId));
                }
            } else if (message.hasSticker()) {
                logger.warn("Sticker File ID={}", message.getSticker().getFileId());
            }
        }else if (update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            logger.warn("Callback Value ={}" , callbackQuery.getData());

        }

    }




}
