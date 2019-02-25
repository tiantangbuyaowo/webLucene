package org.tj.lucene.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Test;
import org.tj.lucene.bean.Content;

public class SolrTest {
	/**
	 * slor保存
	 * @Title: ClientTestSave
	 * @Description: TODO
	 * @author: 唐靖
	 * @return: void
	 */
	@Test
	public void ClientTestSave() {
		Content c = new Content();
		c.setId(IDUtil.getId());
		c.setTitle("朱丽叶与罗密欧");
		c.setContent("最佳答案:		 凯普莱特和蒙太古是一座城市的两大家族,这两大家族有宿仇,经常械斗。蒙太古家有个儿子叫罗密欧,品行端方,是个大家都很喜欢的小伙子。有一天,他听说自己");
		try {
			// HttpSolrClient client = SolrUtils.getClient("core");
			CloudSolrClient client = SolrUtils.getCloudSolrClient("core");
			SolrUtils.addBean(client, c);
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * solr查询，无高亮
	 * @Title: ClientTestSearch
	 * @Description: TODO
	 * @author: 唐靖
	 * @return: void
	 */
	@Test
	public void ClientTestSearch() {
		try {
			// HttpSolrClient client = SolrUtils.getClient("core");
			CloudSolrClient client = SolrUtils.getCloudSolrClient("core");
			Page<Content> p = SolrUtils.getByPage(client, "罗密", 1, 5, Content.class, "", false);
			for (Content c : p.getDatas()) {
				System.out.println(c.getContent());
			}
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * solr查询，带高亮
	 * @Title: ClientTestSearchHigh
	 * @Description: TODO
	 * @author: 唐靖
	 * @return: void
	 */
	@Test
	public void ClientTestSearchHigh() {
		try {
			List<String> hlFields = new ArrayList<String>();
			hlFields.add("title");
			hlFields.add("content");
			HttpSolrClient client = SolrUtils.getClient("core");
			Page<Content> p = SolrUtils.getHighterByPage(client, "喜欢", 1, 10, hlFields, "<font color='red'>", "</font>",
					Content.class, "id", "", false);// ("罗密", 1, 5,
													// Content.class,
													// "",
													// false);
			client.close();
			for (Content c : p.getDatas()) {
				System.out.println(c.getTitle());
				System.out.println(c.getContent());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
