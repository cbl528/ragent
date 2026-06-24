package com.caobolun.bootstrap.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caobolun.bootstrap.user.dto.resquest.ChangePasswordRequest;
import com.caobolun.bootstrap.user.dto.resquest.UserCreateRequest;
import com.caobolun.bootstrap.user.dto.resquest.UserPageRequest;
import com.caobolun.bootstrap.user.dto.resquest.UserUpdateRequest;
import com.caobolun.bootstrap.user.dto.vo.UserVO;

public interface UserService {

    /**
     * 分页查询用户列表
     */
    IPage<UserVO> pageQuery(UserPageRequest requestParam);

    /**
     * 创建用户
     */
    String create(UserCreateRequest requestParam);

    /**
     * 更新用户
     */
    void update(String id, UserUpdateRequest requestParam);

    /**
     * 删除用户
     */
    void delete(String id);

    /**
     * 修改当前用户密码
     */
    void changePassword(ChangePasswordRequest requestParam);
}
