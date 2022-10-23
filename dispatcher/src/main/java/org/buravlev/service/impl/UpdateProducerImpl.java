package org.buravlev.service.impl;

import org.buravlev.service.UpdateProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UpdateProducerImpl implements UpdateProducer {
    private static Logger log = LoggerFactory.getLogger(UpdateProducerImpl.class);
    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
    }
}
