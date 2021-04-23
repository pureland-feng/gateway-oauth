package com.leyu.authservice.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.leyu.authservice.constant.MessageConstant;
import com.leyu.authservice.domain.SecurityUser;
import com.leyu.video.persistence.entity.InfoToUser;
import com.leyu.video.persistence.entity.Role;
import com.leyu.video.persistence.entity.User;
import com.leyu.video.persistence.entity.UserToRole;
import com.leyu.video.persistence.mapper.InfoToUserMapper;
import com.leyu.video.persistence.mapper.RoleMapper;
import com.leyu.video.persistence.mapper.UserMapper;
import com.leyu.video.persistence.mapper.UserToRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户管理业务类
 * Created by chuyan on 2020/6/19.
 */
@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private InfoToUserMapper infoToUserMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserToRoleMapper userToRoleMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        EntityWrapper userEw = new EntityWrapper();
        userEw.eq("user_name",username);
        List<User> userList = userMapper.selectList(userEw);
        if (userList == null || userList.isEmpty()) {
            throw new UsernameNotFoundException(MessageConstant.USERNAME_PASSWORD_ERROR);
        }
        User user = userList.get(0);
        EntityWrapper userToRoleEw = new EntityWrapper();
        userToRoleEw.eq("user_no",user.getUserNo());
        List<UserToRole> userToRoleList = userToRoleMapper.selectList(userToRoleEw);
        if(userToRoleList != null && userToRoleList.size()>0){
            Role role = roleMapper.selectById(userToRoleList.get(0).getRoleCode());
            user.setRoleName(role.getRoleName());
        }
        SecurityUser securityUser = new SecurityUser(user);
        if (!securityUser.isEnabled()) {
            throw new DisabledException(MessageConstant.ACCOUNT_DISABLED);
        } else if (!securityUser.isAccountNonLocked()) {
            throw new LockedException(MessageConstant.ACCOUNT_LOCKED);
        } else if (!securityUser.isAccountNonExpired()) {
            throw new AccountExpiredException(MessageConstant.ACCOUNT_EXPIRED);
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(MessageConstant.CREDENTIALS_EXPIRED);
        }
        return securityUser;
    }

}
