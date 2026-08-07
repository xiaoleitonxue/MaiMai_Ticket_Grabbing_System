package com.mai.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mai.user.domain.po.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 用户数据访问层，提供用户余额扣减等自定义数据库操作
 * </p>
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * <p>
     * 扣减指定用户的余额，使用数据库层面原子操作保证并发安全
     * </p>
     *
     * @param userId 用户ID
     * @param totalFee 扣减金额
     */
    @Update("update user set balance = balance - ${totalFee} where id = #{userId}")
    void updateMoney(@Param("userId") Long userId, @Param("totalFee") Integer totalFee);
}