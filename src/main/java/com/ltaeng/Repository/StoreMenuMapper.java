package com.ltaeng.Repository;

import com.ltaeng.Domain.StoreMenu;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreMenuMapper {
    @Insert("INSERT INTO `store_menu` (`storeId`, `name`, `price`) VALUES (#{storeId}, #{name}, #{price})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void insert(StoreMenu storeMenu);

    @Update("UPDATE `store_menu` SET `storeId` = #{storeId}, `name` = #{name}, `price` = #{price} WHERE id = #{id}")
    void update(StoreMenu storeMenu);

    @Select("SELECT * FROM `store_menu` WHERE `storeId` = #{id}")
    List<StoreMenu> find(@Param("id") int id);

    @Delete("DELETE FROM `store_menu` WHERE id = #{id}")
    void delete(@Param("id") int id);

}
