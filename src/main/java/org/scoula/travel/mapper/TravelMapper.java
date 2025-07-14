package org.scoula.travel.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.common.pagination.PageRequest;
import org.scoula.travel.domain.TravelImageVO;
import org.scoula.travel.domain.TravelVO;

import java.util.List;


public interface TravelMapper {
    int getTotalCount();                                   // 전체 여행지 수
    List<String> getDistricts();                           // 권역 목록 조회
    List<TravelVO> getTravels();                           // 전체 여행지 목록
    List<TravelVO> getPage(PageRequest pageRequest);       // 페이징 처리된 목록
    List<TravelVO> getTravelsByDistrict(String district);  // 권역별 여행지 목록
    TravelVO getTravel(Long no);                           // 특정 여행지 상세 정보
    List<TravelImageVO> getImages(Long travelNo);          // 특정 여행지 이미지 목록
    TravelImageVO getImage(Long no);                       // 특정 이미지 정보
}
