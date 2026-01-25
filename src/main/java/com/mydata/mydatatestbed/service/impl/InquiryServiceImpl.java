package com.mydata.mydatatestbed.service.impl;

import com.mydata.mydatatestbed.dto.inquiry.InquiryListResponseDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryRequestDto;
import com.mydata.mydatatestbed.dto.inquiry.InquiryResponseDto;
import com.mydata.mydatatestbed.entity.Inquiry;
import com.mydata.mydatatestbed.entity.Member;
import com.mydata.mydatatestbed.mapper.InquiryMapper;
import com.mydata.mydatatestbed.repository.InquiryRepository;
import com.mydata.mydatatestbed.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMapper inquiryMapper;

    @Override
    @Transactional
    public Long createInquiry(Member member, InquiryRequestDto requestDto) {
        Inquiry inquiry = inquiryMapper.toEntity(requestDto, member);
        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        return savedInquiry.getId();
    }

    @Override
    public Page<InquiryListResponseDto> getMyInquiries(Member member, Pageable pageable) {
        return inquiryRepository.findByMemberOrderByCreatedAtDesc(member, pageable)
                .map(inquiryMapper::toListResponseDto);
    }

    @Override
    public InquiryResponseDto getInquiryDetail(Long id, Member member) {
        Inquiry inquiry = inquiryRepository.findByIdWithMemberAndAnswerer(id)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다. id=" + id));

        // 본인 문의인지 확인
        if (!inquiry.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        return inquiryMapper.toResponseDto(inquiry);
    }

    @Override
    public long countMyInquiries(Member member) {
        return inquiryRepository.countByMember(member);
    }
}