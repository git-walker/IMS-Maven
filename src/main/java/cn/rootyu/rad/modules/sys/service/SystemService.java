package cn.rootyu.rad.modules.sys.service;

import cn.rootyu.rad.common.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 * @author yuhui
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService {

	

}
