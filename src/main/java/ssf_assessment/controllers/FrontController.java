package ssf_assessment.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ssf_assessment.model.CaptchaResponse;
import ssf_assessment.model.User;
import ssf_assessment.services.AuthenticationService;

@Controller
// @RequestMapping
public class FrontController {
	@Autowired
	AuthenticationService service;


	// TODO: Task 2, Task 3, Task 4, Task 6

	@GetMapping("/")
	public String landingPage(Model model, HttpSession session) {
		User user = (User) model.getAttribute("user");
		if (user == null)
			user = new User();
		model.addAttribute("user", user);

		String auth = (String) session.getAttribute("auth");
		if (auth == null) {
			session.setAttribute("auth", "false");
		} else {
			session.setAttribute("auth", auth);
		}

		Integer captcha = (Integer) session.getAttribute("captcha");
		if (captcha == null) {
			session.setAttribute("captcha", 0);
			captcha = 0;
		} else {
			session.setAttribute("captcha", captcha);
		}
		System.out.println("initial captcha: " + captcha);
		model.addAttribute("captcha", captcha);

		if (captcha > 0) {
			List<String> eqnList = service.generateEqn();
			model.addAttribute("equation", ((String) eqnList.get(0)));
			model.addAttribute("correct", ((String) eqnList.get(1)));
			session.setAttribute("correct", ((String) eqnList.get(1)));
			model.addAttribute("captcharesponse", new CaptchaResponse());
		}

		return "view0";
	}

	@PostMapping(path="/login",
		// consumes="application/x-www-form-url-encoded", produces="text/html")
		consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE) //, produces=MediaType.TEXT_HTML)
	public String login(@Valid User user, BindingResult binding, Model model, 
		HttpSession session, CaptchaResponse captchaResponse) {
		System.out.println("bindError: " + binding.getErrorCount());
		System.out.println("username: " + user.getUsername());
		System.out.println("Captcha response: " + captchaResponse.getAnswer());
		System.out.println("attempts: " + session.getAttribute(user.getUsername()));

		Integer captcha = (Integer) session.getAttribute("captcha");

		if(binding.hasErrors()) {

			model.addAttribute("captcha", captcha);
			if (captcha > 0) {
				List<String> eqnList = service.generateEqn();
				model.addAttribute("equation", ((String) eqnList.get(0)));
				model.addAttribute("correct", ((String) eqnList.get(1)));
				session.setAttribute("correct", ((String) eqnList.get(1)));
				model.addAttribute("captcharesponse", new CaptchaResponse());
			}

			return "view0";
		}

		if (service.isTimeout(user.getUsername())) {
			return "view2";
		}

		if (captchaResponse.getAnswer() != null) {
			if (!captchaResponse.getAnswer().equals(session.getAttribute("correct"))) {
				Integer attempt = (Integer) session.getAttribute(user.getUsername());
				if(attempt == null) {
					session.setAttribute(user.getUsername(), 1);
				} else {
					attempt += 1;
					session.setAttribute(user.getUsername(), attempt);
					if (attempt > 2) {
						service.disableUser(user.getUsername());
						return "view2";
					}
				}
				System.out.println("session: " + session.getAttribute(user.getUsername()));

				
				captcha += 1;
				System.out.println("captcha:" + captcha);
				session.setAttribute("captcha", captcha);

				model.addAttribute("captcha", captcha);
				if (captcha > 0) {
					List<String> eqnList = service.generateEqn();
					model.addAttribute("equation", ((String) eqnList.get(0)));
					model.addAttribute("correct", ((String) eqnList.get(1)));
					session.setAttribute("correct", ((String) eqnList.get(1)));
					model.addAttribute("captcharesponse", new CaptchaResponse());
				}
				return "view0";
			}
		}
		
		try {
			System.out.println("correct: " + session.getAttribute("correct"));
			service.authenticate(user.getUsername(), user.getPassword());

			session.removeAttribute(user.getUsername());
			session.removeAttribute("captcha");
		} catch (Exception e) {
			Integer attempt = (Integer) session.getAttribute(user.getUsername());
			if(attempt == null) {
				session.setAttribute(user.getUsername(), 1);
			} else {
				attempt += 1;
				session.setAttribute(user.getUsername(), attempt);
				if (attempt > 2) {
					service.disableUser(user.getUsername());
					return "view2";
				}
			}
			System.out.println("session: " + session.getAttribute(user.getUsername()));

			
			captcha += 1;
			System.out.println("captcha:" + captcha);
			session.setAttribute("captcha", captcha);

			model.addAttribute("captcha", captcha);
			if (captcha > 0) {
				List<String> eqnList = service.generateEqn();
				model.addAttribute("equation", ((String) eqnList.get(0)));
				model.addAttribute("correct", ((String) eqnList.get(1)));
				session.setAttribute("correct", ((String) eqnList.get(1)));
				model.addAttribute("captcharesponse", new CaptchaResponse());
			}
			return "view0";
		} finally {
			session.setAttribute("auth", "true");
		}
		
		return "view1";
		
	}

	@PostMapping("/")
	public String logout(Model model, HttpSession session) {
		session.setAttribute("auth", "false");

		User user = (User) model.getAttribute("user");
		if (user == null)
			user = new User();
		model.addAttribute("user", user);

		String auth = (String) session.getAttribute("auth");
		if (auth == null) {
			session.setAttribute("auth", "false");
		} else {
			session.setAttribute("auth", auth);
		}
		
		Integer captcha = (Integer) session.getAttribute("captcha");
		if (captcha == null) {
			session.setAttribute("captcha", 0);
			captcha = 0;
		} else {
			session.setAttribute("captcha", captcha);
		}
		System.out.println("initial captcha: " + captcha);
		model.addAttribute("captcha", captcha);
		// session.invalidate();
		return "view0";
	}

}
