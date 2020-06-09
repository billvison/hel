package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.model.ConfigurationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 配置dao
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Mapper
@Component
public interface ConfigurationDao {

    String getConfiguratioValue(@Param("confKey") String confKey);

    void addConfiguration(ConfigurationEntity configurationEntity);

    void updateConfiguration(ConfigurationEntity configurationEntity);
}
