package com.example.logintype.service;

import com.example.logintype.entity.Book;
import com.example.logintype.service.dto.request.BookRequestDto;
import com.example.logintype.service.dto.response.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    /**
     *
     */
    Page<Book> getBooks(Pageable pageable);

    BookResponseDto getBook(Long bookId);

    BookResponseDto createBook(BookRequestDto request);

    void updateBook(Long bookId, BookRequestDto request);

    void deleteImageBook(Long bookId);
}
