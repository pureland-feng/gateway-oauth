package com.leyu.authservice.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.leyu.authservice.constant.RedisConstant;
import com.leyu.video.persistence.entity.Menu;
import com.leyu.video.persistence.entity.Role;
import com.leyu.video.persistence.entity.RoleToMenu;
import com.leyu.video.persistence.mapper.MenuMapper;
import com.leyu.video.persistence.mapper.RoleMapper;
import com.leyu.video.persistence.mapper.RoleToMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源与角色匹配关系管理业务类
 * Created by chuyan on 2020/6/19.
 */
@Service
@Slf4j
public class ResourceServiceImpl {

    private Map<String, List<String>> resourceRolesMap;

    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleToMenuMapper roleToMenuMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 初始化角色权限信息
     */
    @PostConstruct
    public void initResourceRoleMap() {
        resourceRolesMap = new ConcurrentHashMap<>();
        List<Menu> menuList = menuMapper.selectList(new EntityWrapper<>());
        List<Role> roleList = roleMapper.selectList(new EntityWrapper<>());
        List<RoleToMenu> roleToMenuList = roleToMenuMapper.selectList(new EntityWrapper<>());
        for(RoleToMenu roleToMenu:roleToMenuList){
            String url = findUrlByMenuCode(roleToMenu.getMenuCode(),menuList);
            if(StringUtils.isEmpty(url))continue;
            String roleName = findRoleNameByRoleCode(roleToMenu.getRoleCode(),roleList);
            if(resourceRolesMap.containsKey(url)){
                Set<String> roleNameTempSet = new HashSet<>();
                roleNameTempSet.addAll(resourceRolesMap.get(url));
                roleNameTempSet.add(roleName);
                resourceRolesMap.put(url,new ArrayList<>(roleNameTempSet));
            }else {
                resourceRolesMap.put(url, CollUtil.toList(roleName));
            }
        }
        log.info("resourceRolesMap======>{}",resourceRolesMap);
        redisTemplate.boundHashOps(RedisConstant.RESOURCE_ROLES_MAP).putAll(resourceRolesMap);
    }

    private String findUrlByMenuCode(String menuCode,List<Menu> menuList){
        for(Menu menu:menuList){
            if(menuCode.equals(menu.getMenuCode())){
                return menu.getUrl();
            }
        }
        return "";
    }

    private String findRoleNameByRoleCode(Integer roleCode,List<Role> roleList){
        for(Role role:roleList){
            if(roleCode.equals(role.getRoleCode())){
                return role.getRoleName();
            }
        }
        return "";
    }
}
