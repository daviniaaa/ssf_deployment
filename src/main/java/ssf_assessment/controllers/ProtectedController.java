package ssf_assessment.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import ssf_assessment.model.User;

@Controller
@RequestMapping("/protected")
public class ProtectedController {

	// TODO Task 5
	// Write a controller to protect resources rooted under /protected
	@GetMapping("/login")
	public String directAccess(Model model, HttpSession session) {
		System.out.println(session.getAttribute("auth"));
		if(((String) session.getAttribute("auth")).equals("true"))
			return "view1";
		

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

		return "view0";
	}
}
