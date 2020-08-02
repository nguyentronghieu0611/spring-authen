package com.javainuse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.model.LoginResponse;
import com.javainuse.model.Response;

@RestController
public class HelloWorldController {

	@RequestMapping(value = "/hello", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> firstPage() {
		return ResponseEntity.ok(new Response("Welcome", 0));
	}

}