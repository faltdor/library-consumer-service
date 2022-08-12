package com.faltdor.libraryconsumerservice.consumer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Book {
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer bookId;

    private String bookName;

    private String bookAuthor;

    //@OneToOne
    //@JoinColumn( name = "libraryEventId" )
    @Transient
    private LibraryEvent libraryEvent;
}
