package com.vegmarket.shoppingcart.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vegmarket.shoppingcart.entity.Product;
import com.vegmarket.shoppingcart.entity.User;
import com.vegmarket.shoppingcart.form.CustomerForm;
import com.vegmarket.shoppingcart.model.CartInfo;
import com.vegmarket.shoppingcart.model.CustomerInfo;
import com.vegmarket.shoppingcart.model.ProductInfo;
import com.vegmarket.shoppingcart.services.UserService;
import com.vegmarket.shoppingcart.util.Utils;


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
	        List<ProductInfo> products = userService.getAllProduct();
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
	        List<ProductInfo> products = userService.getAllProduct();
			modelAndView.addObject("products", products);
	        modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		
		 modelAndView.setViewName("home");
		 return modelAndView;
	 }
	 
	 @GetMapping(value="/admin/home")
	    public ModelAndView adminHome(){
	        ModelAndView modelAndView = new ModelAndView();
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByUserName(auth.getName());
	        modelAndView.addObject("userName", "Welcome " + user.getUserName() + "/" + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
	        modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
	        modelAndView.setViewName("admin/home");
	        return modelAndView;
	    }
	 
	 @GetMapping(value="/buyProduct/{prdCode}")
	 public String addToCart(@PathVariable(value = "prdCode", required = false) String prdCode, HttpServletRequest request) {
		 Product product = null;
	      if (prdCode != null && prdCode.length() > 0) {
	         product = userService.findProduct(prdCode).get();
	      }
	      if (product != null) {
	         CartInfo cartInfo = Utils.getCartInSession(request); // this will give prevoius added item
	         ProductInfo productInfo = new ProductInfo(product);
	         cartInfo.addProduct(productInfo, 1); // this will add new item into prevoius session
	      }
	      return "redirect:/shoppingCart";
	 }
	 
	 @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.GET)
	   public String shoppingCartHandler(HttpServletRequest request, Model model) {
	      CartInfo myCart = Utils.getCartInSession(request);
	 
	      model.addAttribute("cartForm", myCart);
	      return "shoppingCart";
	   }
	 
	 @RequestMapping({ "/shoppingCartRemoveProduct" })
	   public String removeProductHandler(HttpServletRequest request, Model model, //
	         @RequestParam(value = "code", defaultValue = "") String code) {
	      Product product = null;
	      if (code != null && code.length() > 0) {
	         product = userService.findProduct(code).get();
	      }
	      if (product != null) {
	 
	         CartInfo cartInfo = Utils.getCartInSession(request);
	 
	         ProductInfo productInfo = new ProductInfo(product);
	 
	         cartInfo.removeProduct(productInfo);
	 
	      }
	 
	      return "redirect:/shoppingCart";
	   }
	 
	  @RequestMapping(value = { "/shoppingCart" }, method = RequestMethod.POST)
	   public String shoppingCartUpdateQty(HttpServletRequest request, //
	         Model model, //
	         @ModelAttribute("cartForm") CartInfo cartForm) {
	 
	      CartInfo cartInfo = Utils.getCartInSession(request);
	      cartInfo.updateQuantity(cartForm);
	 
	      return "redirect:/shoppingCart";
	   }
	  
	  @RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.GET)
	   public String shoppingCartCustomerForm(HttpServletRequest request, Model model) {
	 
	      CartInfo cartInfo = Utils.getCartInSession(request);
	 
	      if (cartInfo.isEmpty()) {
	 
	         return "redirect:/shoppingCart";
	      }
	      CustomerInfo customerInfo = cartInfo.getCustomerInfo();
	 
	      CustomerForm customerForm = new CustomerForm(customerInfo);
	 
	      model.addAttribute("customerForm", customerForm);
	 
	      return "shoppingCartCustomer";
	   }
	  
	  @RequestMapping({ "/productList" })
	   public String listProductHandler(Model model, //
	         @RequestParam(value = "name", defaultValue = "") String likeName,
	         @RequestParam(value = "page", defaultValue = "1") int page) {
	      final int maxResult = 5;
	      final int maxNavigationPage = 10;
	 
	      List<ProductInfo> products = userService.getAllProduct();
	      model.addAttribute("products", products);
	      return "productList";
	   }
	
	  // POST: Save customer information.
	   @RequestMapping(value = { "/shoppingCartCustomer" }, method = RequestMethod.POST)
	   public String shoppingCartCustomerSave(HttpServletRequest request, //
	         Model model, //
	         @ModelAttribute("customerForm") @Validated CustomerForm customerForm, //
	         BindingResult result, //
	         final RedirectAttributes redirectAttributes) {
	 
	      if (result.hasErrors()) {
	         customerForm.setValid(false);
	         // Forward to reenter customer info.
	         return "shoppingCartCustomer";
	      }
	 
	      customerForm.setValid(true);
	      CartInfo cartInfo = Utils.getCartInSession(request);
	      CustomerInfo customerInfo = new CustomerInfo(customerForm);
	      cartInfo.setCustomerInfo(customerInfo);
	 
	      return "redirect:/shoppingCartConfirmation";
	   }
	   
	// GET: Show information to confirm.
	   @RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.GET)
	   public String shoppingCartConfirmationReview(HttpServletRequest request, Model model) {
	      CartInfo cartInfo = Utils.getCartInSession(request);
	 
	      if (cartInfo == null || cartInfo.isEmpty()) {
	 
	         return "redirect:/shoppingCart";
	      } else if (!cartInfo.isValidCustomer()) {
	 
	         return "redirect:/shoppingCartCustomer";
	      }
	      model.addAttribute("myCart", cartInfo);
	 
	      return "shoppingCartConfirmation";
	   }
	 
	   // POST: Submit Cart (Save)
	   @RequestMapping(value = { "/shoppingCartConfirmation" }, method = RequestMethod.POST)
	 
	   public String shoppingCartConfirmationSave(HttpServletRequest request, Model model) {
	      CartInfo cartInfo = Utils.getCartInSession(request);
	 
	      /*if (cartInfo.isEmpty()) {
	 
	         return "redirect:/shoppingCart";
	      } else if (!cartInfo.isValidCustomer()) {
	 
	         return "redirect:/shoppingCartCustomer";
	      }*/
	      try {
	         userService.saveOrder(cartInfo);
	      } catch (Exception e) {
	 
	         return "shoppingCartConfirmation";
	      }
	 
	      // Remove Cart from Session.
	      Utils.removeCartInSession(request);
	 
	      // Store last cart.
	      Utils.storeLastOrderedCartInSession(request, cartInfo);
	 
	      return "redirect:/shoppingCartFinalize";
	   }
	 
	   @RequestMapping(value = { "/shoppingCartFinalize" }, method = RequestMethod.GET)
	   public String shoppingCartFinalize(HttpServletRequest request, Model model) {
	 
	      CartInfo lastOrderedCart = Utils.getLastOrderedCartInSession(request);
	 
	      if (lastOrderedCart == null) {
	         return "redirect:/shoppingCart";
	      }
	      model.addAttribute("lastOrderedCart", lastOrderedCart);
	      return "shoppingCartFinalize";
	   }
	   
	   @RequestMapping(value = { "/productImage" }, method = RequestMethod.GET)
	   public void productImage(HttpServletRequest request, HttpServletResponse response, Model model,
	         @RequestParam("code") String code) throws IOException {
	      Product product = null;
	      if (code != null) {
	         product = this.userService.findProduct(code).get();
	      }
	      if (product != null && product.getImage() != null) {
	         response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
	         response.getOutputStream().write(product.getImage());
	      }
	      response.getOutputStream().close();
	   }
	}
