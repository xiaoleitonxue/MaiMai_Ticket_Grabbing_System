package com.mai.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mai.common.exception.BadRequestException;
import com.mai.common.exception.BizIllegalException;
import com.mai.common.exception.ForbiddenException;
import com.mai.common.utils.UserContext;
import com.mai.user.config.JwtProperties;
import com.mai.user.domain.dto.LoginFormDTO;
import com.mai.user.domain.po.User;
import com.mai.user.domain.vo.UserLoginVO;
import com.mai.user.enums.UserStatus;
import com.mai.user.mapper.UserMapper;
import com.mai.user.service.IUserService;
import com.mai.user.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * <p>
 * 用户服务实现类，负责用户登录认证（密码校验、状态检查、JWT生成）和余额扣减等核心业务逻辑
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final PasswordEncoder passwordEncoder;

    private final JwtTool jwtTool;

    private final JwtProperties jwtProperties;

    /**
     * <p>
     * 用户登录认证，依次校验用户名、账户状态、密码，通过后生成JWT令牌并返回用户信息
     * </p>
     *
     * @param loginDTO 登录表单，包含用户名和密码
     * @return 用户登录信息，包含令牌、用户名和余额
     * @throws ForbiddenException 当用户被冻结时抛出
     * @throws BadRequestException 当用户名或密码错误时抛出
     */
    @Override
    public UserLoginVO login(LoginFormDTO loginDTO) {
        // 1.数据校验
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        // 2.根据用户名或手机号查询
        User user = lambdaQuery().eq(User::getUsername, username).one();
        Assert.notNull(user, "用户名错误");
        // 3.校验是否禁用
        if (user.getStatus() == UserStatus.FROZEN) {
            throw new ForbiddenException("用户被冻结");
        }
        // 4.校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }
        // 5.生成TOKEN
        String token = jwtTool.createToken(user.getId(), jwtProperties.getTokenTTL());
        // 6.封装VO返回
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setBalance(user.getBalance());
        vo.setToken(token);
        return vo;
    }

    /**
     * <p>
     * 扣减用户余额，先校验支付密码，再通过数据库更新余额，若余额不足则抛出异常
     * </p>
     *
     * @param pw 支付密码
     * @param totalFee 扣减金额
     * @throws BizIllegalException 当支付密码错误时抛出
     */
    @Override
    public void deductMoney(String pw, Integer totalFee) {
        log.info("开始扣款");
        // 1.校验密码
        User user = getById(UserContext.getUser());
        if(user == null || !passwordEncoder.matches(pw, user.getPassword())){
            // 密码错误
            throw new BizIllegalException("用户密码错误");
        }

        // 2.尝试扣款
        try {
            baseMapper.updateMoney(UserContext.getUser(), totalFee);
        } catch (Exception e) {
            throw new RuntimeException("扣款失败，可能是余额不足！", e);
        }
        log.info("扣款成功");
    }
}