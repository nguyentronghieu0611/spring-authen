package com.bnv.util;

import com.bnv.config.JwtTokenUtil;
import com.bnv.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

@Component
public class ApplicationUtil {
    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;


    public String extractPostRequestBody(HttpServletRequest request){
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }

    public boolean validateTokenOrg(String token, Map<String, String> body){
        if(!jwtUserDetailsService.loadOrgCodeByUsername(jwtTokenUtil.getUsernameFromToken(token.substring(7))).equals(body.get("org_Code")))
            return false;
        else
            return true;
    }
}
