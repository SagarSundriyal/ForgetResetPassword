package com.reset.password.demo.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResetPasswordController {

	@Autowired
	private PersonDAO personStub;
	@Autowired
	private AccessTokenService tokenStub;
	@Autowired
	private EmailService emailStub;

	private static final String USER = "user";
	private static final String GENERAL_ERROR = "Something  Went Wrong";
	private static final String LOGIN_ERROR = "Username or password are incorrect";

	@RequestMapping(path = "/reset-pass-demo/registration", method = RequestMethod.POST ,
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response registration(@RequestBody(required = true) Person person, @Context HttpServletRequest req, 
			@Context HttpSession session) {
		System.out.println("  line 43 ");
		if (!ResetPasswordDemoUtil.emailValidator(person.getEmail())) {
			System.out.println("  line 45 "+person.getEmail());
			return Response.status(200).entity(new Message("*Plaese enter a valid email address")).build();
		}
		else if (person.getEmail().equals(person.getPassword())) {
			System.out.println("  line 49 "+person.getPassword());
			return Response.status(200).entity(new Message("*Username and password cannot be the same")).build();
		}
		else if (personStub.findByEmail(person.getEmail())!=null) {
			System.out.println("  line 53 "+personStub.findByEmail(person.getEmail()));
			return Response.status(200).entity(new Message("*Username already exist")).build();
		}
		Person newPersonEntry = personStub.signup(person);
		System.out.println("  newPersonEntry 57 "+newPersonEntry);
		if (null != newPersonEntry) {
			session = req.getSession(true);
			session.setAttribute(USER, person);
			System.out.println("  session 61 "+session);
			return Response.status(201).entity(newPersonEntry).build();
		}
		return Response.status(400).entity(new Message(GENERAL_ERROR)).build();
	}

	@RequestMapping(path = "/reset-pass-demo/login", method = RequestMethod.POST ,produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public Response login(@RequestBody(required = true) Person person, @Context HttpServletRequest req, @Context HttpSession session) {
		System.out.println(" person line 62 "+person);
		
		Person loggedInPerson = personStub.login(person);
		System.out.println(" loggedInPerson line 65 "+loggedInPerson);
		
		if (null != loggedInPerson) {
			session = req.getSession(true);
			session.setAttribute(USER, person);
			return Response.status(200).entity(loggedInPerson).build();
		}
		return Response.status(400).entity(new Message(LOGIN_ERROR)).build();
	}

	@RequestMapping(path = "/reset-pass-demo/logout", method = RequestMethod.POST)
	public Response logout(@Context HttpSession session) {
		System.out.println(" session line 73 "+session);
		session.removeAttribute(USER);
		session.invalidate();
		if (session != null) {
			session = null;
		}
		return Response.status(200).entity(Status.OK.getReasonPhrase()).build();

	}

	@RequestMapping(path = "/reset-pass-demo/updatePassword", produces = MediaType.APPLICATION_JSON, 
			consumes = MediaType.APPLICATION_JSON, method = RequestMethod.POST)
	public Response updatePassword(@RequestBody ResetPasswordRequest resetPasswordRequest,
			@Context HttpServletRequest req, HttpSession session) {
		System.out.println(" resetPasswordRequest "+resetPasswordRequest);
		System.out.println(" req "+req);
		System.out.println(" session "+session);
		
		Person person = personStub.findByEmail(resetPasswordRequest.getEmail());
		System.out.println(" person "+person);
		
		if (StringUtils.isEmpty(resetPasswordRequest.getNewPassword())) {
			System.out.println(" line 94 "+StringUtils.isEmpty(resetPasswordRequest.getNewPassword()));
			return Response.status(200).entity(new Message("*A new Password Is Required")).build();
		}
		if (tokenStub.isEligible(person, resetPasswordRequest.getToken())) {
			System.out.println(" line 98 "+tokenStub.isEligible(person, resetPasswordRequest.getToken()));
			
			personStub.updatePassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getNewPassword());
			System.out.println(" line 101 "+personStub.updatePassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getNewPassword()));
			return Response.status(200).entity(new Message("Your password has been rest successfully")).build();
		}
		return Response.status(401).entity(new Message("*Request not authorized")).build();
	}

	@RequestMapping(value = "/reset-pass-demo/forgotMyPassword", produces = MediaType.APPLICATION_JSON, 
			consumes = MediaType.APPLICATION_JSON, method = RequestMethod.POST)
	public Response sendConformationMailTo(@RequestBody String sendConformationMailTo, @Context HttpServletResponse res,
			@Context HttpServletRequest req) {
		System.out.println(" sendConformationMailTo.. "+sendConformationMailTo);
		System.out.println(" res "+res);
		System.out.println(" req "+req);
		
	if (!ResetPasswordDemoUtil.emailValidator(sendConformationMailTo)) {
			System.out.println(" line 105 "+!ResetPasswordDemoUtil.emailValidator(sendConformationMailTo));
			
			return Response.status(200).entity(new Message("*Plaese enter a valid email address")).build();
		}
		if (emailStub.sendResetPasswordMail(sendConformationMailTo, req)) {
			System.out.println(" line 110 "+emailStub.sendResetPasswordMail(sendConformationMailTo, req));
			return Response.status(200).entity(new Message("A confirmation link has been sent to your email address")).build();
		}
		System.out.println(" line 113 ");
		return Response.status(200).entity(new Message("*This email is not registered in our website")).build();
	}

}
