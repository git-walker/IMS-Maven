package cn.rootyu.rad.modules.sys.web;

import cn.rootyu.rad.common.web.BaseController;
import cn.rootyu.rad.modules.sys.security.SystemAuthorizingRealm;
import cn.rootyu.rad.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName LoginController
 * @Description TODO
 * @Author yuhui
 * @Date 2019/3/18 21:00
 * @Version 1.0
 */
@Controller
public class LoginController extends BaseController {


    /**
     * 管理登录
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();
        return "sys/sysLogin";
    }
}
