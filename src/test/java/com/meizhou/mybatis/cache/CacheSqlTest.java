package com.meizhou.mybatis.cache;

import java.util.Arrays;

/**
 * Created by meizhou on 2018/8/19.
 */
public class CacheSqlTest {

    public static void main(String[] args) {
        System.out.println(CacheSql.buildCacheSql("select * from city where city_id>1 and user_id=1 or (aaa=1 and kkk=9)", Arrays.asList(), "mysql"));
        System.out.println(CacheSql.buildCacheSql("delete from t where id = 2 and name = 'wenshao'", Arrays.asList(), "mysql"));
        System.out.println(CacheSql.buildCacheSql("update t set val = 12312 where id = 2 and name = 'wenshao'", Arrays.asList(), "mysql"));
        System.out.println(CacheSql.buildCacheSql("INSERT INTO Persons (LastName, Address) VALUES ('Wilson', 'Champs-Elysees')", Arrays.asList(), "mysql"));
    }

}