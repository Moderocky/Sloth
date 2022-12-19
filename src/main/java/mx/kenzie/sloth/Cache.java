package mx.kenzie.sloth;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;

public interface Cache<Key, Value> extends Map<Key, Value> {
    
    static <Key, Value> Cache<Key, Value> weak() {
        return new WeakCache<>();
    }
    
    static <Key, Value> Cache<Key, Value> soft() {
        return new SoftCache<>();
    }
    
    boolean isPresent(Key key);
    
    boolean isPresent(Key key, Value value);
    
}

class WeakCache<Key, Value> implements Cache<Key, Value> {
    
    protected final Map<Key, WeakReference<Value>> map = new WeakHashMap<>();
    
    @Override
    public boolean isPresent(Key key) {
        final WeakReference<Value> reference = map.get(key);
        if (reference == null) return false;
        return reference.get() != null;
    }
    
    @Override
    public boolean isPresent(Key key, Value value) {
        final WeakReference<Value> reference = map.get(key);
        if (reference == null) return false;
        return reference.get() == value;
    }
    
    @Override
    public int size() {
        return map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }
    
    @Override
    @SuppressWarnings("all")
    public boolean containsValue(Object value) {
        return this.values().contains((Value) value);
    }
    
    @Override
    public Value get(Object key) {
        final WeakReference<Value> reference = map.get(key);
        if (reference == null) return null;
        return reference.get();
    }
    
    @Override
    public Value put(Key key, Value value) {
        final WeakReference<Value> reference = this.map.put(key, new WeakReference<>(value));
        if (reference == null) return null;
        return reference.get();
    }
    
    @Override
    public Value remove(Object key) {
        final WeakReference<Value> reference = map.remove(key);
        if (reference == null) return null;
        return reference.get();
    }
    
    @Override
    public void putAll(Map<? extends Key, ? extends Value> m) {
        for (Entry<? extends Key, ? extends Value> entry : m.entrySet()) {
            this.map.put(entry.getKey(), new WeakReference<>(entry.getValue()));
        }
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public Set<Key> keySet() {
        return map.keySet();
    }
    
    @Override
    public Collection<Value> values() {
        final List<Value> list = new ArrayList<>(map.size());
        for (WeakReference<Value> reference : map.values()) {
            final Value value = reference.get();
            if (value == null) continue;
            list.add(value);
        }
        return list;
    }
    
    @Override
    public Set<Entry<Key, Value>> entrySet() {
        final Set<Entry<Key, Value>> set = new HashSet<>(map.size());
        for (Entry<Key, WeakReference<Value>> entry : map.entrySet()) {
            final Value value = entry.getValue().get();
            if (value == null) continue;
            set.add(new WeakEntry<>(entry.getKey(), value));
        }
        return set;
    }
    
    record WeakEntry<Key, Value>(Key getKey, Value getValue) implements Entry<Key, Value> {
        @Override
        public Value setValue(Value value) {
            return getValue;
        }
    }
}

class SoftCache<Key, Value> implements Cache<Key, Value> {
    
    protected final Map<Key, SoftReference<Value>> map = new WeakHashMap<>();
    
    @Override
    public boolean isPresent(Key key) {
        final SoftReference<Value> reference = map.get(key);
        if (reference == null) return false;
        return reference.get() != null;
    }
    
    @Override
    public boolean isPresent(Key key, Value value) {
        final SoftReference<Value> reference = map.get(key);
        if (reference == null) return false;
        return reference.get() == value;
    }
    
    @Override
    public int size() {
        return map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }
    
    @Override
    @SuppressWarnings("all")
    public boolean containsValue(Object value) {
        return this.values().contains((Value) value);
    }
    
    @Override
    public Value get(Object key) {
        final SoftReference<Value> reference = map.get(key);
        if (reference == null) return null;
        return reference.get();
    }
    
    @Override
    public Value put(Key key, Value value) {
        final SoftReference<Value> reference = this.map.put(key, new SoftReference<>(value));
        if (reference == null) return null;
        return reference.get();
    }
    
    @Override
    public Value remove(Object key) {
        final SoftReference<Value> reference = map.remove(key);
        if (reference == null) return null;
        return reference.get();
    }
    
    @Override
    public void putAll(Map<? extends Key, ? extends Value> m) {
        for (Entry<? extends Key, ? extends Value> entry : m.entrySet()) {
            this.map.put(entry.getKey(), new SoftReference<>(entry.getValue()));
        }
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public Set<Key> keySet() {
        return map.keySet();
    }
    
    @Override
    public Collection<Value> values() {
        final List<Value> list = new ArrayList<>(map.size());
        for (SoftReference<Value> reference : map.values()) {
            final Value value = reference.get();
            if (value == null) continue;
            list.add(value);
        }
        return list;
    }
    
    @Override
    public Set<Entry<Key, Value>> entrySet() {
        final Set<Entry<Key, Value>> set = new HashSet<>(map.size());
        for (Entry<Key, SoftReference<Value>> entry : map.entrySet()) {
            final Value value = entry.getValue().get();
            if (value == null) continue;
            set.add(new SoftEntry<>(entry.getKey(), value));
        }
        return set;
    }
    
    record SoftEntry<Key, Value>(Key getKey, Value getValue) implements Entry<Key, Value> {
        @Override
        public Value setValue(Value value) {
            return getValue;
        }
    }
}
