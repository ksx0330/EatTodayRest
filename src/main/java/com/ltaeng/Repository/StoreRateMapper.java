package com.ltaeng.Repository;

import com.ltaeng.Domain.StoreRate;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRateMapper {
    @Insert("INSERT INTO `store_rate` (`storeId`, `userId`. `rate`) VALUES (#{storeId}, #{userId}, #{rate})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void insert(StoreRate storeRate);

    @Update("UPDATE `store_rate` SET `storeId` = #{storeId}, `userId` = #{userId}, `rate` = #{rate} WHERE id = #{id}")
    void update(StoreRate storeRate);

    @Select("SELECT * FROM `store_rate` WHERE `storeId` = #{id}")
    List<StoreRate> find(@Param("id") int id);

    @Select("SELECT * FROM `store_rate` WHERE `id` = #{id}")
    StoreRate findById(@Param("id") int id);

    @Delete("DELETE FROM `store_rate` WHERE id = #{id}")
    void delete(@Param("id") int id);


}
