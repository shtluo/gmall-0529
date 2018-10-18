package com.atguigu.gmall.search;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {
	@Autowired
	JestClient jestClient;

	@Test
	public void contextLoads() throws IOException {
		String query="{\n" +
				"  \"query\": {\n" +
				"    \"match\": {\n" +
				"      \"actorList.name\": \"张涵予\"\n" +
				"    }\n" +
				"  }\n" +
				"}";
		Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie").build();

		SearchResult result = jestClient.execute(search);
		List<SearchResult.Hit<HashMap, Void>> hits = result.getHits(HashMap.class);
		for (SearchResult.Hit<HashMap, Void> hit : hits) {
			System.out.println(hit.score);
			System.out.println(hit.source);

		}

	}

}
