package com.example.demo.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Constants.ResponseCode;
import com.example.demo.Constants.WebConstants;
import com.example.demo.Model.Address;
import com.example.demo.Model.Bufcart;
import com.example.demo.Model.PlaceOrder;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.AddressRepository;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Response.cartResp;
import com.example.demo.Response.prodResp;
import com.example.demo.Response.response;
import com.example.demo.Response.serverResp;
import com.example.demo.Response.userResp;
import com.example.demo.util.Validator;
import com.example.demo.util.jwtUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private jwtUtil jwtutil;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private ProductRepository prodRepo;
	
	@Autowired
	private CartRepository catRepository;
	
	@Autowired 
	private OrderRepository ordRepo;
	
	
	@PostMapping("/signup")
	public ResponseEntity<serverResp> addUser(@Valid @RequestBody User user){
		
		serverResp resp=new serverResp();
		try
		{
			if(Validator.isUserEmpty(user))
			{
				resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
				resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
			}
			else if(!Validator.isValidEmail(user.getEmail()))
			{
				resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
				resp.setMessage(ResponseCode.INVALID_EMAIL_FAIL_MSG);
			}
			else
			{
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.CUST_REG);
				User reg=userRepository.save(user);
				resp.setObject(reg);
			}
		} 
		catch (Exception e) {
		resp.setStatus(ResponseCode.FAILURE_CODE);
		resp.setMessage(e.getMessage());
		}
	return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
	}
	
	
	@PostMapping("/verify")
	public ResponseEntity<serverResp> verifyUser(@Valid @RequestBody Map<String, String> credential) {

		String email = "";
		String password = "";
		if (credential.containsKey(WebConstants.USER_EMAIL)) {
			email = credential.get(WebConstants.USER_EMAIL);
		}
		if (credential.containsKey(WebConstants.USER_PASSWORD)) {
			password = credential.get(WebConstants.USER_PASSWORD);
		}
		User loggedUser = userRepository.findByEmailAndPasswordAndUsertype(email, password, WebConstants.USER_CUST_ROLE);
		serverResp resp = new serverResp();
		if (loggedUser != null) {
			String jwtToken = jwtutil.createToken(email, password, WebConstants.USER_CUST_ROLE);
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.SUCCESS_MESSAGE);
			resp.setAUTH_TOKEN(jwtToken);
		} else {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<serverResp>(resp, HttpStatus.OK);
	}
	
	@PostMapping("/addAddress")
	public ResponseEntity<userResp> addAddress(@Valid @RequestBody Address address,
											   @RequestHeader(name=WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN){
		userResp resp=new userResp();
		if(Validator.isAddressEmpty(address))
		{
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		}
		else if(!Validator.isStringEmpty(AUTH_TOKEN) && jwtutil.checkToken(AUTH_TOKEN) != null)
		{
			try
			{
				User user=jwtutil.checkToken(AUTH_TOKEN);
				user.setAddress(address);
				address.setUser(user);
				Address adr=addressRepository.saveAndFlush(address);
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.SUCCESS_MESSAGE);
				resp.setUser(user);
				resp.setAddress(adr);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		    catch (Exception e) 
			{
		    	resp.setStatus(ResponseCode.FAILURE_CODE);
		    	resp.setMessage(e.getMessage());
		    	resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		} 
		else 
		{
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
	return new ResponseEntity<userResp>(resp, HttpStatus.ACCEPTED);	
	}
	
	
	@PostMapping("/getAddress")
	public ResponseEntity<response> getAddress(@RequestHeader(name=WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN){
		response resp=new response();
		if(jwtutil.checkToken(AUTH_TOKEN) != null)
		{
			try
			{
			User user = jwtutil.checkToken(AUTH_TOKEN);
			Address adr=addressRepository.findByUser(user);
			HashMap<String,String> map = new HashMap<>();
			map.put(WebConstants.ADR_CITY, adr.getCity());
			map.put(WebConstants.ADR_COUNTRY, adr.getCountry());
			map.put(WebConstants.ADR_STATE, adr.getState());
			map.put(WebConstants.ADR_NAME, adr.getAddress());
			map.put(WebConstants.ADR_ZP, String.valueOf(adr.getZipcode()));
			map.put(WebConstants.PHONE, adr.getPhonenumber());
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.SUCCESS_MESSAGE);
			resp.setMap(map);
			resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
			catch (Exception e)
			{
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		}
		else
		{
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<response>(resp, HttpStatus.ACCEPTED);
	}
	
	
	@PostMapping("/getProducts")
	public ResponseEntity<prodResp> getProducts(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN)
			throws IOException {

		prodResp resp = new prodResp();
		if (!Validator.isStringEmpty(AUTH_TOKEN) && jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.LIST_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(prodRepo.findAll());
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		} else {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
	}
	
	
	@GetMapping(path = { "/addToCart/{productid}" })
	public ResponseEntity<serverResp> addToCart(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
												@PathVariable("productid") String productId){
		serverResp resp=new serverResp();
		if(!Validator.isStringEmpty(AUTH_TOKEN) && jwtutil.checkToken(AUTH_TOKEN) != null)
		{
			try
			{
				User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
				Product product=prodRepo.findByProductid(Integer.parseInt(productId));
				Bufcart cart=new Bufcart();
				cart.setEmail(loggedUser.getEmail());
				cart.setProductId(Integer.parseInt(productId));
				cart.setProductname(product.getProductname());
				cart.setPrice(product.getPrice());
				cart.setQuantity(1);
				Date date=new Date();
				cart.setDateAdded(date);
				
				catRepository.save(cart);
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.CART_UPD_MESSAGE_CODE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
			catch (Exception e)
			{
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		}
		else
		{
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);		
	}
	
	
	@GetMapping("/viewCart")
	public ResponseEntity<cartResp> viewCart(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN){
		cartResp resp=new cartResp();
		if(!Validator.isStringEmpty(AUTH_TOKEN) && jwtutil.checkToken(AUTH_TOKEN) != null)
		{
			try
			{
				User loggedUser = jwtutil.checkToken(AUTH_TOKEN);
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(catRepository.findByEmail(loggedUser.getEmail()));
			}
			catch (Exception e)
			{
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		}
		else {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/updateCart")
	public ResponseEntity<cartResp> updateCart(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(name = WebConstants.BUF_ID) String bufcartid,
			@RequestParam(name = WebConstants.BUF_QUANTITY) String quantity) throws IOException {

		cartResp resp = new cartResp();
		if (!Validator.isStringEmpty(AUTH_TOKEN) && jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User loggedUser = jwtutil.checkToken(AUTH_TOKEN);
				Bufcart selCart = catRepository.findByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
				selCart.setQuantity(Integer.parseInt(quantity));
				catRepository.save(selCart);
				List<Bufcart> bufcartlist = catRepository.findByEmail(loggedUser.getEmail());
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.UPD_CART_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(bufcartlist);
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		} else {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
	}

	
	@DeleteMapping(path = { "/deleteCart/{bufcartid}" })
	public ResponseEntity<cartResp> delCart(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
											@PathVariable("bufcartid") String bufcartid) throws IOException {

		cartResp resp = new cartResp();
		if (!Validator.isStringEmpty(AUTH_TOKEN) && jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User loggedUser = jwtutil.checkToken(AUTH_TOKEN);
				catRepository.deleteByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
				List<Bufcart> bufcartlist = catRepository.findByEmail(loggedUser.getEmail());
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.DEL_CART_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(bufcartlist);
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		} else {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
	}

	
	@GetMapping("/placeOrder")
	public ResponseEntity<serverResp> placeOrder(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN){
		serverResp resp=new serverResp();
		if(!Validator.isStringEmpty(AUTH_TOKEN) && jwtutil.checkToken(AUTH_TOKEN) != null)
		{
			try
			{
				User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
				PlaceOrder po=new PlaceOrder();
				po.setEmail(loggedUser.getEmail());
				po.setOrderStatus(WebConstants.ORD_STATUS);
				Date date = new Date();
				po.setOrderDate(date);
				double total=0;
				List<Bufcart> cartList=catRepository.findAllByEmail(loggedUser.getEmail());
				for(Bufcart cart: cartList)
				{
					total=+(cart.getQuantity()*cart.getPrice());
				}
				po.setTotalCost(total);
				PlaceOrder PO=ordRepo.save(po);
				cartList.forEach(bufcart ->{
					bufcart.setOrderId(PO.getOrderId());
					catRepository.save(bufcart);
				});
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.ORD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
			catch (Exception e)
			{
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		}
		else
		{
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
	}
	
}
