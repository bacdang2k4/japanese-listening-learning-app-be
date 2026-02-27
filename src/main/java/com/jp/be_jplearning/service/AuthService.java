package com.jp.be_jplearning.service;

import com.jp.be_jplearning.dto.AdminAuthResponse;
import com.jp.be_jplearning.dto.LearnerAuthResponse;
import com.jp.be_jplearning.dto.LoginRequest;
import com.jp.be_jplearning.dto.RegisterRequest;
import com.jp.be_jplearning.dto.RegisterResponse;

public interface AuthService {
    RegisterResponse registerLearner(RegisterRequest request);

    LearnerAuthResponse loginLearner(LoginRequest request);

    AdminAuthResponse loginAdmin(LoginRequest request);
}
