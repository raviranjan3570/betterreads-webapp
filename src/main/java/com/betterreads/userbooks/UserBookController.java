package com.betterreads.userbooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
public class UserBookController {

    @Autowired
    private UserBookRepository userBookRepository;

    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(
            @RequestBody MultiValueMap<String, String> formData,
            @AuthenticationPrincipal OAuth2User principal) {
        if(principal == null || principal.getAttribute("login") == null){
            return null;
        }
        UserBook userBook = new UserBook();
        UserBookPrimaryKey primaryKey = new UserBookPrimaryKey();
        primaryKey.setUserId(principal.getAttribute("login"));
        String bookId = formData.getFirst("bookId");
        primaryKey.setBokId(bookId);
        userBook.setKey(primaryKey);
        userBook.setStartedDate(LocalDate.parse(formData.getFirst("startDate")));
        userBook.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBook.setRating(Integer.parseInt(formData.getFirst("rating")));
        userBook.setReadingStatus(formData.getFirst("readingStatus"));
        userBookRepository.save(userBook);
        return new ModelAndView("redirect:/books/" + bookId);
    }
}
