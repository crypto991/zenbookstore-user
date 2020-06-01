package com.bookstore.service;

import com.bookstore.domain.Book;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {


    Book findById(Long id);

    List<Book> findAll();

    List<Book> findByCategory(String category);

    List<Book> blurrySearch(String title);

}
