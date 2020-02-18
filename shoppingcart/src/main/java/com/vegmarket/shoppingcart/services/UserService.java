package com.vegmarket.shoppingcart.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vegmarket.shoppingcart.entity.Order;
import com.vegmarket.shoppingcart.entity.OrderDetail;
import com.vegmarket.shoppingcart.entity.Product;
import com.vegmarket.shoppingcart.entity.Role;
import com.vegmarket.shoppingcart.entity.User;
import com.vegmarket.shoppingcart.model.CartInfo;
import com.vegmarket.shoppingcart.model.CartLineInfo;
import com.vegmarket.shoppingcart.model.CustomerInfo;
import com.vegmarket.shoppingcart.model.ProductInfo;
import com.vegmarket.shoppingcart.repository.OrderDetailRepository;
import com.vegmarket.shoppingcart.repository.OrderRepository;
import com.vegmarket.shoppingcart.repository.ProductRepository;
import com.vegmarket.shoppingcart.repository.RoleRepository;
import com.vegmarket.shoppingcart.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    
    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder
                       ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public List<ProductInfo> getAllProduct(){
    	List<Product> products = productRepository.findAll();
    	
    	//Using Stream
    	return products.stream().map(new Function<Product, ProductInfo>() {
			@Override
			public ProductInfo apply(Product p) {
				return new ProductInfo(p.getCode(), p.getName(), p.getPrice());
			}
		}).collect(Collectors.toList());
    	
    	//Using Lamda
    	//return products.stream().map(p -> new ProductInfo(p.getCode(), p.getName(), p.getPrice())).collect(Collectors.toList());
    }
    
    public Product getProductById(Integer id) {
    	return null;
    }

	public Optional<Product> findProduct(String prdCode) {
		return productRepository.findById(prdCode);
	}

	public void saveOrder(CartInfo cartInfo) {
		Random random = new Random();
		int orderNum = random.nextInt();
        Order order = new Order();
 
        order.setId(UUID.randomUUID().toString());
        order.setOrderNum(orderNum);
        order.setOrderDate(new Date());
        order.setAmount(cartInfo.getAmountTotal());
 
        CustomerInfo customerInfo = cartInfo.getCustomerInfo();
        order.setCustomerName(customerInfo.getName());
        order.setCustomerEmail(customerInfo.getEmail());
        order.setCustomerPhone(customerInfo.getPhone());
        order.setCustomerAddress(customerInfo.getAddress());
 
        orderRepository.save(order);
 
        List<CartLineInfo> lines = cartInfo.getCartLines();
 
        for (CartLineInfo line : lines) {
            OrderDetail detail = new OrderDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setOrder(order);
            detail.setAmount(line.getAmount());
            detail.setPrice(line.getProductInfo().getPrice());
            detail.setQuanity(line.getQuantity());
 
            String code = line.getProductInfo().getCode();
            Product product = findProduct(code).get();
            detail.setProduct(product);
 
            orderDetailRepository.save(detail);
        }
 
        // Order Number!
        cartInfo.setOrderNum(orderNum);
		
		
	}
}