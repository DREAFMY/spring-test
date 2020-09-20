package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.TradeDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface TradeRepository extends CrudRepository<TradeDto, Integer>  {

    TradeDto findFirstByRsEventIdOrderByAmount(Integer rsEventId, Sort sort);
}
