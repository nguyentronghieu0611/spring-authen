package com.bnv.service;

import com.bnv.model.DAOUser;
import com.bnv.repository.HelloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelloService {
    @Autowired
    HelloRepository helloRepository;

    public List<Object> getData(){
        return helloRepository.getAll();
    }

    public String getData1(){
        return helloRepository.findCarsAfterYear("HIEU");
    }
}
