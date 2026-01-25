package com.mydata.mydatatestbed.service;

import com.mydata.mydatatestbed.dto.inquiry.InquiryListResponseDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryRequestDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryResponseDto;
import com.mydata.mydatatestbed.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {

    // 문의 등록
    Long createInquiry(Member member, InquiryRequestDto requestDto);

    // 내 문의 목록 조회
    Page<InquiryListResponseDto> getMyInquiries(Member member, Pageable pageable);

    // 문의 상세 조회
    InquiryResponseDto getInquiryDetail(Long id, Member member);

    // 내 문의 개수
    long countMyInquiries(Member member);
}