package com.meategg.service;

import com.meategg.entity.Result;
import org.springframework.stereotype.Service;

@Service
public interface userService{
    public Result login(String username, String password);
}
