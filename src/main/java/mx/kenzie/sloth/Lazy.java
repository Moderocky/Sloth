package mx.kenzie.sloth;

import java.util.function.Supplier;

/**
 * A 'lazy' object.
 * This is designed to be resolved (finished) at a future time by a different thread.
 * The {@link #await()} methods will pause the current thread until the resource is resolved.
 * <p>
 * This class is designed either to be extended by a 'lazy' object, or to be used as a 'holder'
 * similar to a future.
 */
public class Lazy<Type> implements Supplier<Type> {
    
    protected final Object lock = new Object();
    protected Type target;
    
    /**
     * This is volatile since it incurs a lower computation cost than arbitrating access via the kernel.
     */
    protected volatile boolean ready;
    
    public Lazy() {
    }
    
    /**
     * Creates an immediately-ready form of the lazy.
     */
    public static <Type> Lazy<Type> instant(Type thing) {
        return new InstantLazy<>(thing);
    }
    
    /**
     * Waits for the resolution of the resource, up to the given amount of time.
     * If the process is interrupted, this will attempt to requeue based on the system clock.
     */
    public Type await(long millis) {
        this.await0(millis);
        synchronized (this) {
            return target;
        }
    }
    
    private void await0(long millis) {
        final long end = System.currentTimeMillis() + millis;
        boolean run = false;
        while (!this.isReady()) {
            try {
                long timer = run ? end - System.currentTimeMillis() : millis;
                run = true;
                synchronized (lock) {
                    this.lock.wait(timer);
                }
                break;
            } catch (InterruptedException ignore) {
            }
        }
    }
    
    /**
     * Whether the resource is marked 'finished' (resolved.)
     */
    public boolean isReady() {
        return ready;
    }
    
    /**
     * Waits for the resolution of the resource, up to the given amount of time.
     * If the process is interrupted, this will attempt to requeue based on the system clock.
     * If the resource is not ready this will return the alternative.
     */
    public Type await(long millis, Type alternative) {
        this.await0(millis);
        if (ready) synchronized (this) {
            return target;
        }
        else return alternative;
    }
    
    @Override
    public synchronized Type get() {
        return this.await();
    }
    
    /**
     * Waits for the resolution of the resource, by any means necessary.
     */
    public Type await() {
        while (!ready) {
            try {
                synchronized (lock) {
                    this.lock.wait();
                }
            } catch (InterruptedException ignore) {
            }
        }
        synchronized (this) {
            return target;
        }
    }
    
    /**
     * Mark this resource as finished with the provided result.
     * Notify all watchers.
     */
    public void finish(Type result) {
        synchronized (this) {
            this.target = result;
            this.ready = true;
        }
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }
    
    /**
     * Mark this resource as finished without a result.
     * Notify all watchers.
     */
    public void finish() {
        this.ready = true;
        synchronized (lock) {
            this.lock.notifyAll();
        }
    }
    
}

/**
 * A pre-finished resource.
 */
class InstantLazy<Type> extends Lazy<Type> {
    
    public InstantLazy(Type thing) {
        this.target = thing;
    }
    
    @Override
    public Type await() {
        return target;
    }
    
    @Override
    public boolean isReady() {
        return true;
    }
    
    @Override
    public Type await(long millis) {
        return target;
    }
    
    @Override
    public Type await(long millis, Type alternative) {
        return target;
    }
    
    @Override
    public void finish(Type result) {
    }
}
