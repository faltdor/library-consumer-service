package com.faltdor.libraryconsumerservice.repository;

import com.faltdor.libraryconsumerservice.consumer.entity.Book;
import org.springframework.data.repository.CrudRepository;


public interface BookEventRepository extends CrudRepository<Book, Integer> {
}
