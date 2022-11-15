package com.kkb.common.mybatis.base.mybatise.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：PageInfo
 * 类描述：分页信息
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:05:06
 * version 2.0
 */
public class PageInfo<T> implements Serializable {

	private static final long serialVersionUID = 3444007526867205523L;
	
	private Integer pageSize;					//每页数据数量限制
	private Integer page;				//当前查询的页码，从1开始计数
	private Long recordsTotal;				//根据查询参数查询出的所有记录总数
	private Long recordsFiltered;			//根据查询参数查询出的所有记录总数
	private Map<String, Object> params = new HashMap<>();		//查询参数
	private List<T> data;					//查询结果集
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Long getRecordsTotal() {
		return recordsTotal;
	}
	public void setRecordsTotal(Long recordsTotal) {
		this.recordsTotal = recordsTotal;
		this.setRecordsFiltered(recordsTotal);
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		params.put("pageSize", this.pageSize);
		params.put("pageIndex", (this.page - 1) * this.pageSize);
		this.params = params;
	}
	public Long getRecordsFiltered() {
		return recordsFiltered;
	}
	public void setRecordsFiltered(Long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}

	public void addParams(String key, Object value){
        params.put(key, value);
	}
}
