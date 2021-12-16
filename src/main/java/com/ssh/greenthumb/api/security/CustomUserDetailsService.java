package com.ssh.greenthumb.api.security;


import com.ssh.greenthumb.api.common.exception.ResourceNotFoundException;
import com.ssh.greenthumb.api.dao.user.UserRepository;
import com.ssh.greenthumb.api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userDao.findByEmailAndIsDeleted(email, "n");

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userDao.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }

}

