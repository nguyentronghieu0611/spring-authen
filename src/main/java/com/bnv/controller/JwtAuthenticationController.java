package com.bnv.controller;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.bnv.service.JwtUserDetailsService;

import com.bnv.config.JwtTokenUtil;
import com.bnv.model.JwtRequest;
import com.bnv.model.JwtResponse;
import com.bnv.model.LoginResponse;
import com.bnv.model.Response;
import com.bnv.model.UserDTO;

@RestController
@CrossOrigin
@RequestMapping("/ApiAuth/oauth")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

            final String token = jwtTokenUtil.generateToken(userDetails);
            final Date expired_time = jwtTokenUtil.getExpirationDateFromToken(token);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
            String strDate = formatter.format(expired_time);

            System.out.println("---------------------current date: " + new Date(System.currentTimeMillis()));
            System.out.println("---------------------expired date: " + expired_time);

            return ResponseEntity.ok(new LoginResponse(token, 0, strDate));
        } catch (Exception e) {
            return (ResponseEntity<?>) ResponseEntity.ok(new Response(e.getMessage(), 1));
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) {
        try {
            userDetailsService.save(user);
            return ResponseEntity.ok(new Response("Đăng ký thành công!", 0));
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("constraint"))
                return (ResponseEntity<?>) ResponseEntity.ok(new Response("Tên tài khoản đã được sử dụng!", 1));
            else
                return (ResponseEntity<?>) ResponseEntity.ok(new Response(e.getMessage(), 1));
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("Tài khoản vô hiệu hóa!", e);
        } catch (BadCredentialsException e) {
            throw new Exception("Xác thực không hợp lệ!", e);
        }
    }
}