package com.mai.user.controller;

import com.mai.user.domain.dto.LoginFormDTO;
import com.mai.user.domain.vo.UserLoginVO;
import com.mai.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户控制器，提供用户登录和余额扣减等HTTP接口
 * </p>
 */
@Api(tags = "用户相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * <p>
     * 用户登录接口，校验用户名密码并返回JWT令牌
     * </p>
     *
     * @param loginFormDTO 登录表单，包含用户名和密码
     * @return 用户登录信息，包含令牌和余额
     */
    @ApiOperation("用户登录接口")
    @PostMapping("login")
    public UserLoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO){
        return userService.login(loginFormDTO);
    }

    /**
     * <p>
     * 扣减用户余额，需要校验支付密码
     * </p>
     *
     * @param pw 支付密码
     * @param amount 扣减金额
     */
    @ApiOperation("扣减余额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pw", value = "支付密码"),
            @ApiImplicitParam(name = "amount", value = "支付金额")
    })
    @PutMapping("/money/deduct")
    public void deductMoney(@RequestParam("pw") String pw,@RequestParam("amount") Integer amount){
        userService.deductMoney(pw, amount);
    }
}