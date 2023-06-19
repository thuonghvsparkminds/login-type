package com.example.logintype.service.iml;

import com.example.logintype.entity.Book;
import com.example.logintype.entity.enumrated.BookStatusEnum;
import com.example.logintype.entity.enumrated.FileProperty;
import com.example.logintype.exception.BadRequestException;
import com.example.logintype.exception.ResourceNotFoundException;
import com.example.logintype.repository.BookRepository;
import com.example.logintype.service.BookService;
import com.example.logintype.service.FileUploadService;
import com.example.logintype.service.dto.request.BookRequestDto;
import com.example.logintype.service.dto.response.BookResponseDto;
import com.example.logintype.service.dto.response.FileUploadResponseDto;
import com.example.logintype.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    /**
     *
     */
    private final BookRepository bookRepository;
    private final FileUploadService fileUploadService;
    private final BookMapper bookMapper;

    @Override
    public Page<BookResponseDto> getBooks(Pageable pageable){

        return bookRepository.findAll(pageable)
                .map(book -> bookMapper.toDto(book));
    }

    @Override
    public BookResponseDto getBook(Long bookId){

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("This book is not exist"));
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public BookResponseDto createBook(BookRequestDto request) {

        Book book = new Book();
        book.setBookName(request.getBookName());

        //upload image
        if(request.getImageFile() != null) {
            FileUploadResponseDto uploadFile = fileUploadService.uploadFile(request.getImageFile(), FileProperty.BOOK_IMAGE);
            book.setImageFileUrl(uploadFile.getDownloadUri());
        }

        book.setNumber(0);
        book.setAvailable(0);
        book.setStatus(BookStatusEnum.NON_AVAILABLE);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    @Transactional
    public void updateBook(Long bookId, BookRequestDto request) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("This book not exist"));

        // upload document
        if(request.getImageFile() != null) {

            fileUploadService.deleteFile(book.getImageFileUrl());
            FileUploadResponseDto uploadFile = fileUploadService.uploadFile(request.getImageFile(), FileProperty.BOOK_IMAGE);
            book.setImageFileUrl(uploadFile.getDownloadUri());
        }
    }

    @Override
    @Transactional
    public void deleteImageBook(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("This book not exist"));

        fileUploadService.deleteFile(book.getImageFileUrl());
        book.setImageFileUrl(null);
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("This book not exist"));

        book.setStatus(BookStatusEnum.DELETED);
    }

    @Override
    @Transactional
    public void addBookNumber(Long bookId, Integer number) {

        if (number < 0) {
            throw new BadRequestException("The number of book added must be getter then 0");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BadRequestException("This book is not exist"));

        if (book.getStatus() == BookStatusEnum.NON_AVAILABLE) {
            book.setStatus(BookStatusEnum.AVAILABLE);
        }

        book.setNumber(book.getNumber() + number);
        book.setAvailable(book.getAvailable() + number);
    }
}
