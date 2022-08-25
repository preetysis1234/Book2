package com.megaIT.book.springboot.web;

import com.fasterxml.jackson.databind.Module;
import com.megaIT.book.springboot.config.auth.LoginUser;
import com.megaIT.book.springboot.config.auth.dto.SessionUser;
import com.megaIT.book.springboot.service.PostsService;
import com.megaIT.book.springboot.web.dto.PostsResponseDto;
import jdk.internal.icu.text.NormalizerBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user){
        model.addAttribute("posts",postsService.findAllDesc());

        /*자원낭비가 심해서 사용하지 않는다.*/
        /*SessionUser user = (SessionUser) httpSession.getAttribute("user");*/
        if(user !=null){
            model.addAttribute("userName1",user.getName());
        }
        return "index";
    }
    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postUpdate(@PathVariable Long id, Model model){
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post",dto);
        return "posts-update";
    }



}
