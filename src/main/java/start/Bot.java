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
import com.pengrad.telegrambot.request.*;


public class Bot {

    private final TelegramBot bot = new TelegramBot(System.getenv("BOT_TOKEN"));
    private final String PROCESSING_LABEL = "Processing...";


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

        //для кнопки
        //проверяем что это месседж у нас написан через бота
        if (message != null && message.viaBot() != null && message.viaBot().username().equals("gameBonya_bot")) {
            InlineKeyboardMarkup replyMarkup = message.replyMarkup();
            if( replyMarkup == null) {
                return;
            }
            InlineKeyboardButton[][] buttons = replyMarkup.inlineKeyboard();
            if (buttons == null) {
                return;
            }
            String senderChose = buttons[0][0].text();

            if (!senderChose.equals(PROCESSING_LABEL)) {
                return;
            }

            Long chatId = message.chat().id();
            String senderName = message.from().firstName();
            Integer messageId = message.messageId();

            //исправляет клавиатуру
            new EditMessageText(chatId, messageId, message.text())
                    .replyMarkup(
                            new InlineKeyboardMarkup(
                                    new InlineKeyboardButton("🤏")
                                            .callbackData(String.format("%d %s %s %s", chatId, senderName, senderChose, "0")),
                                    new InlineKeyboardButton("✌")
                                            .callbackData(String.format("%d %s %s %s", chatId, senderName, senderChose, "1")),
                                    new InlineKeyboardButton("✊")
                                            .callbackData(String.format("%d %s %s %s", chatId, senderName, senderChose, "2"))

                            )

                    );


        } else if (inlineQuery != null) {
            InlineQueryResultArticle paper = buildInbutton("paper", "\uD83E\uDD0F Paper", "0");
            InlineQueryResultArticle scissors = buildInbutton("scissors", "✌️Scissors", "1");
            InlineQueryResultArticle rock = buildInbutton("rock", "✊ Rock ", "2");

            request = new AnswerInlineQuery(inlineQuery.id(), paper, scissors, rock);


        } else if (message != null) {
            long chatId = update.message().chat().id();
            request = new SendMessage(chatId, "Hello");
        }



        //только когда будем отвечать на события пришедшие из него
        if (request != null) {
            bot.execute(request);
        }



    }

    private InlineQueryResultArticle buildInbutton(String id, String title, String callbackData) {
        return new InlineQueryResultArticle(id, title, "I'm ready to fight")
                //сохраняем данные
                .replyMarkup(
                        new InlineKeyboardMarkup(
                                new InlineKeyboardButton(PROCESSING_LABEL).callbackData(callbackData)
                        )
                );
    }


}
