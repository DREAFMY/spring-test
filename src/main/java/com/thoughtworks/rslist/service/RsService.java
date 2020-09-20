package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.exception.CommonsException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RsService {
  final RsEventRepository rsEventRepository;
  final UserRepository userRepository;
  final VoteRepository voteRepository;
  final TradeRepository tradeRepository;

  public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository, TradeRepository tradeRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.tradeRepository = tradeRepository;
  }

  public void vote(Vote vote, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    Optional<UserDto> userDto = userRepository.findById(vote.getUserId());
    if (!rsEventDto.isPresent()
        || !userDto.isPresent()
        || vote.getVoteNum() > userDto.get().getVoteNum()) {
      throw new RuntimeException();
    }
    VoteDto voteDto =
        VoteDto.builder()
            .localDateTime(vote.getTime())
            .num(vote.getVoteNum())
            .rsEvent(rsEventDto.get())
            .user(userDto.get())
            .build();
    voteRepository.save(voteDto);
    UserDto user = userDto.get();
    user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
    userRepository.save(user);
    RsEventDto rsEvent = rsEventDto.get();
    rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
    rsEventRepository.save(rsEvent);
  }

  @Transactional
  public ResponseEntity<Void> buy(Trade trade, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    if (!rsEventDto.isPresent()) {
      throw new CommonsException("未找到该热搜");
    }
    RsEventDto event = rsEventDto.get();
    if (event.getPosition() > 0) {
      TradeDto dbRecoder = tradeRepository.findFirstByRsEventIdOrderByAmount(event.getId(), Sort.by("amount").descending());
      if (dbRecoder.getAmount() > trade.getAmount()) {
        throw new CommonsException("金额不够");
      }

      RsEventDto newEvent = RsEventDto.builder().eventName(event.getEventName()).keyword(event.getKeyword())
              .user(event.getUser()).voteNum(event.getVoteNum()).build();
      newEvent.setPosition(trade.getRank());
      rsEventRepository.save(newEvent);
      rsEventRepository.deleteById(event.getId());
    }

    event.setPosition(trade.getRank());
    rsEventRepository.save(event);

    TradeDto recoder = TradeDto.builder().amount(trade.getAmount()).rank(trade.getRank()).rsEventId(event.getId()).build();
    tradeRepository.save(recoder);

    return ResponseEntity.ok().build();
  }
}
