package com.mai.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mai.user.domain.dto.LoginFormDTO;
import com.mai.user.domain.po.User;
import com.mai.user.domain.vo.UserLoginVO;

/**
 * <p>
 * 用户服务接口，提供用户登录认证和余额扣减等核心业务操作
 * </p>
 */
public interface IUserService extends IService<User> {

    /**
     * <p>
     * 用户登录认证，校验用户名密码及用户状态，成功则返回JWT令牌和用户信息
     * </p>
     *
     * @param loginFormDTO 登录表单，包含用户名和密码
     * @return 用户登录信息，包含令牌、用户名和余额
     * @throws ForbiddenException 当用户被冻结时抛出
     * @throws BadRequestException 当用户名或密码错误时抛出
     */
    UserLoginVO login(LoginFormDTO loginFormDTO);

    /**
     * <p>
     * 扣减用户余额，先校验支付密码再执行扣款操作
     * </p>
     *
     * @param pw 支付密码
     * @param totalFee 扣减金额
     * @throws BizIllegalException 当支付密码错误时抛出
     */
    void deductMoney(String pw, Integer totalFee);
}