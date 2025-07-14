package org.scoula.travel.service;

import org.scoula.common.pagination.Page;
import org.scoula.common.pagination.PageRequest;
import org.scoula.travel.dto.TravelDTO;
import org.scoula.travel.dto.TravelImageDTO;

import java.util.List;

public interface TravelService {
    Page<TravelDTO> getPage(PageRequest pageRequest);  // 페이징 처리된 목록
    List<TravelDTO> getList();                         // 전체 목록
    TravelDTO get(Long no);                            // 특정 여행지 상세
    TravelImageDTO getImage(Long no);                  // 이미지 정보 조회
}
