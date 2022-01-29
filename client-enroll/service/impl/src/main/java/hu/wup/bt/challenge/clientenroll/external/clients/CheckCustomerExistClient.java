package hu.wup.bt.challenge.clientenroll.external.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class CheckCustomerExistClient {
	
	@Value("${validation.client.existence.url}")
	private String clientExistenceUrl;

	@Autowired
	private RestTemplate restTemplate;
	
	public Boolean checkCustomerExistence(MultiValueMap<String, String> map) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		
		return restTemplate.postForObject(clientExistenceUrl, request, Boolean.class);
	}
}
