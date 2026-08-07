package com.mai.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mai.user.domain.po.Address;
import com.mai.user.mapper.AddressMapper;
import com.mai.user.service.IAddressService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收货地址服务实现类，负责用户收货地址的数据持久化操作
 * </p>
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

}