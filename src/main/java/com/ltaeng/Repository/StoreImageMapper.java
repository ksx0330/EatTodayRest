package com.ltaeng.Repository;

import com.ltaeng.Domain.StoreImage;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreImageMapper {

    @Insert("INSERT INTO `store_image` (`storeId`, `path`) VALUES (#{storeId}, #{path})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void insert(StoreImage storeImage);

    @Update("UPDATE `store_image` SET `storeId` = #{storeId}, `path` = #{path} WHERE id = #{id}")
    void update(StoreImage storeImage);

    @Select("SELECT * FROM `store_image` WHERE `storeId` = #{id}")
    List<StoreImage> find(@Param("id") int id);

    @Delete("DELETE FROM `store_image` WHERE id = #{id}")
    void delete(@Param("id") int id);
}
