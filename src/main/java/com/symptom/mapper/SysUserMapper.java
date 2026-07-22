package com.symptom.mapper;

import com.symptom.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    SysUser findByUsername(@Param("username") String username);
    List<SysUser> findAll();
    int insert(SysUser user);
    int update(SysUser user);
    int delete(@Param("id") Integer id);
}
