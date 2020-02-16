package com.vegmarket.shoppingcart.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.vegmarket.shoppingcart.model.Product;
import com.vegmarket.shoppingcart.model.User;
import com.vegmarket.shoppingcart.services.UserService;


@Controller
public class ShoppingCartController {

	@Autowired
	UserService userService;
	
	public ShoppingCartController(){
		System.out.println("============================ShoppingCartController constrcutr====================");
	}
	@GetMapping(value = { "/", "/login" })
	public ModelAndView login() {
		System.out.println("=========================ShoppingCartController.login()======================");
		ModelAndView modelAndView = new ModelAndView();
	    modelAndView.addObject("user", new User());
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@GetMapping(value = "/registration")
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}
	
	@PostMapping(value = "/registration")
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        
        System.out.println("USer enter data "+user.getName());
        System.out.println("USer enter data "+user.getLastName());
        
        User userExists = userService.findUserByUserName(user.getUserName());
        if (userExists != null) {
            bindingResult
                    .rejectValue("userName", "error.user",
                            "There is already a user registered with the user name provided");
       
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }
	/*@PostMapping(value = "/login")
    public ModelAndView login(@Valid User user, BindingResult bindingResult) {
		System.out.println(user.getUserName());
		ModelAndView modelAndView = new ModelAndView();
		String userName = user.getUserName();
		String userEntredPassword = user.getPassword();
		User userFromDB = userService.findUserByUserName(userName);
		if(userFromDB == null) {
			System.out.println("User is not avialable");
			modelAndView.setViewName("registration");
		}else {
			if(userEntredPassword.equals(userFromDB.getPassword())) {
				List<Product> products = userService.getAllProduct();
				modelAndView.addObject("products", products);
				modelAndView.setViewName("home");
			}else {
				System.out.println("Entred wrong password");
				 modelAndView.addObject("wrongPassword", "Your password is wrong");
				modelAndView.setViewName("login");
			}
		}
		return modelAndView;
	}*/
	
	 @GetMapping(value="/home")
	    public ModelAndView home(){
	        ModelAndView modelAndView = new ModelAndView();
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByUserName(auth.getName());
	        List<Product> products = userService.getAllProduct();
			modelAndView.addObject("products", products);
	        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
	        modelAndView.setViewName("home");
	        return modelAndView;
	    }
	 
	 @PostMapping(value="/updatepassword")
	 public ModelAndView updatePassowrd(@RequestParam(value = "oldpassword", required = false) String oldpassword,
			 @RequestParam(value = "newpassword", required = false) String newpassword) {
		 System.out.println("New Password : "+newpassword);
		 System.out.println("Old Password : "+oldpassword);
		 ModelAndView modelAndView = new ModelAndView();
		    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByUserName(auth.getName());
	        user.setPassword(newpassword);
	        user.setLastName("update last name");
	        userService.saveUser(user);
	        List<Product> products = userService.getAllProduct();
			modelAndView.addObject("products", products);
	        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		
		 modelAndView.setViewName("home");
		 return modelAndView;
	 }
	
	}
