package org.scoula.travel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.common.pagination.Page;
import org.scoula.common.pagination.PageRequest;
import org.scoula.travel.domain.TravelImageVO;
import org.scoula.travel.domain.TravelVO;
import org.scoula.travel.dto.TravelDTO;
import org.scoula.travel.dto.TravelImageDTO;
import org.scoula.travel.mapper.TravelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Slf4j
@Service
@RequiredArgsConstructor
public class TravelServiceImpl implements TravelService {

    private final TravelMapper travelMapper;

    // 페이징 처리된 목록
    @Override
    public Page<TravelDTO> getPage(PageRequest pageRequest) {
        // 1. 기본 여행지 정보 조회 (페이징 적용)
        List<TravelDTO> travels = travelMapper.getPage(pageRequest)
                .stream().map(TravelDTO::of).toList();

        // 2. 각 여행지별 이미지 정보 별도 조회
        travels.forEach(travel -> {
            List<TravelImageDTO> images = travelMapper.getImages(travel.getNo())
                    .stream().map(TravelImageDTO::of).toList();
            travel.setImages(images);
        });

        // 3. 전체 개수 조회 및 Page 객체 생성
        int totalCount = travelMapper.getTotalCount();
        return Page.of(pageRequest, totalCount, travels);
    }


    // 전체 목록
    @Override
    public List<TravelDTO> getList() {
        List<TravelVO> travels = travelMapper.getTravels();
        return travels.stream().map(TravelDTO::of).toList();
    }

    // 특정 여행지 상세
    @Override
    public TravelDTO get(Long no) {
        TravelVO travel = travelMapper.getTravel(no);
        if (travel == null) {
            throw new NoSuchElementException();
        }
        return TravelDTO.of(travel);
    }

    // 이미지 정보 조회
    @Override
    public TravelImageDTO getImage(Long no) {
        TravelImageVO image = travelMapper.getImage(no);
        return TravelImageDTO.of(image);
    }

}
