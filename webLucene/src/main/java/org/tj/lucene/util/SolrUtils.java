package org.tj.lucene.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 * @Description:单例的solr服务工具类
 * @author kang
 * @创建时间 2015下午2:17:18
 */
public class SolrUtils {
	private static String URL = "http://192.168.171.132:8080/solr/";
	private static String CLOUDHOST = "192.168.171.133:2181,192.168.171.133:2182,192.168.171.133:2183";

	/**
	 * 获取单独的solr客户端
	 * @Title: getClient
	 * @Description: TODO
	 * @author: 唐靖
	 * @param core
	 * @return
	 * @return: HttpSolrClient
	 */
	public static HttpSolrClient getClient(String core) {
		HttpSolrClient client = new HttpSolrClient(URL + core);
		client.setSoTimeout(100000); // socket read timeout
		client.setConnectionTimeout(100000);
		client.setDefaultMaxConnectionsPerHost(100);
		client.setMaxTotalConnections(100);
		client.setFollowRedirects(false); // defaults to false
		client.setAllowCompression(true);
		// client.setMaxRetries(1); // defaults to 0. > 1 not recommended.
		return client;
	}
	/**
	 * 获取solr集群的客户端
	 * @Title: getCloudSolrClient
	 * @Description: TODO
	 * @author: 唐靖
	 * @param core
	 * @return
	 * @return: CloudSolrClient
	 */
	public static CloudSolrClient getCloudSolrClient(String core) {
		CloudSolrClient client = new CloudSolrClient(CLOUDHOST);
		client.setDefaultCollection(core);
		client.setZkClientTimeout(10000);
		client.setZkConnectTimeout(10000);
		client.connect();
		return client;
	}
	/**
	 * 添加单个对象到索引
	 * @Title: addBean
	 * @Description: TODO
	 * @author: 唐靖
	 * @param object
	 * @throws IOException
	 * @throws SolrServerException
	 * @return: void
	 */
	public static void addBean(SolrClient client, Object object) throws IOException, SolrServerException {
		client.addBean(object);
		client.commit(false, false);
	}
	/**
	 * 添加集合到索引
	 * @Title: addBeans
	 * @Description: TODO
	 * @author: 唐靖
	 * @param lists
	 * @throws SolrServerException
	 * @throws IOException
	 * @return: void
	 */
	public static <E> void addBeans(SolrClient client, List<E> lists) throws SolrServerException, IOException {
		client.addBeans(lists);
		client.commit(false, false);
	}
	/**
	 * 根据id删除记录
	 * @Title: deleteById
	 * @Description: TODO
	 * @author: 唐靖
	 * @param idName
	 * @param id
	 * @throws SolrServerException
	 * @throws IOException
	 * @return: void
	 */
	public static void deleteById(SolrClient client, String idName, Object id) throws SolrServerException, IOException {
		client.deleteByQuery(idName + ":" + id.toString());
		client.commit(false, false);
	}
	/**
	 * 根据ids批量删除
	 * @Title: deleteByIds
	 * @Description: TODO
	 * @author: 唐靖
	 * @param idName
	 * @param ids
	 * @throws SolrServerException
	 * @throws IOException
	 * @return: void
	 */
	public static <E> void deleteByIds(SolrClient client, String idName, List<E> ids)
			throws SolrServerException, IOException {
		if (ids.size() > 0) {
			StringBuffer query = new StringBuffer(idName + ":" + ids.get(0));
			for (int i = 1; i < ids.size(); i++) {
				if (null != ids.get(i)) {
					query.append(" OR " + idName + ":" + ids.get(i).toString());
				}
			}
			client.deleteByQuery(query.toString());
			client.commit(false, false);
		}
	}
	/**
	 * 根据查询从索引删除
	 * @Title: deleteByQuery
	 * @Description: TODO
	 * @author: 唐靖
	 * @param query
	 * @throws SolrServerException
	 * @throws IOException
	 * @return: void
	 */
	public static void deleteByQuery(SolrClient client, String query) throws SolrServerException, IOException {
		client.deleteByQuery(query);
		client.commit(false, false);
	}
	/**
	 * 删除所有
	 * @Title: deleteAll
	 * @Description: TODO
	 * @author: 唐靖
	 * @throws SolrServerException
	 * @throws IOException
	 * @return: void
	 */
	public static void deleteAll(SolrClient client) throws SolrServerException, IOException {
		client.deleteByQuery("*:*");
		client.commit(false, false);
	}
	/**
	 * 关键字分页查询
	 * @Title: getByPage
	 * @Description: TODO
	 * @author: 唐靖
	 * @param keywords
	 * @param pageNum
	 * @param pageSize
	 * @param clzz
	 * @param lang
	 * @param distinguish
	 * @return
	 * @return: Page<T>
	 * @throws IOException
	 */
	public static <T> Page<T> getByPage(SolrClient client, String keywords, int pageNum, int pageSize, Class<T> clzz,
			String lang, Boolean distinguish) throws IOException {
		SolrQuery query = new SolrQuery();
		query.setQuery(keywords)// 查询内容
				.setStart((pageNum - 1) * pageSize)// 分页
				.setRows(pageSize);//
		if (distinguish) {
			query.addFilterQuery("lang:" + lang);// 中英文区别
		}
		QueryResponse response = null;
		try {
			response = client.query(query);
		} catch (SolrServerException e) {
			e.printStackTrace();
			return null;
		}
		// 查询结果集
		SolrDocumentList results = response.getResults();
		// 获取对象
		List<T> beans = client.getBinder().getBeans(clzz, results);
		// 总记录数
		int total = new Long(response.getResults().getNumFound()).intValue();
		return new Page<T>(pageNum, pageSize, total, beans);
	}
	/**
	 * 判断是否有索引数据
	 * @Title: hasIndex
	 * @Description: TODO
	 * @author: 唐靖
	 * @return
	 * @return: boolean
	 * @throws IOException
	 */
	public static boolean hasIndex(SolrClient client) throws IOException {
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*")// 查询内容
				.setStart(0)// 分页
				.setRows(1);
		QueryResponse response = null;
		try {
			response = client.query(query);
		} catch (SolrServerException e) {
			e.printStackTrace();
			return false;
		}
		// 总记录数
		int total = new Long(response.getResults().getNumFound()).intValue();
		return total == 0 ? false : true;
	}
	/**
	 * 带高亮的关键字查询
	 * @Title: getHighterByPage
	 * @Description: TODO
	 * @author: 唐靖
	 * @param keywords
	 * @param pageNum
	 * @param pageSize
	 * @param hlFields
	 * @param preTag
	 * @param postTag
	 * @param clzz
	 * @param idName
	 * @param lang
	 * @param distinguish
	 * @return
	 * @return: Page<T>
	 * @throws IOException
	 */
	public static <T> Page<T> getHighterByPage(SolrClient client, String keywords, int pageNum, int pageSize,
			List<String> hlFields, String preTag, String postTag, Class<T> clzz, String idName, String lang,
			Boolean distinguish) throws IOException {
		SolrQuery query = new SolrQuery();
		query.setQuery(keywords)// 查询内容
				.setHighlight(true)// 设置高亮显示
				.setHighlightSimplePre(preTag)// 渲染头标签
				.setHighlightSimplePost(postTag)// 尾标签
				.setStart((pageNum - 1) * pageSize)// 分页
				.setRows(pageSize);//
		if (distinguish) {
			query.addFilterQuery("lang:" + lang);// 中英文区别
		}
		// 设置高亮区域
		for (String hl : hlFields) {
			query.addHighlightField(hl);
		}
		QueryResponse response = null;
		try {
			response = client.query(query);
		} catch (SolrServerException e) {
			e.printStackTrace();
			return null;
		}
		SolrDocumentList results = response.getResults();
		// 总记录数
		int total = new Long(results.getNumFound()).intValue();
		// 查询结果集
		ArrayList<T> list = new ArrayList<T>();
		try {
			Object object = null;
			Method method = null;
			Class<?> fieldType = null;
			Map<String, Map<String, List<String>>> map = response.getHighlighting();
			for (SolrDocument solrDocument : results) {
				object = clzz.newInstance();
				// 得到所有属性名
				Collection<String> fieldNames = solrDocument.getFieldNames();
				for (String fieldName : fieldNames) {
					Field[] fields = clzz.getDeclaredFields();
					for (Field f : fields) {
						// 如果实体属性名和查询返回集中的字段名一致，填充对应的set方法
						if (f.getName().equals(fieldName)) {
							f = clzz.getDeclaredField(fieldName);
							fieldType = f.getType();
							// 构造set方法名 setId
							String dynamicSetMethod = dynamicMethodName(f.getName(), "set");
							// 获取方法
							method = clzz.getMethod(dynamicSetMethod, fieldType);
							// 获取fieldType类型
							// fieldType = getFileType(fieldType);
							// 获取到的属性
							method.invoke(object, fieldType.cast(solrDocument.getFieldValue(fieldName)));
							for (String hl : hlFields) {
								if (hl.equals(fieldName)) {
									String idv = solrDocument.getFieldValue(idName).toString();
									List<String> hfList = map.get(idv).get(fieldName);
									if (null != hfList && hfList.size() > 0) {
										// 高亮添加
										method.invoke(object, fieldType.cast(hfList.get(0)));
									} else {
										method.invoke(object, fieldType.cast(solrDocument.getFieldValue(fieldName)));
									}
								}
							}
						}
					}
				}
				list.add(clzz.cast(object));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return new Page<T>(pageNum, pageSize, total, list);
	}
	/**
	 * @Description: 动态生成方法名 @author kang @创建时间 2015下午9:16:59 @param @param name @param @param
	 *               string @param @return @return String @throws
	 */
	private static String dynamicMethodName(String name, String string) {
		if (Character.isUpperCase(name.charAt(0)))
			return string + name;
		else
			return (new StringBuilder()).append(string + Character.toUpperCase(name.charAt(0)))
					.append(name.substring(1)).toString();
	}
	/**
	 * 因为反射的属性可能是一个集合,所以在利用反射转换之前,需要进行更精确地判断,这实例中实体对象中的属性为简单类型,所以这个方法可以处理
	 * @Title: getFileType
	 * @Description: TODO
	 * @author: 唐靖
	 * @param fieldType
	 * @return
	 * @return: Class<?>
	 */
	public Class<?> getFileType(Class<?> fieldType) {
		// 如果是 int, float等基本类型，则需要转型
		if (fieldType.equals(Integer.TYPE)) {
			return Integer.class;
		} else if (fieldType.equals(Float.TYPE)) {
			return Float.class;
		} else if (fieldType.equals(Double.TYPE)) {
			return Double.class;
		} else if (fieldType.equals(Boolean.TYPE)) {
			return Boolean.class;
		} else if (fieldType.equals(Short.TYPE)) {
			return Short.class;
		} else if (fieldType.equals(Long.TYPE)) {
			return Long.class;
		} else if (fieldType.equals(String.class)) {
			return String.class;
		} else if (fieldType.equals(Collection.class)) {
			return Collection.class;
		}
		return null;
	}
}
