package outpolic.user.payment.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import outpolic.user.payment.dto.UserPaymentDTO;

@Controller
@Slf4j
public class UserPaymentController {
	
	@GetMapping("/paymentSuccess")
	public String paymentSuccessPage(String orderId, String paymentKey, String amount) {
		log.info("orderId: {}", orderId);
		log.info("paymentKey: {}", paymentKey);
		log.info("amount: {}", amount);
		
	    HttpRequest request = HttpRequest.newBuilder()
	    	    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
	    	    .header("Authorization", "Basic dGVzdF9za19qRXhQZUpXWVZRUkE5UWFnSmRwb3I0OVI1Z3ZOOg==")
	    	    .header("Content-Type", "application/json")
	    	    .method("POST", HttpRequest.BodyPublishers.ofString("{\"paymentKey\":\""+ paymentKey +"\",\"orderId\":\""+ orderId +"\",\"amount\":"+ amount +"}"))
	    	    .build();
	    	HttpResponse<String> response;
			try {
				response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
				var objectMapper = new ObjectMapper();
				UserPaymentDTO userPayment = objectMapper.readValue(response.body(), UserPaymentDTO.class);
				System.out.println(userPayment);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    /*
		RestTemplate restTemplate = new RestTemplate();
	    // 랜덤으로 세계 맥주에 대한 정보를 주는 url
		String url = "https://api.tosspayments.com/v1/payments/"+paymentKey;
	    
	    URI uri = UriComponentsBuilder
		              .fromUriString("https://api.tosspayments.com")
		              .path("/v1/payments/confirm")
		              .encode()
		              .build()
		              .expand(100)
		              .expand("steve")
		              .toUri();
	    
	    MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
	    body.add("orderId", orderId);	
	    body.add("paymentKey", paymentKey);	
	    body.add("amount", amount);	
	    
	
	  
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Basic dGVzdF9za19qRXhQZUpXWVZRUkE5UWFnSmRwb3I0OVI1Z3ZOOg==");
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	  
	    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
	    
	    //HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UserPaymentDTO> response = restTemplate.exchange(
            	uri,
                HttpMethod.POST,
                entity,
                UserPaymentDTO.class
            );
           
            if (response.getStatusCode().is2xxSuccessful()) {
            	log.info("Request successful!");
            	log.info("Response body : {}", response.getBody());
            	
                
            } else {
            	log.info("Request failed with status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
        	log.error("An error occurred: {}", e.getMessage());
        } 
	   */
	    
	    return "user/goods/paymentSuccess";
	}
	
	@GetMapping("/paymentFail")
	public String paymentFailPage() {
		return "user/goods/paymentFail";
	}
}
