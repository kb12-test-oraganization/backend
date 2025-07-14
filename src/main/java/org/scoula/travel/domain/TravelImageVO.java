package org.scoula.travel.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelImageVO {
    private Long no;           // 이미지 고유 번호
    private String filename;   // 파일명
    private Long travelNo;     // 연결된 여행지 번호
}
