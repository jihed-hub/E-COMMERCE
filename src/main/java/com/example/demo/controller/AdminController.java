package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Constants.ResponseCode;
import com.example.demo.Constants.WebConstants;
import com.example.demo.Model.PlaceOrder;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Response.order;
import com.example.demo.Response.prodResp;
import com.example.demo.Response.serverResp;
import com.example.demo.Response.viewOrdResp;
import com.example.demo.util.Validator;
import com.example.demo.util.jwtUtil;

@CrossOrigin(origins = WebConstants.ALLOWED_URL)
@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private jwtUtil util;

	
	@PostMapping("/verify")
	public ResponseEntity<serverResp> verifyUser(@Valid @RequestBody HashMap<String,String> credential){
		
		String email="";
		String password="";
		
		if(credential.containsKey(WebConstants.USER_EMAIL))
		{
			email=credential.get(WebConstants.USER_EMAIL);
		}
		if(credential.containsKey(WebConstants.USER_PASSWORD))
		{
			password=credential.get(WebConstants.USER_PASSWORD);
		}
		
		User user = userRepo.findByEmailAndPasswordAndUsertype(email, password, WebConstants.USER_ADMIN_ROLE);
		
		serverResp resp=new serverResp();
		
		if(user!=null)
		{
			String jwtToken=util.createToken(email, password,  WebConstants.USER_ADMIN_ROLE);
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.SUCCESS_MESSAGE);
			resp.setAUTH_TOKEN(jwtToken);
		}
		else
		{
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		
		return new ResponseEntity<serverResp>(resp, HttpStatus.OK);		
	}
	
	
	@PostMapping("/addProduct")
	public ResponseEntity<prodResp> addProduct(@RequestHeader(name=WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
											   @RequestParam(name=WebConstants.PROD_FILE) MultipartFile prodImage,
											   @RequestParam(name=WebConstants.PROD_NAME) String productname,
											   @RequestParam(name=WebConstants.PROD_PRICE) String price,
											   @RequestParam(name=WebConstants.PROD_DESC) String description,
											   @RequestParam(name=WebConstants.PROD_QUANITY) String quantity){
		
		prodResp resp=new prodResp();
		if(Validator.isStringEmpty(productname) || Validator.isStringEmpty(description) || Validator.isStringEmpty(price)
				|| Validator.isStringEmpty(quantity))
		{
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		}
		else if (!Validator.isStringEmpty(AUTH_TOKEN) && util.checkToken(AUTH_TOKEN) != null)
		{	
			try{
				Product product=new Product();
				product.setProductname(productname);
				product.setDescription(description);
				product.setPrice(Double.parseDouble(price));
				product.setQuantity(Integer.parseInt(quantity));
				product.setProductimage(prodImage.getBytes());
				productRepo.save(product);
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.ADD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(productRepo.findAll());
			}
			catch (Exception e){
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		}
		else
		{
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		}
		
		 return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);	
	}
	
	
	@PostMapping("/getProducts")
	public ResponseEntity<prodResp> getProducts(@RequestHeader(name=WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN){
		
		prodResp resp=new prodResp();
		if (!Validator.isStringEmpty(AUTH_TOKEN) && util.checkToken(AUTH_TOKEN) != null)
		{	
			try{
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.ADD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(productRepo.findAll());
			}
			catch (Exception e){
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
		
		
		return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);	
	}
	
	
	
	@DeleteMapping(path = { "/deleteProduct/{productid}" })
	public ResponseEntity<prodResp> deleteProduct(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@PathVariable("productid") String productid) throws IOException {
		prodResp resp = new prodResp();
		if (Validator.isStringEmpty(productid)) {
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		} else if (!Validator.isStringEmpty(AUTH_TOKEN) && util.checkToken(AUTH_TOKEN) != null) {
			try {
				productRepo.deleteByProductid(Integer.parseInt(productid));
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.DEL_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(productRepo.findAll());
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		} else {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
	}
	
	
	@PutMapping(path = { "/updateProduct/{productid}" })
	public ResponseEntity<serverResp> updateProducts(
			@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@PathVariable("productid") String productid,
			@RequestParam(name = WebConstants.PROD_FILE, required = false) MultipartFile prodImage,
			@RequestParam(name = WebConstants.PROD_DESC) String description,
			@RequestParam(name = WebConstants.PROD_PRICE) String price,
			@RequestParam(name = WebConstants.PROD_NAME) String productname,
			@RequestParam(name = WebConstants.PROD_QUANITY) String quantity) throws IOException {
		serverResp resp = new serverResp();
		if (Validator.isStringEmpty(productname) || Validator.isStringEmpty(description)
				|| Validator.isStringEmpty(price) || Validator.isStringEmpty(quantity)) {
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		} else if (!Validator.isStringEmpty(AUTH_TOKEN) && util.checkToken(AUTH_TOKEN) != null) {
			try {
				Product prodOrg;
				Product prod;
				if (prodImage != null) {
					prod = new Product(Integer.parseInt(productid), description, productname, Double.parseDouble(price),
							Integer.parseInt(quantity), prodImage.getBytes());
				} else {
					prodOrg = productRepo.findByProductid(Integer.parseInt(productid));
					prod = new Product(Integer.parseInt(productid), description, productname, Double.parseDouble(price),
							Integer.parseInt(quantity), prodOrg.getProductimage());
				}
				productRepo.save(prod);
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.UPD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
		} else {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(ResponseCode.FAILURE_MESSAGE);
		}
		return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
	}

	
	@GetMapping("/viewOrders")
	public ResponseEntity<viewOrdResp> viewOrders(@RequestHeader(name=WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN){
		viewOrdResp resp=new viewOrdResp();
		if(!Validator.isStringEmpty(AUTH_TOKEN) && util.checkToken(AUTH_TOKEN) != null)
		{
			try
			{
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.SUCCESS_MESSAGE);
			resp.setAUTH_TOKEN(AUTH_TOKEN);
			List<order> orders=new ArrayList<>();
			List<PlaceOrder> placeOrders= orderRepository.findAll();
			placeOrders.forEach((po) ->{
				order ord=new order();
				ord.setOrderId(po.getOrderId());
				ord.setOrderBy(po.getEmail());
				ord.setOrderStatus(po.getOrderStatus());
				ord.setProducts(cartRepository.findAllByOrderId(po.getOrderId()));
				orders.add(ord);
			});
			resp.setOrderlist(orders);
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
		return new ResponseEntity<viewOrdResp>(resp, HttpStatus.ACCEPTED);		
	}
	
	
	@PostMapping("/updateOrder")
	public ResponseEntity<serverResp> updateOrder(@RequestHeader(name=WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
												  @RequestParam(name=WebConstants.ORD_ID) String orderid,
												  @RequestParam(name=WebConstants.ORD_STATUS) String orderstatus){
		serverResp resp=new serverResp();
		if(Validator.isStringEmpty(orderid) || Validator.isStringEmpty(orderstatus))
		{
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		}
		else if (!Validator.isStringEmpty(AUTH_TOKEN) && util.checkToken(AUTH_TOKEN) != null)
		{
			try
			{
				PlaceOrder pc=orderRepository.findByOrderId(Integer.parseInt(orderid));
				pc.setOrderStatus(orderstatus);
				orderRepository.save(pc);
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.UPD_ORD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
			}
			catch (Exception e)
			{
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.toString());
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


