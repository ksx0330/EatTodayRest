package com.ltaeng.Repository;

import com.ltaeng.Domain.StoreNormal;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreMapper {

    @Insert("INSERT INTO `store`(`daumId`, `name`, `address`, `x`, `y`, `phone`, `flag`, `picture`) VALUES (${daumId}, #{name},#{address},#{x},#{y},#{phone},#{flag},#{picture})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void insert(StoreNormal store);

    @Update("UPDATE `store` SET `daumId`=${daumId}, `name`=#{name},`address`=#{address},`x`=#{x},`y`=#{y},`phone`=#{phone},`flag`=#{flag},`picture`=#{picture} WHERE id = #{id}")
    void update(StoreNormal store);

    @Select("SELECT * FROM `store` WHERE `id` = ${id}")
    StoreNormal find(@Param("id") int id);

    @Select("SELECT * FROM `store` WHERE `daumId` = ${id}")
    StoreNormal findStoreId(@Param("id") int id);

    @Delete("DELETE FROM `store` WHERE id = ${id}")
    void delete(@Param("id") int id);


}
