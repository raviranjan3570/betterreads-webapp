package com.betterreads.book;

import java.util.Optional;

import com.betterreads.userbooks.UserBook;
import com.betterreads.userbooks.UserBookPrimaryKey;
import com.betterreads.userbooks.UserBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserBookRepository userBookRepository;

    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model,
            @AuthenticationPrincipal OAuth2User principal) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            String coverImageUrl = "/images/no-image.png";
            if (book.getCoverIds() != null && book.getCoverIds().size() > 0) {
                coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-L.jpg";
            }
            model.addAttribute("coverImage", coverImageUrl);
            model.addAttribute("book", book);
            if (principal != null && principal.getAttribute("login") != null) {
                String userId = principal.getAttribute("login");
                model.addAttribute("loginId", userId);
                UserBookPrimaryKey primaryKey = new UserBookPrimaryKey();
                primaryKey.setBokId(bookId);
                primaryKey.setUserId(userId);
                Optional<UserBook> optionalUserBook = userBookRepository.findById(primaryKey);
                if(optionalUserBook.isPresent()){
                    model.addAttribute("userBook", optionalUserBook.get());
                }else{
                    model.addAttribute("userBook", new UserBook());
                }
            }
            return "book";
        }
        return "book-not-found";
    }
}
