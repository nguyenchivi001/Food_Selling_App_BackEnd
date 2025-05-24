package com.nt118.foodsellingapp.repository;


import com.nt118.foodsellingapp.entity.Food;
import com.nt118.foodsellingapp.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByStatus(String status);
    List<Order> findByUserId(int userId);
    Page<Order> findByUserIdAndStatus(Integer userId, String status, Pageable pageable);
    Page<Order> findAllByUserId(Integer userId, Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.user.name LIKE %:name%")
    Page<Order> findByUserNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.name LIKE %:name% AND o.status = :status")
    Page<Order> findByUserNameContainingAndStatus(@Param("name") String name,
                                                  @Param("status") String status,
                                                  Pageable pageable);
}
