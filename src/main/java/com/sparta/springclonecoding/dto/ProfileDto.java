package com.sparta.springclonecoding.dto;

import com.sparta.springclonecoding.model.Post;
import com.sparta.springclonecoding.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ProfileDto {
    private String username; // 유저 정보(username, nickname)
    private String nickname;
    private String progfileImg; // 프로필 이미지
    private int postCnt; // 게시글 수
    private List<PostResponseDto> postDtos; // 게시글 목록
    private boolean loginUser; // 이 페이지가 로그인한 유저의 프로필인지
    private boolean followState;
    private Long userFollowerCnt; // 해당 유저를 팔로우 하는 사람들의 수
    private Long userFollowingCnt; // 해당 유저가 팔로우 하는 사람들의 수


    public ProfileDto(User user, List<PostResponseDto> postDtos, int postCnt, boolean loginUser, boolean followState, Long userFollowerCnt, Long userFollowingCnt){
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.progfileImg= user.getProfile();
        this.postCnt = postCnt;
        this.postDtos = postDtos;
        this.loginUser = loginUser;
        this.followState = followState;
        this.userFollowerCnt = userFollowerCnt;
        this.userFollowingCnt = userFollowingCnt;
    }
}
