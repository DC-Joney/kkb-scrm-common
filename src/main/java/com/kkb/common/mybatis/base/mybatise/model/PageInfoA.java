package com.kkb.common.mybatis.base.mybatise.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：PageInfo
 * 类描述：分页信息
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:05:19
 * version 2.0
 */
public class PageInfoA<T> implements Serializable {

	private static final long serialVersionUID = 3444007526867205523L;
	
	private Integer limit;					//每页数据数量限制
	private Integer pageIndex;				//当前查询的页码，从0开始计数
	private Integer start;					//开始记录，从0开始计数
	private Long recordsTotal;				//根据查询参数查询出的所有记录总数
	private Long recordsFiltered;			//根据查询参数查询出的所有记录总数
	private Map<String, Object> params;		//查询参数
	private List<T> data;					//查询结果集
	
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
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
		params.put("limit", this.limit);
		params.put("start", this.start);
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
}
