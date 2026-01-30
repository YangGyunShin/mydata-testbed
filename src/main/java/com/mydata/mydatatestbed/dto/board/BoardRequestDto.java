package com.mydata.mydatatestbed.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 게시글 작성/수정 요청 DTO
 * 
 * @Setter 필요 이유:
 * - Thymeleaf th:field 바인딩 시 Spring이 Setter를 통해 폼 데이터를 주입
 * - Request DTO는 외부에서 값을 받아오는 용도이므로 Setter 허용
 * - (Entity와 달리 DTO는 데이터 전달 객체이므로 Setter 사용 가능)
 */
@Getter
@Setter
@NoArgsConstructor
public class BoardRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @Builder
    private BoardRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
