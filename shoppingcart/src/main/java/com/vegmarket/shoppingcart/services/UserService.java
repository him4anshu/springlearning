package com.vegmarket.shoppingcart.services;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vegmarket.shoppingcart.model.Role;
import com.vegmarket.shoppingcart.model.User;
import com.vegmarket.shoppingcart.repository.RoleRepository;
import com.vegmarket.shoppingcart.repository.UserRepository;

@Service
public class UserService {

	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private RoleRepository roleRepository;
    //private BCryptPasswordEncoder bCryptPasswordEncoder;

   /* @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder
                       ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }*/

   /* public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
*/
    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User saveUser(User user) {
        user.setPassword(user.getPassword());
        user.setActive(true);
        Role userRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

}