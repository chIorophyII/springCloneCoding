package com.sparta.springclonecoding.dto;


import com.sparta.springclonecoding.model.Favorite;
import com.sparta.springclonecoding.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long postid;
    private String imageUrl;
    private String content;
    private int favoriteCnt; // 좋아요 수
    private boolean myLike; // 해당 게시글에 대한 사용자의 좋아요 여부
    private UserResponseDto userResponseDto;
    private String timeBefore;

    private int commentCnt; // 댓글 갯수
    private LocalDateTime createdAt;
    private List<CommentResponseDto> comments;
    private List<Favorite> favorites;

    public PostResponseDto(Post post, List<Favorite> favorites,List<CommentResponseDto> comments, int commentCnt, int favoriteCnt,
                           boolean myLike, UserResponseDto userResponseDto,String timeBefore) {
        this.userResponseDto = userResponseDto;
        this.postid = post.getId();
        this.imageUrl = post.getImageUrl();
        this.content = post.getContent();
        this.favoriteCnt = favoriteCnt;
        this.myLike = myLike;
        this.commentCnt = commentCnt;
        this.createdAt = post.getCreatedAt();
        this.comments = comments;
        this.createdAt = post.getCreatedAt();
        this.favorites = favorites;
        this.timeBefore = timeBefore;
    }
}
