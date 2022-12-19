package mx.kenzie.sloth;

import junit.framework.TestCase;

public class CacheTest extends TestCase {
    
    public void testWeak() {
    }
    
    public void testSoft() {
    }
    
    public void testIsPresent() {
        final Cache<String, Object> cache = Cache.weak();
        cache.put("hello", "there");
        assert cache instanceof WeakCache<String, Object>;
        assert cache.isPresent("hello");
        assert !cache.isPresent("bean");
        assert cache.isPresent("hello", "there");
        assert !cache.isPresent("hello", "bean");
        assert !cache.isPresent("bean", "bean");
        cache.clear();
        assert !cache.isPresent("bean");
        assert !cache.isPresent("hello");
        assert !cache.isPresent("hello", "there");
        assert !cache.containsKey("hello");
        assert !cache.containsValue("there");
    }
    
}
