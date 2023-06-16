package com.example.logintype.controller.privates;

import com.example.logintype.entity.Book;
import com.example.logintype.service.BookService;
import com.example.logintype.service.dto.request.BookRequestDto;
import com.example.logintype.service.dto.response.BookResponseDto;
import com.example.logintype.service.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/private/books")
public class BookPrivateController {

    /**
     *
     */
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(@PageableDefault Pageable pageable) {

        Page<Book> page = bookService.getBooks(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                page
        );

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponseDto> getBook(@PathVariable("bookId") Long bookId) {

        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<BookResponseDto> createBook(BookRequestDto requestDto) {

        return ResponseEntity.ok(bookService.createBook(requestDto));
    }

    @PutMapping(value = "/{bookId}",consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable("bookId") Long bookId,
            BookRequestDto requestDto
    ) {

        bookService.updateBook(bookId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(consumes = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<BookResponseDto> deleteImageBook(@RequestParam Long bookId) {

        bookService.deleteImageBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
