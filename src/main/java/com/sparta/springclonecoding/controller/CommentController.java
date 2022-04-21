package com.sparta.springclonecoding.controller;

import com.sparta.springclonecoding.dto.CommentRequestDto;
import com.sparta.springclonecoding.model.Comment;
import com.sparta.springclonecoding.model.Post;
import com.sparta.springclonecoding.service.CommentService;
import lombok.RequiredArgsConstructor;
import com.sparta.springclonecoding.security.UserDetailsImpl;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentservice;

    @ExceptionHandler(IllegalArgumentException.class)
    public String nullex(IllegalArgumentException e) {
        return e.getMessage();
    }
    // 댓글 작성
    @PostMapping("/api/comment")
    public Optional<Post> registComment(@RequestBody CommentRequestDto commentRequestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userid = userDetails.getUser().getId();
        return commentservice.registComment(commentRequestDto,userid);
    }

    @DeleteMapping("/api/comment/{commentid}")
    public Comment deleteComment(@PathVariable Long commentid){
        return commentservice.deleteComment(commentid);
    }
}
