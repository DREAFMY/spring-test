package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.service.RsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradeController {
    @Autowired
    RsService rsService;

    @PostMapping("/trade/{eventId}")
    public ResponseEntity<Void> trade(@PathVariable int eventId, @RequestBody Trade trade) {
        rsService.buy(trade, eventId);
        return ResponseEntity.ok().build();
    }
}
