package com.ServeTech.Webapp.security;

import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MyUserDetailService  implements UserDetailsService {
    private final UserRepository userRepository ;

    public MyUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );
        return new UserPrincipal(user) ;
    }
}
