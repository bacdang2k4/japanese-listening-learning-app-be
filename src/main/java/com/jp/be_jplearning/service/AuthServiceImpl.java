package com.jp.be_jplearning.service;

import com.jp.be_jplearning.common.BusinessException;
import com.jp.be_jplearning.dto.*;
import com.jp.be_jplearning.entity.Admin;
import com.jp.be_jplearning.entity.Learner;
import com.jp.be_jplearning.entity.Profile;
import com.jp.be_jplearning.repository.AdminRepository;
import com.jp.be_jplearning.repository.LearnerRepository;
import com.jp.be_jplearning.repository.ProfileRepository;
import com.jp.be_jplearning.security.CustomUserDetails;
import com.jp.be_jplearning.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LearnerRepository learnerRepository;
    private final AdminRepository adminRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public RegisterResponse registerLearner(RegisterRequest request) {
        if (learnerRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Tên đăng nhập '" + request.getUsername() + "' đã tồn tại");
        }
        if (learnerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email '" + request.getEmail() + "' đã được sử dụng");
        }

        Learner learner = new Learner();
        learner.setUsername(request.getUsername());
        learner.setEmail(request.getEmail());
        learner.setFirstName(request.getFirstName());
        learner.setLastName(request.getLastName());
        learner.setPassword(passwordEncoder.encode(request.getPassword()));
        learner.setCreatedAt(LocalDateTime.now());

        Learner savedLearner = learnerRepository.save(learner);

        return RegisterResponse.builder()
                .learnerId(savedLearner.getId())
                .username(savedLearner.getUsername())
                .email(savedLearner.getEmail())
                .firstName(savedLearner.getFirstName())
                .lastName(savedLearner.getLastName())
                .avatarUrl(savedLearner.getAvatarUrl())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LearnerAuthResponse loginLearner(LoginRequest request) {
        // Check if username exists first
        Learner learner = learnerRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Tài khoản '" + request.getUsername() + "' không tồn tại"));

        // Block login if account is locked
        if (learner.getStatus() == com.jp.be_jplearning.entity.enums.LearnerStatusEnum.LOCKED) {
            throw new BusinessException("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên.");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_LEARNER"))) {
                throw new BusinessException("Tài khoản này không phải là tài khoản học viên");
            }

            Long profileId = profileRepository.findByLearnerId(learner.getId()).stream()
                    .findFirst().map(Profile::getId).orElse(null);

            String token = jwtUtil.generateToken(userDetails, learner.getId(), "ROLE_LEARNER");

            return LearnerAuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .learnerId(learner.getId())
                    .profileId(profileId)
                    .username(learner.getUsername())
                    .role("ROLE_LEARNER")
                    .firstName(learner.getFirstName())
                    .lastName(learner.getLastName())
                    .avatarUrl(learner.getAvatarUrl())
                    .build();

        } catch (BadCredentialsException e) {
            throw new BusinessException("Mật khẩu không đúng");
        } catch (BusinessException e) {
            throw e; // Re-throw our own exceptions
        } catch (Exception e) {
            throw new BusinessException("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AdminAuthResponse loginAdmin(LoginRequest request) {
        // Check if admin username exists first
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(
                        () -> new BusinessException("Tài khoản admin '" + request.getUsername() + "' không tồn tại"));

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new BusinessException("Tài khoản này không có quyền quản trị viên");
            }

            String token = jwtUtil.generateToken(userDetails, admin.getId(), "ROLE_ADMIN");

            return AdminAuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .adminId(admin.getId())
                    .username(admin.getUsername())
                    .role("ROLE_ADMIN")
                    .build();

        } catch (BadCredentialsException e) {
            throw new BusinessException("Mật khẩu admin không đúng");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Đăng nhập admin thất bại: " + e.getMessage());
        }
    }
}
