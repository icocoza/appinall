package com.ccz.appinall.application.server.http.admin.web.controller;

import java.net.InetSocketAddress;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ccz.appinall.application.server.http.admin.business.service.ResourceLoaderService;
import com.ccz.appinall.library.type.ResponseData;
import com.ccz.appinall.library.util.KeyGen;
import com.ccz.appinall.library.util.StrUtil;
import com.ccz.appinall.services.action.admin.AdminCommandAction;
import com.ccz.appinall.services.action.admin.entity.AddApp;
import com.ccz.appinall.services.action.admin.entity.AdminCommon;
import com.ccz.appinall.services.action.admin.entity.AdminRegister;
import com.ccz.appinall.services.action.admin.entity.ModifyApp;
import com.ccz.appinall.services.action.db.DbAppManager;
import com.ccz.appinall.services.entity.db.RecAdminApp;
import com.ccz.appinall.services.type.enums.EAdminAppStatus;
import com.ccz.appinall.services.type.enums.EAdminError;
import com.ccz.appinall.services.type.enums.EAdminStatus;
import com.ccz.appinall.services.type.enums.EUserRole;

@Controller
public class AdminController {

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	ResourceLoaderService resourceLoaderService;
	
	AdminCommandAction adminCommandAction = new AdminCommandAction(null);
	
    /** Home page. */
    @RequestMapping("/index")
    public String index(Model model) {
    		model.addAttribute("logo", resourceLoaderService.loadText("/static/logo.txt"));
    		//Cookie cookie = new Cookie("email", email);
    		//response.addCookie(cookie);
        return "login";
    }
    
    @RequestMapping("/login")
    public String login(Model model) {
    		model.addAttribute("logo", resourceLoaderService.loadText("/static/logo.txt"));
    		//Cookie cookie = new Cookie("email", email);
    		//response.addCookie(cookie);
        return "login";
    }

    /** Login form with error. */
    @RequestMapping("/loginerror")
    public String loginError(Model model) {
    		model.addAttribute("logo", resourceLoaderService.loadText("/static/logo.txt"));
        model.addAttribute("error_message", "Wrong email or password");
        return "login";
    }
    
    @RequestMapping("/home")
    public String home(Model model, HttpServletRequest request, HttpServletResponse response) {
    		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    		String currentPrincipalName = authentication.getName();

    		String email = currentPrincipalName;
    		String token = KeyGen.makeKeyWithSeq("tk");
    		DbAppManager.getInst().upsertAdminToken(email, token, request.getRemoteAddr());

    		response.addCookie(new Cookie("email", email));
    		response.addCookie(new Cookie("token", token));
    		List<RecAdminApp> apps = DbAppManager.getInst().getAppList(email, EAdminAppStatus.all, 0, 10);
    		if(apps.size()>0)
    			model.addAttribute("apps", apps);
    		else
    			model.addAttribute("apps_empty", true);
        return "list";
    }

    /** Home page. */
    @RequestMapping("/view")
    public String view(Model model, HttpServletRequest request) {
    		Cookie[] cookies = request.getCookies();
    		model.addAttribute("is_newapp", true);
        return "view";
    }
    
    private void updateCookieToEntity(AdminCommon rec, Cookie[] cookies) {
    		for(Cookie cookie: cookies) {
    			if(cookie.getName().equals("email"))
    				rec.setEmail(cookie.getValue());
    			else if(cookie.getName().equals("token"))
    				rec.setToken(cookie.getValue());
    		}
    }
    
	@RequestMapping(value="/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String register(AdminRegister record, Model model, HttpServletRequest request) {
		model.addAttribute("logo", resourceLoaderService.loadText("/static/logo.txt"));
		
		if(record.passwd.length()<8) {
			model.addAttribute("error_message", EAdminError.short_password_length_than_8);
            return "login";	
		}
		record.setPasswd(passwordEncoder.encode(record.passwd));
		//default value for the admin user
		record.adminstatus = EAdminStatus.normal;
		record.userrole = EUserRole.adminuser;
		
		ResponseData<EAdminError> res = adminCommandAction.processWebData(record);
		if(res.getError() != EAdminError.ok) {
            model.addAttribute("error_message", res.getError().toString());
            return "login";
		}
		return "login";
	}
    
    static int seqIndex =0;
	@RequestMapping(value="/savenew", method = RequestMethod.POST)
	public String savenew(AddApp record, Model model, HttpServletRequest request) {
		updateCookieToEntity(record, request.getCookies());
		
		ResponseData<EAdminError> res = adminCommandAction.processWebData(record);
		if( res.getError() != EAdminError.ok) {
			model.addAttribute("error_message", res.getError().toString());
			model.addAttribute("app", record);
			return "view";
		}
		return "redirect:home";
	}
	
	@RequestMapping(value="/updateapp", method = RequestMethod.POST)
	public String updateapp(ModifyApp record, Model model, HttpServletRequest request) {
		updateCookieToEntity(record, request.getCookies());
		ResponseData<EAdminError> res = adminCommandAction.processWebData(record);
		if( res.getError() != EAdminError.ok) {
			model.addAttribute("error_message", res.getError().toString());
			model.addAttribute("app", record);
			model.addAttribute("disabled", true);
			model.addAttribute("is_newapp", false);
			model.addAttribute("updatenow", record.isUpdateNow());
			model.addAttribute("updatelater", !record.isUpdateNow());
			return "view";
		}
		return "redirect:home";
	}
	
	@RequestMapping(value="/details", method = RequestMethod.GET)
	public String details(@RequestParam("appid") String appid, Model model, HttpServletRequest request) {
		RecAdminApp record = DbAppManager.getInst().getApp(appid);
		if(record==null) {
			model.addAttribute("error_message", "failed to load the record information");
			return "view";
		}
		model.addAttribute("app", record);
		model.addAttribute("disabled", true);
		model.addAttribute("is_newapp", false);
		model.addAttribute("updatenow", record.updateforce);
		model.addAttribute("updatelater", !record.updateforce);
		return "view";
	}

}
