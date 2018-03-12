package com.sunit.global;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * 
 * 类名称：GzipJsfilter
 * 类描述：
 * 创建人：Administrator
 * 创建时间：Sep 13, 2017 5:18:07 PM
 * 修改人：joye
 * 修改时间：Sep 13, 2017 5:18:07 PM
 * 修改备注：
 * @version 
 *
 */ 
public class GzipJsfilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		response.setCharacterEncoding("utf-8");
		HttpServletResponse res=((HttpServletResponse)response);
		res.addHeader("Content-Encoding", "gzip");
		chain.doFilter(request, response);
		
		 	
	}

	public void init(FilterConfig arg0) throws ServletException {

	}
	
	public static void main(String[] args) {
		System.err.println(1);
	}

}
