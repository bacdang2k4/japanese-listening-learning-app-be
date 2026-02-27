package com.jp.be_jplearning.security;

import com.jp.be_jplearning.entity.Admin;
import com.jp.be_jplearning.entity.Learner;
import com.jp.be_jplearning.repository.AdminRepository;
import com.jp.be_jplearning.repository.LearnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final LearnerRepository learnerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to load admin first
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            return new CustomUserDetails(
                    admin.getId(),
                    admin.getUsername(),
                    admin.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }

        // Try to load learner
        Learner learner = learnerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new CustomUserDetails(
                learner.getId(),
                learner.getUsername(),
                learner.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_LEARNER")));
    }
}
