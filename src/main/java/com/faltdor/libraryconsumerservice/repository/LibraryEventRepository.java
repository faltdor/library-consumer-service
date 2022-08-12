package com.faltdor.libraryconsumerservice.repository;

import com.faltdor.libraryconsumerservice.consumer.entity.LibraryEvent;
import org.springframework.data.repository.CrudRepository;


public interface LibraryEventRepository extends CrudRepository<LibraryEvent, Integer> {

}
