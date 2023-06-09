package ssf_assessment.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import ssf_assessment.respositories.RedisRepository;

@Service
public class AuthenticationService {
	@Autowired
	RedisRepository redisRepo;

	// TODO: Task 2
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write the authentication method in here
	public void authenticate(String username, String password) throws Exception {
		JsonObject userJson = Json.createObjectBuilder()
			.add("username", username)
			.add("password", password)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<>(userJson.toString(), headers);

		String url = UriComponentsBuilder
		.fromUriString("https://authservice-production-e8b2.up.railway.app")
		.path("/api/authenticate")
		.toUriString();
		
		RestTemplate template= new RestTemplate();
		ResponseEntity<String> response = template.exchange(url, 
				HttpMethod.POST, entity, String.class);
		
		System.out.println(response.getBody());

		HttpStatusCode statusCode = response.getStatusCode();
		if (statusCode.is4xxClientError())
			throw new HttpClientErrorException(statusCode);
	}

	public List<String> generateEqn() {
		List<String> list = new ArrayList<>();

		Random random = new Random();
		int a = random.nextInt(1,51);
		int b = random.nextInt(1,51);

		String[] arr = new String[4];
		arr[0] = "+";
		arr[1] = "-";
		arr[2] = "/";
		arr[3] = "*";
		int opt = random.nextInt(4);

		String eqn = "What is " + a + " " + arr[opt] + " " + b + "?";
		list.add(eqn);

		int ans = 0;
		switch(opt) {
			case 0:
				ans = a + b;
				break;
			case 1:
				ans = a - b;
				break;
			case 2:
				ans = a / b;
				break;
			case 3:
				ans = a * b;
				break;
		}

		list.add(String.valueOf(ans));

		return list;
	}

	// TODO: Task 3
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to disable a user account for 30 mins
	public void disableUser(String username) {
		redisRepo.timeout(username);
	}

	public boolean isTimeout(String username) {
		return redisRepo.isTimeout(username);
	}

	// TODO: Task 5
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to check if a given user's login has been disabled
	public boolean isLocked(String username) {
		return false;
	}
}
