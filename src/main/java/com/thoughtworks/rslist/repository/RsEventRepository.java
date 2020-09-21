package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.dto.RsEventDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RsEventRepository extends CrudRepository<RsEventDto, Integer> {
  @Override
  List<RsEventDto> findAll();

  @Transactional
  void deleteAllByUserId(int userId);

  @Transactional
  void deleteByKeyword(String keyword);

  List<RsEventDto> findAll(Sort sort);
}
