package com.mftplus.telegram.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mftplus.telegram.entity.Article;
import com.mftplus.telegram.entity.SevenWonders;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Dice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.awt.font.ShapeGraphicAttribute;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Component
public class MessageBuilder {

    public static final String DONATE_CALLBACK ="DONATE_CALLBACK";
    public static final String DO_NOT_SHOW_THIS ="DO_NOT_SHOW_THIS";
    public static final String NEXT_TIME ="NEXT_TIME";

    public SevenWonders sevenWonders;


    @SneakyThrows
    @PostConstruct
    public void initSevenWonders(){
        var resource = new ClassPathResource("/files/seven-wonders.json");
        ObjectMapper mapper = new ObjectMapper();
        sevenWonders = mapper.readValue(resource.getFile() , SevenWonders.class);
    }


    public SendMessage buildTextMessage(Long chatId , String text){
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }

    public SendMessage buildFormattedTextMessage(Long chatId){
        var message = new SendMessage();
        message.setChatId(chatId);
//        message.setParseMode(ParseMode.MARKDOWNV2);
//        message.setText("*This Is Bold Text*");
        String parsMode = ParseMode.MARKDOWNV2;
        message.setParseMode(parsMode);
        message.setText(generateFormattedText(parsMode));
        return  message;
    }

    public SendMessage buildReplyKeyboardMessage(Long chatId){
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose One Game!");
        var replyKeyboard = new ReplyKeyboardMarkup();
        var row1 = new KeyboardRow();
        row1.add("dice");
        row1.add("dart");

        var row2 = new KeyboardRow();
        row2.add("football");
        row2.add("basketball");

        var row3 = new KeyboardRow();
        row3.add("bowling");
        row3.add("casino");

        replyKeyboard.setKeyboard(List.of(row1 , row2 , row3));
        replyKeyboard.setResizeKeyboard(true);
        replyKeyboard.setOneTimeKeyboard(false);
        message.setReplyMarkup(replyKeyboard);
        return message;
    }

    public SendDice buildSendDice(Long chatId , String emoji){
        var dice = new SendDice();
        dice.setChatId(chatId);
        dice.setEmoji(emoji);
        return dice;

    }

    public SendMessage buildDeleteKeyboardMessage(Long chatId){
        var message = new SendMessage();
        message.setText("come back soon!");
        message.setChatId(chatId);
        ReplyKeyboardRemove replyKeyboardRemove =  new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        message.setReplyMarkup(replyKeyboardRemove);
        return message;
    }

    public SendMessage buildInlineKeyboardMessage(Long chatId){
        var message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Pleas Consider Donating.");
        var inlineKeyboard = new InlineKeyboardMarkup();
        message.setReplyMarkup(inlineKeyboard);
        List<List<InlineKeyboardButton>> lineOfButtons = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();

        var donateBtn = new InlineKeyboardButton();
        donateBtn.setText("DONATE \uD83D\uDCB5");
        donateBtn.setCallbackData(DONATE_CALLBACK);
        row1.add(donateBtn);
        lineOfButtons.add(row1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();

        var doNotShowThisBtn = new InlineKeyboardButton();
        doNotShowThisBtn.setText("Don't Show This Again");
        doNotShowThisBtn.setCallbackData(DO_NOT_SHOW_THIS);
        row2.add(doNotShowThisBtn);

        var nextTimeBtn = new InlineKeyboardButton();
        nextTimeBtn.setText("Next Time");
        nextTimeBtn.setCallbackData(NEXT_TIME);
        row2.add(nextTimeBtn);

        lineOfButtons.add(row2);

        inlineKeyboard.setKeyboard(lineOfButtons);
        message.setReplyMarkup(inlineKeyboard);
        return message;


    }

//    public SendMessage buildDonateOptions(Long chatId){
//        var message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText("You Can Donate Through :");
//
//        var inlineKeyboard = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
//
//        var payPalBtn = new InlineKeyboardButton();
//        payPalBtn.setText("PayPal");
//        payPalBtn.setUrl("https://www.paypal.com/");
//
//        var kofiBtn = new InlineKeyboardButton();
//        kofiBtn.setText("ko-fi");
//        kofiBtn.setUrl("https://www.ko-fi.com/");
//
//        listOfButtons.add(List.of(payPalBtn, kofiBtn));
//
//        inlineKeyboard.setKeyboard(listOfButtons);
//        message.setReplyMarkup(inlineKeyboard);
//
//        return message;
//    }


    public EditMessageText buildDonateOptions(Message message){
        var editMessage = new EditMessageText();
        editMessage.setChatId(message.getChatId());
        editMessage.setMessageId(message.getMessageId());
        editMessage.setText("%s\n\nYou Can Donate Through :".formatted(message.getText()));

        var inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        var payPalBtn = new InlineKeyboardButton();
        payPalBtn.setText("PayPal");
        payPalBtn.setUrl("https://www.paypal.com/");

        var kofiBtn = new InlineKeyboardButton();
        kofiBtn.setText("ko-fi");
        kofiBtn.setUrl("https://www.ko-fi.com/");

        listOfButtons.add(List.of(payPalBtn, kofiBtn));

        inlineKeyboard.setKeyboard(listOfButtons);
        editMessage.setReplyMarkup(inlineKeyboard);

        return editMessage;
    }

    public DeleteMessage buildDeleteMessage(Message message){
        var deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(message.getChatId());

        return deleteMessage;
    }


    public SendPhoto buildPhotoMessage(Long chatId , String url , String caption){
        var message = new SendPhoto();
        message.setChatId(chatId);
        message.setCaption(caption);
        var file = new InputFile(url);
        message.setPhoto(file);
        return message;
    }

    public SendDocument buildDocumentMessage(Long chatId , String caption , File file){
        var message = new SendDocument();
        message.setChatId(chatId);
        message.setCaption(caption);
        var document = new InputFile(file , file.getName());
        message.setDocument(document);
        return message;
    }

    public SendSticker buildStickerMessage(Long chatId , String fileId){
        var message = new SendSticker();
        message.setChatId(chatId);
        var file = new InputFile(fileId);
        message.setSticker(file);
        return message;
    }

//    public AnswerInlineQuery buildWondersInlineQuery(InlineQuery inlineQuery){
//        var answer = new AnswerInlineQuery();
//        answer.setInlineQueryId(inlineQuery.getId());
//        List<InlineQueryResult> queryResults = new ArrayList<>();
//
//        queryResults.add(buildQueryResultArticle());
//
//        answer.setResults(queryResults);
//        return answer;
//
//    }

//    private InlineQueryResultArticle buildQueryResultArticle(){
//        var article = new InlineQueryResultArticle();
//        article.setId("1");
//        article.setTitle("Great Pyramid of Giza");
//        article.setDescription("The Great Pyramid pf Giza[a] is the largest egyptian pyramid. ");
//        article.setUrl("https://en.wikipedia.org/wiki/Great_Pyramod_of_Giza");
//        article.setHideUrl(true);
//        article.setThumbUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Kheops-Pyramid.jpg/450px-Kheops-Pyramid.jpg");
//
//        var content = new InputTextMessageContent();
//        content.setMessageText("The Great Pyramid of Giza[a] is the largest Egyptian pyramid. It served as the tomb of pharaoh Khufu, who ruled during the Fourth Dynasty of the Old Kingdom");
//        article.setInputMessageContent(content);
//
//        return article;
//    }


    public AnswerInlineQuery buildWondersInlineQuery(InlineQuery inlineQuery){
        var answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
        List<InlineQueryResult> queryResults = new ArrayList<>();

        List<Article> articles = sevenWonders.getArticles();
      for (int i = 0 ; i < articles.size(); i++){
        InlineQueryResultArticle article = buildQueryResultArticle(i +"" , articles.get(i));
          queryResults.add(article);
      }

        answer.setResults(queryResults);
        return answer;

    }
 private InlineQueryResultArticle buildQueryResultArticle(String id , Article article){
        var resultArticle = new InlineQueryResultArticle();
        resultArticle.setId(id);
        resultArticle.setTitle(article.getTitle());
        resultArticle.setDescription(article.getDescription());
        resultArticle.setUrl(article.getArticleUrl());
        resultArticle.setHideUrl(true);
        resultArticle.setThumbUrl(article.getThumbUrl());

        var content = new InputTextMessageContent();
        content.setMessageText(article.getDescription());
        resultArticle.setInputMessageContent(content);

        return resultArticle;
    }



    public String generateFormattedText(String parsMode){
        if (ParseMode.MARKDOWNV2.equals(parsMode)){
            return """
                    [inline URL](http://www.example.com/)
                    [inline user mention](tg://user?id=531474683)
                    """;
        }else if (ParseMode.HTML.equals(parsMode)){
            return """
                    <span class="tg-spoiler">using span tag</span>
                    <tg-spoiler>tg-spoiler tag</tg-spoiler>
                    
                    """;
        }
        return null;
    }
}
