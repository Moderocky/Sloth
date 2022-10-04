package mx.kenzie.sloth;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class LazyTest {
    
    @Test
    public void test() {
        final Lazy<Object> lazy = new Lazy<>();
        assert !lazy.isReady();
        lazy.finish();
        assert lazy.isReady();
        assert lazy.await() == null;
        lazy.finish(new Object());
        assert lazy.await() != null;
    }
    
    @Test
    public void extended() {
        final Thing thing = new Thing();
        assert !thing.isReady();
        thing.target = thing;
        assert !thing.isReady();
        thing.finish();
        assert thing.isReady();
        assert thing.await() == thing;
    }
    
    @Test
    public void threaded() {
        final Thing thing = new Thing();
        assert !thing.isReady();
        final AtomicInteger state = new AtomicInteger(0);
        CompletableFuture.runAsync(() -> {
            state.set(1);
            thing.finish(thing);
        });
        thing.await();
        assert state.get() == 1;
        state.set(2);
    }
    
    private static class Thing extends Lazy<Thing> {
    }
    
}
