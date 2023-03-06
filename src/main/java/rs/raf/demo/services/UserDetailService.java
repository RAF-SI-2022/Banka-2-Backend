package rs.raf.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.raf.demo.model.User;
import rs.raf.demo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser = this.userRepository.findByMail(username);

        if(myUser == null) throw new UsernameNotFoundException("User not found in the database");

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        myUser.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName()) ));
        return  null; //new org.springframework.security.core.userdetails.User(myUser.getMail(), myUser.getPassword(), authorities);
    }
}