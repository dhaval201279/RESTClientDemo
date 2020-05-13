package com.its.RESTClientDemo.controller;

import com.its.RESTClientDemo.entity.CardEntity;
import com.its.RESTClientDemo.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class CardLifecycleController {
    private final CardService cardService;

    public CardLifecycleController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/card")
    public CardEntity addCard(@RequestBody CardEntity aCard) {
        log.info("Entering and leaving CardLifecycleController : addCard after saving card with no. - {}  ",
                aCard.getCardNumber());
        return cardService.enroll(aCard);

    }
}
