package com.example.studytelegrambot;

import com.example.studytelegrambot.Config.BotConfig;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
//@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Начать работу"));
        commands.add(new BotCommand("/image", "Отобразить изображение"));
        commands.add(new BotCommand("/today", "Сегодняшняя дата"));
        commands.add(new BotCommand("/map", "Картинка текстом"));
        commands.add(new BotCommand("/menu", "Выбери зверушку"));

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет " + name + " как я могу тебе помочь?";
        sendMessage(chatId, answer);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start" -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());

                case "/image" -> {
                    sendMessage(chatId, "https://www.google.com/imgres?imgurl=https%3A%2F%2Fmedia.istockphoto.com%2Fid%2F183412466%2Fru%2F%25D1%2584%25D0%25BE%25D1%2582%25D0%25BE%2F%25D0%25B2%25D0%25BE%25D1%2581%25D1%2582%25D0%25BE%25D1%2587%25D0%25BD%25D1%258B%25D0%25B5-bluebirds-%25D0%25BC%25D1%2583%25D0%25B6%25D1%2581%25D0%25BA%25D0%25BE%25D0%25B3%25D0%25BE-%25D0%25B8-%25D0%25B6%25D0%25B5%25D0%25BD%25D1%2581%25D0%25BA%25D0%25BE%25D0%25B3%25D0%25BE-%25D0%25BF%25D0%25BE%25D0%25BB%25D0%25B0.jpg%3Fs%3D612x612%26w%3D0%26k%3D20%26c%3Dh67A1VxL4ca0AztbhFCJlEcLSr3QMmTmMn0Cmu6PS1M%3D&tbnid=MKzodU0-uOosDM&vet=12ahUKEwj8wP3fr8mEAxXjGRAIHdWYCBcQMygAegQIARBv..i&imgrefurl=https%3A%2F%2Fwww.istockphoto.com%2Fru%2F%25D1%2584%25D0%25BE%25D1%2582%25D0%25BE%25D0%25B3%25D1%2580%25D0%25B0%25D1%2584%25D0%25B8%25D0%25B8%2F%25D0%25B2%25D0%25B5%25D1%2581%25D0%25BD%25D0%25B0-%25D1%2584%25D0%25BE%25D1%2582%25D0%25BE%25D0%25B3%25D1%2580%25D0%25B0%25D1%2584%25D0%25B8%25D0%25B8&docid=cMsFAC5vO_NhVM&w=612&h=408&q=%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8%20%D0%B2%D0%B5%D1%81%D0%BD%D0%B0&ved=2ahUKEwj8wP3fr8mEAxXjGRAIHdWYCBcQMygAegQIARBv");
                    setCallButtons(chatId);
                }

                case "/today" -> {
                    sendMessage(chatId, (String) sendDate());
                }

                case "/belka" -> {
                    sendImage(chatId, "belka.jpeg");
                }
                case "/ezhik" -> {
                    sendImage(chatId, "ezhik.png");
                }
                case "/map" -> {
                    loadMap(chatId);
                }
                case "/menu" -> {
                    setCallButtons(chatId);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            switch (call_data) {
                case "/belka" -> {
                    sendImage(chat_id, "belka.jpeg");
                }
                case "/ezhik" -> {
                    sendImage(chat_id, "ezhik.png");
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("/image"));

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private void sendImage(Long chatId, String path) {
        String filePath = "assets/image/" + path;
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        File file = new File(filePath);
        try {
            photo.setPhoto(new InputFile(new FileInputStream(file), path));
            photo.setCaption("Милые животные");
            execute(photo);
        } catch (FileNotFoundException | TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMap(Long chatId) {
        String path = "assets/map/map.txt";
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    reader.close();
                    break;
                }
                if (!line.startsWith("!")) {
                    lines.add(line);
                    width = Math.max(width, line.length());
                }

            }

            for (int i = 0; i < height; i++) {
                String line = (String) lines.get(i);
                sendMessage(chatId, line);
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setCallButtons(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выбери зверя");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Белка");
        inlineKeyboardButton1.setCallbackData("/belka");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Ёжик");
        inlineKeyboardButton2.setCallbackData("/ezhik");

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Все остальные животные");
        inlineKeyboardButton3.setUrl("https://stock.adobe.com/ru/discover/animals");

        rowInline1.add(inlineKeyboardButton1);
        rowInline1.add(inlineKeyboardButton2);
        rowInline1.add(inlineKeyboardButton3);

        rowInline.add(rowInline1);
        markupInline.setKeyboard(rowInline);
        sendMessage.setReplyMarkup(markupInline);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Object sendDate() {
        java.util.Date date = new java.util.Date();
        return date;
    }
}
