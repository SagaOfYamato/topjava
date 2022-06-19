package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.AbstractBaseEntity;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class InMemoryBaseRepository<T extends AbstractBaseEntity> {
    private static final Logger log = LoggerFactory.getLogger(InMemoryBaseRepository.class);

    static final AtomicInteger counter = new AtomicInteger(START_SEQ);

    final Map<Integer, T> map = new ConcurrentHashMap<>();

    public InMemoryBaseRepository() {}

    public T save(T entity) {
        if (entity.isNew()) {
            entity.setId(counter.incrementAndGet());
            map.put(entity.getId(), entity);
            return entity;
        }
        return map.computeIfPresent(entity.getId(), (id, oldT) -> entity);
    }

    public boolean delete(int id) {
        log.info("InMemoryBaseRepository - delete");
        return map.remove(id) != null;
    }

    public T get(int id) {
        log.info("InMemoryBaseRepository - get");
        return map.get(id);
    }

    Collection<T> getCollection() {
        return map.values();
    }

    void put(T entity) {
        map.put(entity.getId(), entity);
    }
}