package tools;

import org.junit.jupiter.api.Test;
import tools.LRUCache;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/29.
 */
public class LRUCacheTest
{
	private LRUCache lruCache = new LRUCache(1,5);

	@Test void testLru()
	{
		assertEquals(0, lruCache.size());

		lruCache.put("0", 0);
		assertEquals(1, lruCache.size());

		lruCache.put("1", 1);
		lruCache.put("2", 2);
		lruCache.put("3", 3);
		lruCache.put("4", 4);
		assertEquals(5, lruCache.size());
		Integer replaced = lruCache.put("5", 5);

		assertEquals(5, lruCache.size());
		//should have removed key 0L
		assertFalse(lruCache.containsKey("0"));

		//for now, 1,2,3,4,5
		lruCache.get("1");
		//expect:[head]2,3,4,5,1[tail]
		assertEquals("2", lruCache.entrySet().iterator().next().getKey());

		lruCache.put("6", 6);
		//expect:3,4,5,1,6
		assertFalse(lruCache.containsKey("2"));

		//insert again, should not change anything
		lruCache.put("6", 6);
		assertTrue(lruCache.containsKey("3"));

	}
}
