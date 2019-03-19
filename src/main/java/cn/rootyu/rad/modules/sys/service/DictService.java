/**
 * Copyright &copy; 2012-2014 <a href="http://www.dhc.com.cn">DHC</a> All rights reserved.
 */
package cn.rootyu.rad.modules.sys.service;

import cn.rootyu.rad.common.service.CrudService;
import cn.rootyu.rad.common.utils.CacheUtils;
import cn.rootyu.rad.modules.sys.dao.DictDao;
import cn.rootyu.rad.modules.sys.entity.Dict;
import cn.rootyu.rad.modules.sys.utils.DictUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典Service
 * @author DHC
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class DictService extends CrudService<DictDao, Dict> {
	
	/**
	 * 查询字段类型列表
	 * @return
	 */
	public List<String> findTypeList(){
		return dao.findTypeList(new Dict());
	}

	@Transactional(readOnly = false)
	public void save(Dict dict) {
		super.save(dict);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}

	@Transactional(readOnly = false)
	public void delete(Dict dict) {
		super.delete(dict);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	@Transactional(readOnly = false)
	public void batchDelete(String[] ids) {
    	for (int i = 0; i < ids.length; i++) {
    		Dict dict = new Dict(ids[i]);
    		super.delete(dict);
    	}
    	CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
}
