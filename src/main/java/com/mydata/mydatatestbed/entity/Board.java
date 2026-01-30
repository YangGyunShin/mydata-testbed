package com.mydata.mydatatestbed.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(length = 500)
    private String attachmentPath;

    @Column(length = 200)
    private String attachmentName;

    private Long attachmentSize;

    @Builder
    private Board(Member member, String title, String content,
                  String attachmentPath, String attachmentName, Long attachmentSize) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.attachmentPath = attachmentPath;
        this.attachmentName = attachmentName;
        this.attachmentSize = attachmentSize;
        this.viewCount = 0;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void update(String title, String content,
                       String attachmentPath, String attachmentName, Long attachmentSize) {
        this.title = title;
        this.content = content;
        if (attachmentPath != null) {
            this.attachmentPath = attachmentPath;
            this.attachmentName = attachmentName;
            this.attachmentSize = attachmentSize;
        }
    }

    public void removeAttachment() {
        this.attachmentPath = null;
        this.attachmentName = null;
        this.attachmentSize = null;
    }

    public boolean isAuthor(Long memberId) {
        return this.member.getId().equals(memberId);
    }
}