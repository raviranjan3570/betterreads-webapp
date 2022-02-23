package com.betterreads.userbooks;

import com.betterreads.book.Book;
import com.betterreads.book.BookRepository;
import com.betterreads.user.BooksByUser;
import com.betterreads.user.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserBookController {

    @Autowired
    private UserBookRepository userBookRepository;

    @Autowired
    private BooksByUserRepository booksByUserRepository;

    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(
            @RequestBody MultiValueMap<String, String> formData,
            @AuthenticationPrincipal OAuth2User principal) {
        if(principal == null || principal.getAttribute("login") == null){
            return null;
        }
        String bookId = formData.getFirst("bookId");
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        Book book = optionalBook.get();
        int rating = Integer.parseInt(formData.getFirst("rating"));
        String readingStatus = formData.getFirst("readingStatus");
        String userId = principal.getAttribute("login");

        UserBook userBook = new UserBook();
        UserBookPrimaryKey primaryKey = new UserBookPrimaryKey();
        primaryKey.setUserId(userId);
        primaryKey.setBokId(bookId);
        userBook.setKey(primaryKey);
        userBook.setStartedDate(LocalDate.parse(formData.getFirst("startDate")));
        userBook.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBook.setRating(rating);
        userBook.setReadingStatus(readingStatus);
        userBookRepository.save(userBook);

        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userId);
        booksByUser.setBook_id(bookId);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIds());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(readingStatus);
        booksByUser.setRating(rating);
        booksByUserRepository.save(booksByUser);

        return new ModelAndView("redirect:/books/" + bookId);
    }
}