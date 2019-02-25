package org.tj.lucene.controller;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 高并发下redis处理方案测试
 * @ClassName: RedisController
 * @Description: TODO
 * @author: 唐靖
 * @date: 2019年1月25日 上午11:22:11
 */
@Controller
@RequestMapping("/redis")
public class RedisController {
	@Resource
	private RedissonClient redissonClient;// 注入JedisPool

	/**
	 * 初始化库存
	 * @Title: order
	 * @Description: TODO
	 * @author: 唐靖
	 * @return
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping("/init.do")
	public String init() {
		try {
			RMap<String, Integer> totalMap = redissonClient.getMap("totalMap");
			totalMap.put("total", 50);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "success";
	}
	/**
	 * 模拟下单操作
	 * @Title: order
	 * @Description: TODO
	 * @author: 唐靖
	 * @return
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping("/order.do")
	public String order(HttpServletRequest request) {
		String result = "success";
		RLock lock = null;
		RMap<String, Integer> totalMap = redissonClient.getMap("totalMap");
		Integer data = totalMap.get("total");
		if (null == data) {
			return "error";
		}
		try {
			if (data > 0) {
				// synchronized (this) {
				lock = redissonClient.getLock("lock");
				if (lock.tryLock(10, 60, TimeUnit.SECONDS))// 第一个参数代表等待时间，第二是代表超过时间释放锁，第三个代表设置的时间制){
				{
					totalMap = redissonClient.getMap("totalMap");
					data = totalMap.get("total");
					try {
						TimeUnit.MICROSECONDS.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (data > 0) {
						data--;
						totalMap.put("total", data);
						result = result + data;
						// System.out.println("卖出去一个");
					} else {
						// System.out.println(data);
						result = "error";
						// System.out.println("没货了");
					}
					if (!Thread.currentThread().isInterrupted()) {
						lock.unlock();
					}
				} else {
					result = "no lock";
					// System.out.println("没拿到锁");
				}
			} else {
				result = "error";
			}
		} catch (Exception e) {
			if (e instanceof InterruptedException) {
				result = "lock error";
			} else {
				result = "unknown";
			}
		}
		return result;
	}
}
