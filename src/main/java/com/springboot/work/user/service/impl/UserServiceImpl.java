package com.springboot.work.user.service.impl;

import com.springboot.work.auth.repository.PasswordResetTokenRepository;
import com.springboot.work.auth.repository.VerificationTokenRepository;
import com.springboot.work.quiz.entity.QuizResult;
import com.springboot.work.quiz.repository.QuizResultRepository;
import com.springboot.work.user.dto.UserInfoResponseDTO;
import com.springboot.work.user.dto.UserRequestDTO;
import com.springboot.work.user.dto.UserResponseDTO;
import com.springboot.work.user.entity.Users;
import com.springboot.work.user.repository.UserRepository;
import com.springboot.work.user.service.UserService;
import com.springboot.work.util.WorkBusinessException;
import com.springboot.work.util.WorkMessageDTO;
import com.springboot.work.util.WorkMessageType;
import com.springboot.work.util.WorkMessageUtil;

import com.springboot.work.word.entity.Word;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.springboot.work.util.WorkConstant.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final WorkMessageUtil workMessageUtil;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final QuizResultRepository quizResultRepository;




    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null || isAllFieldsEmpty(userRequestDTO)) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("gonderilen.istek.hatali", WorkMessageType.ERROR));

        }
        validateCreateUser(userRequestDTO);


        if (userRepository.findByUsername(userRequestDTO.getUsername()) != null) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("username.zaten.kayitli", WorkMessageType.ERROR));
        }
        Users users = new Users();


        users.setName(userRequestDTO.getName());
        users.setSurname(userRequestDTO.getSurname());
        users.setAge(userRequestDTO.getAge());
        users.setEmail(userRequestDTO.getEmail());
        users.setPassword(userRequestDTO.getPassword());
        users.setUsername(userRequestDTO.getUsername());

        userRepository.save(users);
        WorkMessageDTO successMessage = WorkMessageDTO.builder()
                .type(WorkMessageType.SUCCESS)
                .text(USER_SAVED_SUCCESS)
                .build();


        return UserResponseDTO.builder()
                .userId(users.getId())
                .msg(List.of(successMessage))
                .build();

    }


    public void validateCreateUser(UserRequestDTO userRequestDTO) {


        if (!StringUtils.hasText(userRequestDTO.getUsername())) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("username.bos.olamaz", WorkMessageType.ERROR));

        }
        if (!StringUtils.hasText(userRequestDTO.getName())) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("name.bos.olamaz", WorkMessageType.ERROR));
        }
        if (!StringUtils.hasText(userRequestDTO.getSurname())) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("surname.bos.olamaz", WorkMessageType.ERROR));

        }
        if (!StringUtils.hasText(userRequestDTO.getEmail())) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("mail.bos.olamaz", WorkMessageType.ERROR));

        }
        if (!StringUtils.hasText(userRequestDTO.getPassword())) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("password.bos.olamaz", WorkMessageType.ERROR));

        }
        if (!StringUtils.hasText(userRequestDTO.getAge())) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("age.bos.olamaz", WorkMessageType.ERROR));
        }
    }

    private boolean isAllFieldsEmpty(UserRequestDTO dto) {
        return !StringUtils.hasText(dto.getUsername()) &&
                !StringUtils.hasText(dto.getName()) &&
                !StringUtils.hasText(dto.getSurname()) &&
                !StringUtils.hasText(dto.getEmail()) &&
                !StringUtils.hasText(dto.getPassword()) &&
                !StringUtils.hasText(dto.getAge());
    }


    @Override
    @Transactional
    public UserResponseDTO deleteUser(Long id) {

       Users user= userRepository.findById(id)
                .orElseThrow(() -> new WorkBusinessException(
                        workMessageUtil.createWorkMessageWithCode("gecersiz.id.delete", WorkMessageType.ERROR, String.valueOf(id))
                ));


        String email = user.getEmail(); // silmeden önce maili al

        // 🔥 Token tablosundaki kayıtları sil
        verificationTokenRepository.deleteByEmail(email);
        passwordResetTokenRepository.deleteByEmail(email);

        userRepository.deleteById(id);

        WorkMessageDTO successMessage = WorkMessageDTO.builder()
                .type(WorkMessageType.SUCCESS)
                .text(USER_DELETED)
                .build();

        return UserResponseDTO.builder()
                .userId(id)
                .msg(List.of(successMessage))
                .build();


    }

    @Override
    @Transactional
    public Users getUserById(Long id) {

        return userRepository.findById(id).orElseThrow(() ->
                new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("gecersiz.id", WorkMessageType.ERROR))
        );

    }


    @Override
    @Transactional
    public UserResponseDTO updateUser(UserRequestDTO userRequestDTO, Long id) {

        if (userRequestDTO == null || isAllFieldsEmpty(userRequestDTO)) {
            throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("gonderilen.istek.hatali", WorkMessageType.ERROR));

        }
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new WorkBusinessException(
                        workMessageUtil.createWorkMessageWithCode("kullanici.bulunamadi", WorkMessageType.ERROR, String.valueOf(id))
                ));

        validateCreateUser(userRequestDTO);

        existingUser.setName(userRequestDTO.getName());
        existingUser.setSurname(userRequestDTO.getSurname());
        existingUser.setAge(userRequestDTO.getAge());
        existingUser.setEmail(userRequestDTO.getEmail());
        existingUser.setPassword(userRequestDTO.getPassword());
        existingUser.setUsername(userRequestDTO.getUsername());

        userRepository.save(existingUser);

        WorkMessageDTO updateMessages = WorkMessageDTO.builder()
                .type(WorkMessageType.UPDATES)
                .text(USER_UPDATES)
                .build();

        return UserResponseDTO.builder()
                .userId(existingUser.getId())
                .msg(List.of(updateMessages))
                .build();


    }

    public List<Users> getAllList() {
        return userRepository.findAll();
    }


    public List<Users> getUserByName(String name) {
        return userRepository.findByNameIgnoreCase(name);
    }

    public Users getUserByUserName(String username) {
       if(!StringUtils.hasText(username)) {
           throw new WorkBusinessException(workMessageUtil.createWorkMessageWithCode("gonderilen.istek.hatali", WorkMessageType.ERROR));

       }

        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new WorkBusinessException(
                    workMessageUtil.createWorkMessageWithCode("gecersiz.username", WorkMessageType.ERROR)
            );
        }

        return userRepository.findByUsername(username);
    }

    @Override
    public UserInfoResponseDTO getUserInfo(String email) {

        return userRepository
                .findByEmailIgnoreCase(email)           // Optional<Users>
                .map(this::toUserInfoDto)               // Optional<UserInfoResponseDTO>
                .orElseThrow(() ->                     // UserInfoResponseDTO
                        new EntityNotFoundException("User not found")
                );
    }

    /* ---------- yardımcı dönüştürme ---------- */
    private UserInfoResponseDTO toUserInfoDto(Users user) {


        /* 1) Kullanıcının tüm quiz sonuçlarını çek */
        List<QuizResult> results = quizResultRepository.findByUserEmail(user.getEmail());

        int totalQuestions = results.stream()
                .mapToInt(QuizResult::getTotalQuestions)
                .sum();
        int totalCorrect   = results.stream()
                .mapToInt(QuizResult::getCorrectAnswers)
                .sum();

        double successRate = totalQuestions == 0
                ? 0.0
                : (double) totalCorrect / totalQuestions;   // 0-1 arası oran

        double rate = successRate * 100;

        /* 2) DTO’yu doldur */
        UserInfoResponseDTO dto = new UserInfoResponseDTO();
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setUsername(user.getUsername());
        dto.setAge(String.valueOf(user.getAge()));
        dto.setEmail(user.getEmail());
        dto.setSuccessRate(rate);
        dto.setLevel("A1");                       // ← seviyeyi nasıl hesaplıyorsan

        return dto;
    }

}

