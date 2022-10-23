package org.buravlev.controller;

import org.buravlev.service.UpdateProducer;
import org.buravlev.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.buravlev.model.RabbitQueue.*;

@Component
public class UpdateController {
    private Logger log = LoggerFactory.getLogger(UpdateController.class);
    private TelegramBot telegramBot;
    private MessageUtil messageUtil;
    private UpdateProducer updateProducer;

    public UpdateController(MessageUtil messageUtil, UpdateProducer updateProducer) {
        this.updateProducer = updateProducer;
        this.messageUtil = messageUtil;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Update is null");
        }
        if (update.getMessage() != null) {
            distributeMessageByType(update);
        } else {
            log.error("Received unsupported message type");
        }
    }
    private void distributeMessageByType(Update update) {
        Message message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        } else if (message.getDocument() != null) {
            processDocMessage(update);
        } else if (message.getPhoto() != null) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }
    private void setUnsupportedMessageTypeView(Update update) {
        SendMessage sendMessage = messageUtil.sendText(update, "Unsupported type of message");
        setView(sendMessage);
    }
    private void setFileIsReceivedView(Update update) {
        SendMessage sendMessage = messageUtil.sendText(update, "File is received");
        setView(sendMessage);
    }
    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
        setUnsupportedMessageTypeView(update);
    }
    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }
    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }
}
