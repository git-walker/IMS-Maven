package cn.rootyu.rad.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.Validator;

/**
 * @ClassName BaseController
 * @Description TODO
 * @Author yuhui
 * @Date 2019/3/19 19:23
 * @Version 1.0
 */
public  abstract class BaseController {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 管理基础路径
     */
    @Value("${adminPath}")
    protected String adminPath;

    /**
     * 验证Bean实例对象
     */
    @Autowired
    protected Validator validator;


}
