package com.caobolun.bootstrap.user.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.util.StrUtil;
import com.caobolun.bootstrap.user.entity.UserDO;
import com.caobolun.bootstrap.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限认证接口实现类
 * 用于实现 Sa-Token 框架的权限和角色验证逻辑
 */
@Component
@RequiredArgsConstructor
public class SaTokenStpInterfaceImpl implements StpInterface {

    private final UserMapper userMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }

        String loginIdStr = loginId.toString();
        if(!StrUtil.isNumeric(loginIdStr)){
            return Collections.emptyList();
        }

        UserDO userDO = userMapper.selectById(loginIdStr);
        if(userDO == null || StrUtil.isBlank(userDO.getRole())){
            return Collections.emptyList();
        }

        return List.of(userDO.getRole());
    }
}