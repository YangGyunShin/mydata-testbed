package com.mydata.mydatatestbed.entity;

import com.mydata.mydatatestbed.entity.Enum.InquiryStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 문의 작성자 (회원)
     *
     * 관계: Inquiry(N) : Member(1) - 다대일 관계
     * - 한 회원은 여러 개의 문의를 작성할 수 있음
     * - 하나의 문의는 반드시 한 명의 작성자를 가짐
     *
     * fetch = LAZY: 문의 조회 시 회원 정보는 실제 접근할 때만 로딩 (N+1 방지)
     * nullable = false: 문의는 반드시 작성자가 있어야 함
     *
     * DB 컬럼: member_id (FK -> members.id)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    private LocalDateTime answeredAt;

    /**
     * 답변 작성자 (관리자)
     *
     * 관계: Inquiry(N) : Member(1) - 다대일 관계
     * - 한 관리자는 여러 문의에 답변할 수 있음
     * - 하나의 문의는 최대 한 명의 답변자를 가짐 (답변 전에는 null)
     *
     * fetch = LAZY: 문의 조회 시 답변자 정보는 실제 접근할 때만 로딩
     * nullable = true (기본값): 아직 답변되지 않은 문의는 answerer가 null
     *
     * DB 컬럼: answerer_id (FK -> members.id, nullable)
     *
     * 참고: member와 answerer 모두 Member 엔티티를 참조하지만
     *       서로 다른 역할(작성자 vs 답변자)을 구분하기 위해 별도 필드로 관리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answerer_id")
    private Member answerer;

    @Builder
    public Inquiry(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.status = InquiryStatus.WAITING;
    }

    /**
     * 답변 등록
     * - 답변 내용, 답변자, 상태(COMPLETED), 답변일시를 한 번에 설정
     */
    public void addAnswer(String answer, Member answerer) {
        this.answer = answer;
        this.answerer = answerer;
        this.status = InquiryStatus.COMPLETED;
        this.answeredAt = LocalDateTime.now();
    }
}