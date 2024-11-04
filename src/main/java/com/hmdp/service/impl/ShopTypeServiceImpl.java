package com.hmdp.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryTypeList() {
        String key = "shop:type";
        List<ShopType> typeList = new ArrayList<>();
        // 1.读取redis
        String shopType = stringRedisTemplate.opsForValue().get(key);

        // 2.判断是否为空
        if (StrUtil.isNotBlank(shopType)) {
            typeList = JSONObject.parseArray(shopType, ShopType.class);
            return Result.ok(typeList);
        }

        // 为空，查数据库
        typeList = query().orderByAsc("sort").list();
        if (typeList == null) {
            return Result.fail("未查到店铺类型");
        }
        // 存入redis
        String str = JSONObject.toJSONString(typeList);//把list转换为String
        stringRedisTemplate.opsForValue().set(key, str);
        return Result.ok(typeList);
    }
}
