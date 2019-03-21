package cn.rootyu.rad.modules.sys.security;

import org.springframework.stereotype.Service;

/**
 * 表单验证过滤类
 * @author yuhui
 * @version 1.0
 */
@Service
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

	public static final String DEFAULT_MESSAGE_PARAM = "message";

	private String messageParam = DEFAULT_MESSAGE_PARAM;

	public String getMessageParam() {
		return messageParam;
	}
	
}