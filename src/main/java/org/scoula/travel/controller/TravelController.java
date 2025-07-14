package org.scoula.travel.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.common.pagination.Page;
import org.scoula.common.pagination.PageRequest;
import org.scoula.common.util.UploadFiles;
import org.scoula.travel.dto.TravelDTO;
import org.scoula.travel.dto.TravelImageDTO;
import org.scoula.travel.service.TravelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/travel")
@Api(
        tags = "여행지 관리",                    // 그룹 이름
        description = "여행지 조회 API",         // 상세 설명
        value = "TravelController"             // 컨트롤러 식별자
)
public class TravelController {
    private final TravelService service;

    /**
     * 여행지 목록 조회 API (페이징 처리)
     * GET: http://localhost:8080/api/travel?page=1&amount=10
     * @param pageRequest 페이징 요청 정보
     * @return ResponseEntity<Page>
     *         - 200 OK: 여행지 목록 조회 성공, 페이징 처리된 여행지 리스트 반환
     *         - 400 Bad Request: 잘못된 페이징 파라미터
     *         - 500 Internal Server Error: 서버 내부 오류
     */
    @ApiOperation(value = "여행지 목록 조회(Pagination)", notes = "페이징 처리된 여행지 목록을 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = Page.class),
            @ApiResponse(code = 400, message = "잘못된 요청입니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("")
    public ResponseEntity<Page> getTravels(
            @ApiParam(value = "페이지네이션 요청 객체", required = true)
            PageRequest pageRequest) {
        return ResponseEntity.ok(service.getPage(pageRequest));
    }

    /**
     * 특정 여행지 상세 조회 API
     * GET: http://localhost:8080/api/travel/{no}
     * @param no 여행지 번호(PK)
     * @return ResponseEntity<TravelDTO>
     *         - 200 OK: 여행지 조회 성공, 여행지 상세 정보 반환
     *         - 404 Not Found: 해당 번호의 여행지가 존재하지 않음
     *         - 400 Bad Request: 잘못된 여행지 번호 형식
     *         - 500 Internal Server Error: 서버 내부 오류
     */
    @ApiOperation(value = "여행지 상세 조회", notes = "특정 여행지의 상세 정보를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = TravelDTO.class),
            @ApiResponse(code = 400, message = "잘못된 요청입니다."),
            @ApiResponse(code = 404, message = "여행지를 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("/{no}")
    public ResponseEntity<TravelDTO> getTravels(
            @ApiParam(value = "여행지 ID", required = true, example = "1")
            @PathVariable("no") Long no) {
        return ResponseEntity.ok(service.get(no));
    }


    /**
     * 여행지 이미지 파일 제공 API
     * GET: http://localhost:8080/api/travel/image/{no}
     * @param no 이미지 ID
     * @param response HTTP 응답 객체
     */
    @ApiOperation(value = "여행지 이미지 조회", notes = "여행지의 이미지 파일을 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "이미지 조회 성공"),
            @ApiResponse(code = 404, message = "이미지를 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("/image/{no}")
    public void viewImage(
            @ApiParam(value = "이미지 ID", required = true, example = "1")
            @PathVariable Long no, 
            HttpServletResponse response) {
        TravelImageDTO image = service.getImage(no);
        File file = new File(image.getPath());
        UploadFiles.downloadImage(response, file);
    }


}
