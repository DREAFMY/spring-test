package com.thoughtworks.rslist.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RsServiceTest {
  RsService rsService;

  @Mock RsEventRepository rsEventRepository;
  @Mock UserRepository userRepository;
  @Mock VoteRepository voteRepository;
  @Mock TradeRepository tradeRepository;
  LocalDateTime localDateTime;
  Vote vote;

  @BeforeEach
  void setUp() {
    initMocks(this);
    rsService = new RsService(rsEventRepository, userRepository, voteRepository, tradeRepository);
    localDateTime = LocalDateTime.now();
    vote = Vote.builder().voteNum(2).rsEventId(1).time(localDateTime).userId(1).build();
  }

  @Test
  void shouldVoteSuccess() {
    // given

    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();
    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();

    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    // when
    rsService.vote(vote, 1);
    // then
    verify(voteRepository)
        .save(
            VoteDto.builder()
                .num(2)
                .localDateTime(localDateTime)
                .user(userDto)
                .rsEvent(rsEventDto)
                .build());
    verify(userRepository).save(userDto);
    verify(rsEventRepository).save(rsEventDto);
  }

  @Test
  void shouldThrowExceptionWhenUserNotExist() {
    // given
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.empty());
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
    //when&then
    assertThrows(
        RuntimeException.class,
        () -> {
          rsService.vote(vote, 1);
        });
  }

  @Test
  void shouldBuySuccess() {
    UserDto userDto = UserDto.builder().voteNum(20).phone("18888888881").gender("male").email("asfa@b.com").age(18).userName("huahua").build();
    RsEventDto rsEventDto = RsEventDto.builder().eventName("敲代码").keyword("programing").voteNum(5).user(userDto).build();
    userRepository.save(userDto);
    RsEventDto save = rsEventRepository.save(rsEventDto);
    Trade trade = Trade.builder().amount(2).rank(1).build();
    rsService.buy(trade, save.getId());
    List<TradeDto> all = tradeRepository.findAll();
    assertEquals(5,all.get(0).getAmount());
    assertEquals(1,all.get(0).getRank());
    assertEquals(save.getId(),all.get(0).getRsEventId());
  }

  @Test
  void should_throw_exception_when_money_less() throws Exception{
    UserDto userDto = UserDto.builder().voteNum(20).phone("18888888811").gender("male").email("astrfa@b.com").age(44).userName("xixi").build();
    RsEventDto rsEventDto = RsEventDto.builder().eventName("写作业").keyword("homework").voteNum(2).user(userDto).build();
    RsEventDto save = rsEventRepository.save(rsEventDto);
    Trade trade = Trade.builder().amount(10).rank(1).build();
    rsService.buy(trade, save.getId());
    trade.setAmount(1);
    rsService.buy(trade, save.getId());
  }

  @Test
  void should_throw_exception_when_not_found_event() {
    Trade trade = Trade.builder().amount(10).rank(1).build();
    rsService.buy(trade, 10000);
  }
}
