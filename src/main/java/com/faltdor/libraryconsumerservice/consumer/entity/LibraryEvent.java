package com.faltdor.libraryconsumerservice.consumer.entity;

import lombok.*;

import javax.persistence.*;



@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class LibraryEvent {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer libraryEventId;

    @Enumerated( EnumType.STRING )
    private LibraryEventType libraryEventType;

    //@OneToOne( mappedBy = "libraryEvent")
    //@ToString.Exclude
    @Transient
    private Book book;
}
