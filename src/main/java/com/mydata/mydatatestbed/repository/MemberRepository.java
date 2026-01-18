package com.mydata.mydatatestbed.repository;

import com.mydata.mydatatestbed.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email.value = :email")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.email.value = :email")
    boolean existsByEmail(@Param("email") String email);
}