package cn.rootyu.ims.demo.dao;

import cn.rootyu.rad.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface DemoDao {
    String  getTest();
}
