package com.mydata.mydatatestbed.repository;

import com.mydata.mydatatestbed.entity.Inquiry;
import com.mydata.mydatatestbed.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 특정 회원의 문의 목록 조회 (페이징)
    @Query("SELECT i FROM Inquiry i WHERE i.member = :member ORDER BY i.createdAt DESC")
    Page<Inquiry> findByMemberOrderByCreatedAtDesc(@Param("member") Member member, Pageable pageable);

    // 문의 상세 조회 (작성자, 답변자 함께 조회 - N+1 방지)
    @Query("SELECT i FROM Inquiry i " +
            "LEFT JOIN FETCH i.member " +
            "LEFT JOIN FETCH i.answerer " +
            "WHERE i.id = :id")
    Optional<Inquiry> findByIdWithMemberAndAnswerer(@Param("id") Long id);

    // 특정 회원의 문의 개수
    long countByMember(Member member);
}