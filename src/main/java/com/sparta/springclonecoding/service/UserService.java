package com.sparta.springclonecoding.service;

import com.sparta.springclonecoding.dto.ResultDto;
import com.sparta.springclonecoding.dto.SignupRequestDto;
import com.sparta.springclonecoding.model.User;
import com.sparta.springclonecoding.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import static com.sparta.springclonecoding.exception.ExceptionMessage.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // 회원가입
    public ResultDto save(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String message;
        Optional<User> foundUser = userRepository.findByUsername(username);
        Optional<User> foundNick = userRepository.findByNickname(nickname);

        // username 중복 체크
        if(validatedDuplicateUsername(foundUser)) {
            message = ILLEGAL_USER_NAME_DUPLICATION;
            return new ResultDto(false, message);
        }

        // nickname 중복 체크
        if(validatedDuplicateNickname(foundNick)) {
            message = ILLEGAL_NICK_NAME_DUPLICATION;
            return new ResultDto(false, message);
        }

//        // password 일치 여부
//        if(!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
//            message = ILLEGAL_PASS_WORD_DUPLICATION;
//            return new ResultDto(false, message);
//        }

        // 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

        User user = new User(username, nickname, password,"https://media.istockphoto.com/vectors/default-profile-picture-avatar-photo-placeholder-vector-illustration-vector-id1223671392");
        userRepository.save(user);
        message = "회원가입 성공";
        return new ResultDto(true, message);
    }

    // 유저이름 중복 체크
    private boolean validatedDuplicateUsername(Optional<User> found) {
        return found.isPresent();
    }
    // 닉네임 중복 체크
    private boolean validatedDuplicateNickname(Optional<User> found) {
        return found.isPresent();
    }
}
