package com.bnv.controller;

import com.bnv.model.DAOUser;
import com.bnv.model.DataResponse;
import com.bnv.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bnv.model.Response;

import java.util.List;

@Component
@RestController
public class HelloWorldController {
	@Autowired
	HelloService helloService;

	@RequestMapping(value = "/hello", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> firstPage() {
		try{
			return new ResponseEntity<Object>(new DataResponse(helloService.getData(),0), HttpStatus.OK);
		}
		catch (Exception e){
			return new ResponseEntity<Object>(new Response(e.getMessage(),1), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/data", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> secondPage() {
		try{
			return new ResponseEntity<Object>(new DataResponse(helloService.getData1(),0), HttpStatus.OK);
		}
		catch (Exception e){
			return new ResponseEntity<Object>(new Response(e.getMessage(),1), HttpStatus.OK);
		}
	}

}