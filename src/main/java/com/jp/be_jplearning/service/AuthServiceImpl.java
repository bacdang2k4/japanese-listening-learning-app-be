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
            throw new BusinessException("Username already exists");
        }
        if (learnerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        Learner learner = new Learner();
        learner.setUsername(request.getUsername());
        learner.setEmail(request.getEmail());
        learner.setPassword(passwordEncoder.encode(request.getPassword()));
        learner.setCreatedAt(LocalDateTime.now());

        Learner savedLearner = learnerRepository.save(learner);

        Profile profile = new Profile();
        profile.setLearner(savedLearner);
        profile.setStartDate(LocalDateTime.now());
        profileRepository.save(profile);

        return RegisterResponse.builder()
                .learnerId(savedLearner.getId())
                .username(savedLearner.getUsername())
                .email(savedLearner.getEmail())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public LearnerAuthResponse loginLearner(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Learner learner = learnerRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("Learner not found"));

        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_LEARNER"))) {
            throw new BusinessException("Not found as learner");
        }

        Profile profile = profileRepository.findByLearnerId(learner.getId()).stream().findFirst()
                .orElseThrow(() -> new BusinessException("Profile not found for learner"));

        String token = jwtUtil.generateToken(userDetails, learner.getId(), "ROLE_LEARNER");

        return LearnerAuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .learnerId(learner.getId())
                .profileId(profile.getId())
                .username(learner.getUsername())
                .role("ROLE_LEARNER")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminAuthResponse loginAdmin(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new BusinessException("Not found as admin");
        }

        Admin admin = adminRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException("Admin not found"));

        String token = jwtUtil.generateToken(userDetails, admin.getId(), "ROLE_ADMIN");

        return AdminAuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .adminId(admin.getId())
                .username(admin.getUsername())
                .role("ROLE_ADMIN")
                .build();
    }
}
