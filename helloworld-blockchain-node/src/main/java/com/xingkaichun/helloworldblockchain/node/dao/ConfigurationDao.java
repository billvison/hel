package com.xingkaichun.helloworldblockchain.node.dao;

import com.xingkaichun.helloworldblockchain.node.model.ConfigurationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ConfigurationDao {

    String getConfiguratioValue(@Param("confKey") String confKey);

    void addConfiguration(ConfigurationEntity configurationEntity);
}
