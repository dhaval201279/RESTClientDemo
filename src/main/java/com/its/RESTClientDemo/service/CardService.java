package com.its.RESTClientDemo.service;

import com.its.RESTClientDemo.entity.CardEntity;
import com.its.RESTClientDemo.infrastructure.DownstreamAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {

    @Autowired
    private final DownstreamAdapter downstreamAdapter;

    public CardEntity enroll(CardEntity aCard) {
        log.info("7 Entering enroll");
        String cardNo = aCard.getCardNumber();
        String alias = downstreamAdapter.generateAlias(cardNo);
        log.info("Alias for card = {} is : {}", cardNo, alias);
        aCard.setAlias(alias);
        log.info("7 Leaving enroll");
        return aCard;
    }
}
