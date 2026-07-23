package com.symptom.service;

import com.symptom.entity.SysUser;
import com.symptom.entity.OperationLog;
import com.symptom.mapper.SysUserMapper;
import com.symptom.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final SysUserMapper userMapper;
    private final OperationLogMapper operationLogMapper;

    public UserService(SysUserMapper userMapper, OperationLogMapper operationLogMapper) {
        this.userMapper = userMapper;
        this.operationLogMapper = operationLogMapper;
    }

    public SysUser login(String username, String password) {
        SysUser user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public void logOperation(String username, String operation, String ip) {
        OperationLog log = new OperationLog();
        log.setUsername(username);
        log.setOperation(operation);
        log.setIp(ip);
        operationLogMapper.insert(log);
    }

    public List<SysUser> findAll() {
        return userMapper.findAll();
    }

    public void saveUser(SysUser user) {
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.update(user);
        }
    }

    public void deleteUser(Integer id) {
        userMapper.delete(id);
    }

    public List<OperationLog> getOperationLogs() {
        return operationLogMapper.findAll();
    }
}
