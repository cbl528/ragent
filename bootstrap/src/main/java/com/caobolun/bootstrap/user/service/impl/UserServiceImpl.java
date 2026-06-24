package com.caobolun.bootstrap.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caobolun.bootstrap.user.dto.resquest.ChangePasswordRequest;
import com.caobolun.bootstrap.user.dto.resquest.UserCreateRequest;
import com.caobolun.bootstrap.user.dto.resquest.UserPageRequest;
import com.caobolun.bootstrap.user.dto.resquest.UserUpdateRequest;
import com.caobolun.bootstrap.user.dto.vo.UserVO;
import com.caobolun.bootstrap.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public IPage<UserVO> pageQuery(UserPageRequest requestParam) {
        return null;
    }

    @Override
    public String create(UserCreateRequest requestParam) {
        return "";
    }

    @Override
    public void update(String id, UserUpdateRequest requestParam) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void changePassword(ChangePasswordRequest requestParam) {

    }
}
