package org.scoula.common.pagination;

import lombok.Data;

@Data
public class PageRequest {
    private int page;   // 요청 페이지 번호 (1부터 시작)
    private int amount; // 한 페이지당 데이터 건수
    private String type;      // 검색 타입 (title, content, writer, titleContent 등)
    private String keyword;   // 검색 키워드

    // 기본 생성자: 첫 페이지, 10개씩 표시
    public PageRequest() {
        page = 1;
        amount = 10;
    }

    // 페이지, 사이즈만 설정하는 생성자
    private PageRequest(int page, int amount) {
        this.page = page;
        this.amount = amount;
    }

    // 정적 팩토리 메서드
    public static PageRequest of(int page, int amount) {
        return new PageRequest(page, amount);
    }

    // MyBatis LIMIT 절에 사용할 offset 값 계산
    public int getOffset() {
        return (page - 1) * amount;
    }

    // 검색 조건 확인 메서드
    public boolean hasSearchCondition() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    // 제목 검색 여부
//    public boolean isTitle() {
//        return type != null && type.contains("T");
//    }
//
//    // 내용 검색 여부
//    public boolean isContent() {
//        return type != null && type.contains("C");
//    }
//
//    // 작성자 검색 여부
//    public boolean isWriter() {
//        return type != null && type.contains("W");
//    }
}
