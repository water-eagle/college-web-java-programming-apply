package com.springmvc.interceptor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MonitoringInterceptor implements HandlerInterceptor {
	private final Logger log = LoggerFactory.getLogger(MonitoringInterceptor.class);
	ThreadLocal<StopWatch> stopWatchLocal = new ThreadLocal<StopWatch>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		StopWatch stopWatch = new StopWatch(handler.toString());
		stopWatch.start(handler.toString());
		this.stopWatchLocal.set(stopWatch);
		this.log.info("접근한 URL 경로: {}", this.getURLPath(request));
		this.log.info("요청 처리 시작 시간: {}", this.getCurrentTime());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
		StopWatch stopWatch = this.stopWatchLocal.get();
		stopWatch.stop();
		this.log.info("요청 처리 종료 시간: {}", this.getCurrentTime());
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		StopWatch stopWatch = this.stopWatchLocal.get();
		this.log.info("요청 처리 소요 시간: {} ms", stopWatch.getTotalTimeMillis());
		this.stopWatchLocal.remove();
	}

	private String getURLPath(HttpServletRequest request) {
		StringBuffer currentPath = request.getRequestURL();
		String queryString = request.getQueryString();
		queryString = queryString == null ? "" : "?" + queryString;
		return currentPath + queryString;
	}

	private String getCurrentTime() {
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		return formatter.format(calendar.getTime());
	}
}
