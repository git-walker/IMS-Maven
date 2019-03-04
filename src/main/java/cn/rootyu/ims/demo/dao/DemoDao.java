package cn.rootyu.ims.demo.dao;

import cn.rootyu.base.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface DemoDao {
    String  getTest();
}
