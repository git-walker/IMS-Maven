package cn.rootyu.ims.demo.service.impl;

import cn.rootyu.ims.demo.service.DemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {
    public void test(){
        System.out.println("hello world");
    }
}