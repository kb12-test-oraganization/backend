package org.scoula.travel.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 여행지 정보 VO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelVO {
    private Long no;                      // 여행지 고유 번호
    private String district;              // 권역 (강릉권, 영월권 등)
    private String title;                 // 여행지 명
    private String description;           // 설명
    private String address;               // 주소
    private String phone;                 // 연락처
    private List<TravelImageVO> images;   // 연결된 이미지 목록
}
