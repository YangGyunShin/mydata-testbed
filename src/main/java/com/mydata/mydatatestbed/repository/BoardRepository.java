package com.mydata.mydatatestbed.repository;

import com.mydata.mydatatestbed.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword% ORDER BY b.createdAt DESC")
    Page<Board> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Board> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT b FROM Board b JOIN FETCH b.member WHERE b.id = :id")
    Optional<Board> findByIdWithMember(@Param("id") Long id);

    Page<Board> findByTitleContainingOrderByCreatedAtDesc(String title, Pageable pageable);

    @Query("SELECT b FROM Board b JOIN b.member m WHERE m.name LIKE %:name% ORDER BY b.createdAt DESC")
    Page<Board> findByMemberNameContaining(@Param("name") String name, Pageable pageable);
}