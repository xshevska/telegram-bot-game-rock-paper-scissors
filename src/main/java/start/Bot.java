package start;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;


public class Bot {

    private final TelegramBot bot = new TelegramBot(System.getenv("BOT_TOKEN"));


    public void serve() {
        //получает на вход листенер и этот листенер будет каждый 100 секунд получать список обновление от телеграм апи
        //любой экшен будет реакция и прилетать сюда
        bot.setUpdatesListener(updates -> {
            //поштручно будет обрабатывыать этот процесс
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        //получили
        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();
        InlineQuery inlineQuery = update.inlineQuery();

        BaseRequest request = null; //сюда будем складывать ответ

        if (inlineQuery != null) {
            InlineQueryResultArticle paper =
                    new InlineQueryResultArticle("paper", "Paper", "I'm ready to fight")
                            //сохраняем данные
                            .replyMarkup(
                                    new InlineKeyboardMarkup(
                                            new InlineKeyboardButton("Loading...").callbackData("0")
                                    )
                            );

        } else if (message != null) {
            long chatId = update.message().chat().id();
            request = new SendMessage(chatId, "Hello, Jakubko!");
        }



        //только когда будем отвечать на события пришедшие из него
        if (request != null) {
            bot.execute(request);
        }



    }



}
