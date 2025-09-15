package com.inso.framework.utils;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Title: 多页查询页面控制器
 * </p>
 * <p>
 * Description:total,TotalPages,StartRow,EndRow,
 * PageSize,CurrentPage,PreviousPage,NextPage,FirstPage,LastPage,
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author wab
 * @version 1.0
 */
public class RowPager<T> {

	private static final RowPager emptyRowPage = new RowPager<>(0, Collections.emptyList());


	private int total;

	private int StartRow, endRow;

	private int pageSize;

	private int currentPage, firstPage, lastPage;

	private List<T> list;


	public int getTotal() {
		return getTotalRows();
	}
	
	public int getTotalRows() {
		return total;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getStartRow() {
		return StartRow;
	}

	public int getPreviousPage() {
		return this.validPage(this.currentPage - 1);
	}

	public int getNextPage() {
		return this.validPage(currentPage + 1);
	}

	public int getEndRow() {
		return endRow;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getLastPage() {
		return lastPage;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}


	public static RowPager getEmptyRowPager()
	{
		return emptyRowPage;
	}

	/**
	 * @param total
	 *            int
	 * @param pageno
	 *            int
	 * @param pagesize
	 *            int
	 */
	public RowPager(int total, int pageno, int pagesize) {
		this.total = total;
		this.pageSize = pagesize;
		this.firstPage = 1;

		this.currentPage = validPage(pageno);
		// 计算当前页的开始记录号,结束记录号;
		this.StartRow = (this.currentPage - 1) * this.pageSize + 1;
		this.endRow = (this.currentPage < this.lastPage ? this.currentPage
				* this.pageSize
				: this.total);

	}

	public RowPager(int total, List<T> rowList) {
		this.total = total;
		if(CollectionUtils.isEmpty(rowList))
		{
			this.list = Collections.emptyList();
		}
		else
		{
			this.list = rowList;
		}
	}

	public RowPager(long total, List<T> rowList) {
		this.total = (int)total;
		if(CollectionUtils.isEmpty(rowList))
		{
			this.list = Collections.emptyList();
		}
		else
		{
			this.list = rowList;
		}
	}

	private int validPage(int pageno) {
		return (pageno < this.lastPage ? (pageno > this.firstPage ? pageno
				: this.firstPage) : this.lastPage);
	}

//	public void addRow(T row) {
//		if(this.list == null)
//		{
//			this.list = Lists.newArrayList();
//		}
//		this.list.add(row);
//	}
//
//	public void addRows(List<T> rows)
//	{
//		this.list = rows;
//	}
}
