package com.example.logintype.service.mapper;

import com.example.logintype.entity.Book;
import com.example.logintype.service.dto.response.BookResponseDto;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookResponseDto toDto(Book entity) {
        return BookResponseDto
                .builder()
                .id(entity.getId())
                .bookName(entity.getBookName())
                .imageFile(entity.getImageFileUrl())
                .number(entity.getNumber())
                .available(entity.getAvailable())
                .build();
    }
}
