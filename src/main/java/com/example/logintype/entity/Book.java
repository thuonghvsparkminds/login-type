package com.example.logintype.entity;

import com.example.logintype.entity.enumrated.BookStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "book")
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_name")
    private String bookName;

    @Column(name = "image_file_url")
    private String imageFileUrl;

    @Column(name = "number")
    private Integer number;

    @Column(name = "available")
    private Integer available;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookStatusEnum status;
}
