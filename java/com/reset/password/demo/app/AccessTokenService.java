package com.reset.password.demo.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


@Component
public class AccessTokenService {
	private static final Logger log = Logger.getLogger(AccessTokenService.class.getSimpleName());

	// Access-Token registry
	Map<String, String> tokenRegister;

	public AccessTokenService() {
		tokenRegister = new ConcurrentHashMap<String, String>();
	}

	public boolean isEligible(Person person, String token) {
		try {
			String tokenInMap = tokenRegister.get(person.getEmail());
			return token.equals(tokenInMap);
		} catch (Exception e) {
			log.error("Fiald to resolve Access-Token from reporitory, Data that was passed to Cotroller: Username: "
					+ person.getEmail() + ", Access-Token: " + token);
		}
		return false;
	}

	
	public void addAccessToken(String sendConformationMailTo, String token) {
		try {
			tokenRegister.put(sendConformationMailTo, token);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
