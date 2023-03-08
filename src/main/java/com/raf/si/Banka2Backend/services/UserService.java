package com.raf.si.Banka2Backend.services;

import com.raf.si.Banka2Backend.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService, UserServiceInteface {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> myUser = this.findByEmail(username);
        if(myUser.isEmpty()) {
            throw new UsernameNotFoundException("User with email: " + username + " not found");
        }

        return new org.springframework.security.core.userdetails.User(myUser.get().getEmail(), myUser.get().getPassword(), new ArrayList<>());
    }

    public Optional<User> findByEmail(String email) {
        //TODO
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

//    @Override
//    public User updateUser(User user) {
//        return null;
//    }

    @Override
    public void deleteById(Long id) {

    }
}
