package com.caobolun.bootstrap.user.service;

import com.caobolun.bootstrap.user.dto.resquest.LoginRequest;
import com.caobolun.bootstrap.user.dto.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginRequest requestParam);

    void logout();
}