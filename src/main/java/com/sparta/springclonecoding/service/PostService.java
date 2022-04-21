package com.sparta.springclonecoding.service;

import com.sparta.springclonecoding.dto.*;
import com.sparta.springclonecoding.model.*;
import com.sparta.springclonecoding.repository.*;
import com.sparta.springclonecoding.security.UserDetailsImpl;
import com.sparta.springclonecoding.util.ConvertTime;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final FollowRepository followRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final ConvertTime convertTime;

    // 회원 프로필
    public ProfileDto showProfile(UserDetailsImpl userDetails, Long userid){
        User user = userRepository.findById(userid).orElseThrow(
                () -> new IllegalArgumentException("없는 유저입니다."));
        // 게시글 목록
        List<Post> postList = postRepository.findByUserIdOrderByIdDesc(userid);
        // 게시글 수
        int postCnt = postRepository.countAllByUserId(userid);
        // 로그인 된 유저와 프로필 유저 일치 여부
        boolean isLoginUser = userid.equals(userDetails.getUser().getId());
        // 팔로우 여부
        boolean followState = followRepository.existsByFromUserAndToUser(userDetails.getUser(), user);
        // 해당 프로필을 팔로우한 유저(팔로워) 수
        Long userFollowerCnt = followRepository.countAllByToUser(user);
        // 해당 프로필이 팔로우한 유저(팔로잉) 수
        Long userFollowingCnt = followRepository.countAllByFromUser(user);
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for(Post post : postList){
           postResponseDtos.add(getPostResponseDto(userDetails.getUser().getId(),post));
        }

        return new ProfileDto(user, postResponseDtos, postCnt, isLoginUser, followState, userFollowerCnt, userFollowingCnt);
    }

    // 상세 페이지 + 댓글 페이징 처리
    public DetailDto showDetail(Long postid, UserDetailsImpl userDetails, int loadComment){
        Post post =postRepository.findById(postid).orElseThrow(
                ()-> new IllegalArgumentException("없는 포스트입니다"));
        Boolean myLike = false;
        Long userid =userDetails.getUser().getId();
       if(favoriteRepository.findByUseridAndPostid(userid,postid).isPresent()){
           myLike = true;
       }
       List<Favorite> favorites = favoriteRepository.findAllByPostid(postid);
        // 코멘트 리스트 불러와서 페이징처리
        List<Comment> commentList = commentRepository.findAllByIdOrderByIdDesc(postid);
        final int end = Math.min(loadComment+10 , commentList.size());
        commentList.subList(loadComment,end);

        return new DetailDto(post,favorites.size(),myLike);
    }


    // 게시글 저장
    public PostResponseDto postPost (MultipartFile multipartFile, String content, UserDetailsImpl userDetails) throws IOException {
        String imageUrl = s3Service.upload(multipartFile);
        Post post = new Post(content,imageUrl,userDetails);
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("계정이 없습니다.")
        );
        user.getPosts().add(post);
        postRepository.save(post);

        return getPostResponseDto(user.getId(), post);
    }

    // 게시글 목록 조회 - 게시글 리스트를 반복문으로 꺼내서 각 게시글의 각 코멘트 갯수 보여주고 다시 담아주기
    public List<PostResponseDto> getPost (UserDetailsImpl userDetails,
                                          int loadPost) {

        List<Post> posts = postRepository.findAllByOrderByIdDesc();
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        Long userId = userDetails.getUser().getId();
        if (userId == null) {
            throw new IllegalArgumentException("계정이 없습니다.");
        }

        for (Post post : posts) {
            // 해당 게시물에 대한 사용자의 좋아요 확인 -> 각 포스트마다 좋아요한 리스트에 userid가 사용자와 일치하면 true로 변환
            PostResponseDto postResponseDto = getPostResponseDto(userId, post);
            postResponseDtos.add(postResponseDto);
        }

        // list를 page으로 바꿔서 리턴
        final int end = Math.min(loadPost+3 , postResponseDtos.size());
        return postResponseDtos.subList(0,end);
    }

    @NotNull
    private PostResponseDto getPostResponseDto(Long userid, Post post) {
        boolean myLike = false;
        if(favoriteRepository.findByUseridAndPostid(userid,post.getId()).isPresent()){
            myLike = true;
        }
        User user = userRepository.findByPosts(post);

        // 댓글 갯수
        int commentCnt = 0;
        if (!post.getComments().isEmpty()) {
            commentCnt = post.getComments().size();
        }

        // 좋아요 수
        int favoriteCnt =favoriteRepository.findAllByPostid(post.getId()).size();

        List<CommentResponseDto> commentList = new ArrayList<>();

        for(Comment comment : post.getComments()) {

            String nickname = userRepository.findById(comment.getUserid()).get().getNickname();

            CommentResponseDto commentResponseDto = new CommentResponseDto(comment, nickname);
            commentList.add(commentResponseDto);
        }
        List<Favorite> favorites = favoriteRepository.findAllByPostid(post.getId());
        String timeBefore = convertTime.convertLocaldatetimeToTime(post.getCreatedAt());

        PostResponseDto postResponseDto = new PostResponseDto(post, favorites,
                commentList, commentCnt,favoriteCnt,myLike,new UserResponseDto(user),timeBefore);
        return postResponseDto;
    }



    // 게시글 수정
    @Transactional
    public PostResponseDto putPost(Long postId, String content, Long userId) throws IOException{
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 없습니다.")
        );
        post.update(content);

        return getPostResponseDto(userId, post);
    }



    // 게시글 삭제
    public Long delPost(Long postId) {
        postRepository.deleteById(postId);
        return postId;
    }

    // 프로필 수정
    @Transactional
    public void editprofile(MultipartFile multipartFile,UserDetailsImpl userDetails) throws IOException{
        String imageUrl = s3Service.upload(multipartFile,"static");
        User user = userRepository.findById(userDetails.getId()).orElseThrow(
                ()-> new IllegalArgumentException("없는 유저입니다.")
        );
        user.update(imageUrl);
    }
}
