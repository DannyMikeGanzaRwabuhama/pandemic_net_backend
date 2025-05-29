package com.app.pandemicnet.repository;

import com.app.pandemicnet.model.Entry;
import com.app.pandemicnet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {
    @Query("SELECT e FROM Entry e WHERE e.user.id = :userId AND e.timestamp >= :startTime")
    List<Entry> findByUserIdAndTimeRange(Long userId, LocalDateTime startTime);

    @Query("SELECT DISTINCT e.user FROM Entry e WHERE e.dataCentre.id = :dataCentreId " +
            "AND e.timestamp BETWEEN :startTime AND :endTime AND e.user.id != :excludeUserId")
    List<User> findAtRiskUsers(Long dataCentreId, LocalDateTime startTime, LocalDateTime endTime, Long excludeUserId);

    @Query("select e from Entry e where e.user.id = :userId and e.timestamp between :startTime and :endTime")
    List<Entry> findEntriesByUserIdAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("select e from Entry e where e.user.id = :user_id ")
    List<Entry> findEntriesByUserId(Long user_id);

}