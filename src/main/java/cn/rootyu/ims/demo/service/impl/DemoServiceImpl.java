package cn.rootyu.ims.demo.service.impl;

import cn.rootyu.ims.demo.dao.DemoDao;
import cn.rootyu.ims.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {
    private final DemoDao demoDao;

    @Autowired
    public DemoServiceImpl(DemoDao demoDao) {
        this.demoDao = demoDao;
    }

    public void test(){
        String str = demoDao.getTest();

        System.out.println(str);
    }
}