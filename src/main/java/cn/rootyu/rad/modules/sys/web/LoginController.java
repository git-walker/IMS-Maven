package cn.rootyu.rad.modules.sys.web;

import cn.rootyu.rad.common.config.Global;
import cn.rootyu.rad.common.security.shiro.session.SessionDAO;
import cn.rootyu.rad.common.servlet.ValidateCodeServlet;
import cn.rootyu.rad.common.utils.CacheUtils;
import cn.rootyu.rad.common.utils.CookieUtils;
import cn.rootyu.rad.common.utils.IdGen;
import cn.rootyu.rad.common.utils.StringUtils;
import cn.rootyu.rad.common.web.BaseController;
import cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter;
import cn.rootyu.rad.modules.sys.security.SystemAuthorizingRealm;
import cn.rootyu.rad.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName LoginController
 * @Description TODO
 * @Author yuhui
 * @Date 2019/3/18 21:00
 * @Version 1.0
 */
@Controller
public class LoginController extends BaseController {


//    @Autowired
//    private SessionDAO sessionDAO;

    /**
     * 管理登录
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();

//        if (logger.isDebugEnabled()){
//            logger.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
//        }

        // 如果已登录，再次访问主页，则退出原账号。
        if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))){
            CookieUtils.setCookie(response, "LOGINED", "false");
        }

        // 如果已经登录，则跳转到管理首页
        if(principal != null && !principal.isMobileLogin()){
            return "redirect:" + adminPath;
        }
        return "sys/sysLogin";
    }

    /**
     * 登录失败，真正登录的POST请求由Filter完成
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
    public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();

        // 如果已经登录，则跳转到管理首页
        if(principal != null){
            return "redirect:" + adminPath;
        }

        String username = WebUtils.getCleanParam(request, cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
        boolean rememberMe = WebUtils.isTrue(request, cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
        boolean mobile = WebUtils.isTrue(request, cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
        String exception = (String)request.getAttribute(cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        String message = (String)request.getAttribute(cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

        if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")){
            message = "用户或密码错误, 请重试.";
        }

        model.addAttribute(cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
        model.addAttribute(cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
        model.addAttribute(cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
        model.addAttribute(cn.rootyu.rad.modules.sys.security.FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);
        model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);

//        if (logger.isDebugEnabled()){
//            logger.debug("login fail, active session size: {}, message: {}, exception: {}",
//                    sessionDAO.getActiveSessions(false).size(), message, exception);
//        }

        // 非授权异常，登录失败，验证码加1。
        if (!UnauthorizedException.class.getName().equals(exception)){
            model.addAttribute("isValidateCodeLogin", isValidateCodeLogin(username, true, false));
        }

        // 验证失败清空验证码
        request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());

        // 如果是手机登录，则返回JSON字符串
        if (mobile){
            Map<String,Object> map = new HashMap();
            map.put("data", model);
            map.put("resultStatus", "601");
            return renderString(response, map);
        }

        return "sys/sysLogin";
    }

    /**
     * 登录成功，进入管理首页
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "${adminPath}")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();

        // 登录成功后，验证码计算器清零
        isValidateCodeLogin(principal.getLoginName(), false, true);

//        if (logger.isDebugEnabled()){
//            logger.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
//        }

        // 如果已登录，再次访问主页，则退出原账号。
        if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))){
            String logined = CookieUtils.getCookie(request, "LOGINED");
            if (StringUtils.isBlank(logined) || "false".equals(logined)){
                CookieUtils.setCookie(response, "LOGINED", "true");
            }else if (StringUtils.equals(logined, "true")){
                UserUtils.getSubject().logout();
                return "redirect:" + adminPath + "/login";
            }
        }
        return "sys/sysIndex";
    }

    /**
     * 获取主题方案
     */
    @RequestMapping(value = "/theme/{theme}")
    public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response){
        if (StringUtils.isNotBlank(theme)){
            CookieUtils.setCookie(response, "theme", theme);
        }else{
            theme = CookieUtils.getCookie(request, "theme");
        }
        return "redirect:"+request.getParameter("url");
    }

    /**
     * 是否是验证码登录
     * @param useruame 用户名
     * @param isFail 计数加1
     * @param clean 计数清零
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean){
        Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
        if (loginFailMap==null){
            loginFailMap = new HashMap();
            CacheUtils.put("loginFailMap", loginFailMap);
        }
        Integer loginFailNum = loginFailMap.get(useruame);
        if (loginFailNum==null){
            loginFailNum = 0;
        }
        if (isFail){
            loginFailNum++;
            loginFailMap.put(useruame, loginFailNum);
        }
        if (clean){
            loginFailMap.remove(useruame);
        }
        return loginFailNum >= 3;
    }

    /**
     * 登录页获取随机验证码
     * @param request
     * @param response
     */
    @RequestMapping(value = "randomImg")
    public void randomImg(HttpServletRequest request,HttpServletResponse response){

        // 在内存中创建图象
        int width = 80, height = 32;
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        Graphics g = image.getGraphics();
        // 生成随机类
        Random random = new Random();
        // 设定背景色
        g.setColor(getRandColor(230, 255));
        g.fillRect(0, 0, width, height);
        // 设定字体
        g.setFont(new Font("Arial", Font.CENTER_BASELINE | Font.ITALIC, 24));
        // 产生0条干扰线，
        g.drawLine(0, 0, 0, 0);
        // 取随机产生的认证码(4位数字)
        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand.append(rand);
            // 将认证码显示到图象中
            g.setColor(getRandColor(100, 150));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(rand, 16 * i + 7, 24);
        }
        //干扰线条
        for(int i=0;i<(random.nextInt(5)+5);i++){
            g.setColor(new Color(random.nextInt(255)+1,random.nextInt(255)+1,random.nextInt(255)+1));
            g.drawLine(random.nextInt(100),random.nextInt(height),random.nextInt(100),random.nextInt(height));
        }
        //String pageId = StringEscapeUtils.escapeHtml(request.getParameter("pageId"));
        String key = "_checkCode";
        // 将验证码存入页面KEY值的SESSION里面
        request.getSession().setAttribute(key, sRand.toString());
        // 图象生效
        g.dispose();

        try {
            response.setContentType("image/jpeg");
            ServletOutputStream outputStream = response.getOutputStream();
            // 输出图象到页面
            ImageIO.write(image, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * 给定范围获得随机颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
