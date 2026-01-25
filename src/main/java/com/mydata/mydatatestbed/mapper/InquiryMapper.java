package com.mydata.mydatatestbed.mapper;

import com.mydata.mydatatestbed.dto.inquiry.InquiryListResponseDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryRequestDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryResponseDto;
import com.mydata.mydatatestbed.entity.Inquiry;
import com.mydata.mydatatestbed.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class InquiryMapper {

    public Inquiry toEntity(InquiryRequestDto dto, Member member) {
        return Inquiry.builder()
                .member(member)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
    }

    public InquiryResponseDto toResponseDto(Inquiry inquiry) {
        return InquiryResponseDto.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .memberName(inquiry.getMember().getName())
                .memberEmail(inquiry.getMember().getEmail().getValue())
                .status(inquiry.getStatus())
                .statusDisplayName(inquiry.getStatus().getDisplayName())
                .answer(inquiry.getAnswer())
                .answererName(inquiry.getAnswerer() != null ? inquiry.getAnswerer().getName() : null)
                .answeredAt(inquiry.getAnsweredAt())
                .createdAt(inquiry.getCreatedAt())
                .updatedAt(inquiry.getUpdatedAt())
                .build();
    }

    public InquiryListResponseDto toListResponseDto(Inquiry inquiry) {
        return InquiryListResponseDto.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .status(inquiry.getStatus())
                .statusDisplayName(inquiry.getStatus().getDisplayName())
                .createdAt(inquiry.getCreatedAt())
                .answeredAt(inquiry.getAnsweredAt())
                .build();
    }
}