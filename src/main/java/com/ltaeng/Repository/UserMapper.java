package com.ltaeng.Repository;

import com.ltaeng.Domain.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    @Insert("INSERT INTO `user`(`uniqueCode`, `name`, `email`, `password`) VALUES ([${uniqueCode},${name},${email},${password})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void insert(User user);

    @Update("UPDATE `user` SET `uniqueCode`=${uniqueCode},`name`=${name},`email`=${email},`password`=${password} WHERE id = #{id}")
    void update(User user);

    @Select("SELECT * FROM `user` WHERE `email` = #{email} AND `password` = #{password}")
    User login(@Param("email") String email, @Param("password") String password);

    @Select("SELECT * FROM `user` WHERE `id` = #{id}")
    User find(@Param("id") int id);

    @Select("SELECT * FROM `user` WHERE `uniqueCode` = #{uniqueCode}")
    User findByUniqueCode(@Param("uniqueCode") String uniqueCode);

    @Delete("DELETE FROM `user` WHERE id = #{id}")
    void delete(@Param("id") int id);

    @Select("DELETE FROM `user` WHERE `uniqueCode` = #{uniqueCode}")
    void deleteByUniqueCode(@Param("uniqueCode") String uniqueCode);
}
