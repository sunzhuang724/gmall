package com.atguigu.gmall.ums.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.ums.mapper.UserMapper;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.service.UserService;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
@Autowired
private UserMapper userMapper;
    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        switch (type){
            case 1:wrapper.eq("username",data);break;
            case 2:wrapper.eq("phone",data);break;
            case 3:wrapper.eq("email",data);break;
            default:return null;
        }
        return userMapper.selectCount(wrapper) ==0;

    }

    @Override
    public void register(UserEntity userEntity, String code) {
        String salt = StringUtils.replace(UUID.randomUUID().toString(),"-","");
        userEntity.setSalt(salt);
//        对密码加密
        userEntity.setPassword(DigestUtils.md5Hex(userEntity.getPassword() + salt));
//        对表单其他项设置
        userEntity.setCreateTime(new Date());
        userEntity.setLevelId(1l);
        userEntity.setStatus(1);
        userEntity.setIntegration(0);
        userEntity.setGrowth(0);
        userEntity.setNickname(userEntity.getNickname());

        boolean b = this.save(userEntity);



    }

    @Override
    public UserEntity queryUser(String loginName, String password) {
        // 1.先根据用户的登录名查询出用户列表
        List<UserEntity> userEntities = this.list(new QueryWrapper<UserEntity>().or(wrapper -> wrapper.eq("username", loginName).or().eq("email", loginName).or().eq("phone", loginName)));
        // 2. 判断登录输入是否合法
        if (CollectionUtils.isEmpty(userEntities)){
            return null;
        }
//      返回复合查询条件的结果
        for (UserEntity userEntity : userEntities) {
            // 获取每个用户的盐，对用户输入的明文密码加盐加密
            String pwd = DigestUtils.md5Hex(password + userEntity.getSalt());
            // 比较数据库中的密码和用户输入的密码
            if (StringUtils.equals(userEntity.getPassword(), pwd)){
                return userEntity;
            }
        }

        return null;
    }

}