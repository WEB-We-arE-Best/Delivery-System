package com.webest.store.store.domain.repository;

import com.webest.store.store.domain.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
