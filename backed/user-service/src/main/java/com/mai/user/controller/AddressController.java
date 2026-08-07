package com.mai.user.controller;


import com.mai.common.exception.BadRequestException;
import com.mai.common.utils.BeanUtils;
import com.mai.common.utils.CollUtils;
import com.mai.common.utils.UserContext;
import com.mai.user.domain.dto.AddressDTO;
import com.mai.user.domain.po.Address;
import com.mai.user.service.IAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 收货地址控制器，提供地址查询接口，包含归属校验
 * </p>
 */
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Api(tags = "收货地址管理接口")
public class AddressController {

    private final IAddressService addressService;

    /**
     * <p>
     * 根据地址ID查询收货地址详情，并校验地址是否属于当前登录用户
     * </p>
     *
     * @param id 地址ID
     * @return 收货地址信息
     * @throws BadRequestException 当地址不属于当前用户时抛出
     */
    @ApiOperation("根据id查询地址")
    @GetMapping("{addressId}")
    public AddressDTO findAddressById(@ApiParam("地址id") @PathVariable("addressId") Long id) {
        // 1.根据id查询
        Address address = addressService.getById(id);
        // 2.判断当前用户
        Long userId = UserContext.getUser();
        if(!address.getUserId().equals(userId)){
            throw new BadRequestException("地址不属于当前登录用户");
        }
        return BeanUtils.copyBean(address, AddressDTO.class);
    }

    /**
     * <p>
     * 查询当前登录用户的所有收货地址
     * </p>
     *
     * @return 当前用户的收货地址列表
     */
    @ApiOperation("查询当前用户地址列表")
    @GetMapping
    public List<AddressDTO> findMyAddresses() {
        // 1.查询列表
        List<Address> list = addressService.query().eq("user_id", UserContext.getUser()).list();
        // 2.判空
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }
        // 3.转vo
        return BeanUtils.copyList(list, AddressDTO.class);
    }
}