package com.meategg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meategg.entity.Result;
import com.meategg.entity.User;
import com.meategg.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public interface userService extends IService<User> {
    public Result login(String username, String password);

    Result register(String username, String password);
}
//1