package com.sparta.springclonecoding.controller;

import com.sparta.springclonecoding.dto.DetailDto;
import com.sparta.springclonecoding.dto.PostResponseDto;
import com.sparta.springclonecoding.dto.ProfileDto;
import com.sparta.springclonecoding.security.UserDetailsImpl;
import com.sparta.springclonecoding.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @ExceptionHandler(IllegalArgumentException.class)
    public String nullex(IllegalArgumentException e) {
        return e.getMessage();
    }
    // 게시글 작성
    @PostMapping("/api/posts")
    public PostResponseDto savePost (@RequestParam(value = "multipartFile") MultipartFile multipartFile,
                                     @RequestParam("content") String content,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        if (multipartFile.isEmpty()){
            throw new IllegalArgumentException("사진을 첨부해 주세용!");
        }
        return postService.postPost(multipartFile, content, userDetails);
    }

    // 게시글 조회
    @GetMapping("/api/posts/{loadPost}")
    public List<PostResponseDto> showPost(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable int loadPost) {
        return postService.getPost(userDetails, loadPost);

    }
    
    // 게시글 수정
    @PutMapping("/api/posts/{postId}")
    public PostResponseDto updatePost (@PathVariable Long postId,
                                             @RequestParam("content") String content,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails ) throws IOException{
        return  postService.putPost(postId, content, userDetails.getUser().getId());
    }
    
    // 게시글 삭제
    @DeleteMapping("/api/posts/{postId}")
    public Long deletePost (@PathVariable Long postId) {
        return postService.delPost(postId);
    }

    // 상세페이지
    @GetMapping("/api/detail/{postid}/{loadComment}")
    public DetailDto showDetail(@PathVariable Long postid,
                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                @PathVariable int loadComment){
        return postService.showDetail(postid,userDetails,loadComment);
    }

    // 프로필 보기
    @GetMapping("/api/profile/{userid}")
    public ProfileDto showProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userid){
        return postService.showProfile(userDetails, userid);
    }

    // 프로필 수정
    @PutMapping("/api/profile")
    public void updatePost (@RequestParam("multipartFile") MultipartFile multipartFile, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        postService.editprofile(multipartFile,userDetails);
    }
}
