package com.lureto.rjf;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/login")
public class LoginController {

	private static Logger logger = Logger.getLogger( LoginController.class );
	
	@Autowired
	private PMF pmf;
	
	@SuppressWarnings("unchecked")
	@ModelAttribute("user")
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public User login( @RequestBody User user, HttpServletRequest request ) throws IOException {
		logger.debug(user);
		
		// Hardcoded for demo website
		if( user.getName().equalsIgnoreCase("user1") && user.getPassword().equals("password1") ) {
			user.setId(1001);
			user.setEmail("fake@email.com");
			request.getSession(true).setAttribute( Constants.SESSION_USER , user);
			return user;
		}
		
        PersistenceManager pm = pmf.getManager();

		Query query = pm.newQuery("select from com.lureto.rjf.User " +
				"where password == passwordParam && name == nameParam " +
				"parameters String passwordParam, String nameParam");
		
		List<User> users;
		try {
			users = (List<User>) query.execute( user.getPassword(), user.getName() );
			if( users.size() > 0 ) {
				User luser = users.get(0);
				request.getSession(true).setAttribute( Constants.SESSION_USER , luser);
				return luser;
			}
		} finally {
			query.closeAll();
		}

        return null;
	}
	
	public PMF getPmf() {
		return pmf;
	}

	public void setPmf(PMF pmf) {
		this.pmf = pmf;
	}
	
}
