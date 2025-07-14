package org.scoula.member.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.common.util.UploadFiles;
import org.scoula.member.dto.ChangePasswordDTO;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.dto.MemberJoinDTO;
import org.scoula.member.dto.MemberUpdateDTO;
import org.scoula.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Api(
        tags = "회원 관리",                    // 그룹 이름
        description = "회원 CRUD API",        // 상세 설명
        value = "MemberController"           // 컨트롤러 식별자
)
public class MemberController {
    final MemberService service;

    /**
     * ID 중복 체크 API
     * GET: http://localhost:8080/api/member/checkusername/{username}
     * @param username 중복 확인할 사용자 ID
     * @return ResponseEntity<Boolean>
     *         - 200 OK: 중복 체크 성공, true(중복됨) 또는 false(사용가능) 반환
     *         - 400 Bad Request: 잘못된 사용자명 형식
     *         - 500 Internal Server Error: 서버 내부 오류
     */
    @ApiOperation(value = "ID 중복 체크", notes = "사용자 ID의 중복 여부를 확인하는 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = Boolean.class),
            @ApiResponse(code = 400, message = "잘못된 요청입니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("/checkusername/{username}")
    public ResponseEntity<Boolean> checkUsername(
            @ApiParam(value = "중복 확인할 사용자 ID", required = true, example = "testuser")
            @PathVariable String username) {
        return ResponseEntity.ok().body(service.checkDuplicate(username));
    }

    /**
     * 회원가입 API
     * POST: http://localhost:8080/api/member
     * @param member 회원가입 정보 (아이디, 비밀번호, 이메일 등)
     * @return ResponseEntity<MemberDTO>
     *         - 200 OK: 회원가입 성공, 생성된 회원 정보 반환
     *         - 400 Bad Request: 잘못된 요청 데이터 (필수 필드 누락, 형식 오류 등)
     *         - 409 Conflict: 이미 존재하는 아이디 또는 이메일
     *         - 500 Internal Server Error: 서버 내부 오류
     */
    @ApiOperation(value = "회원가입", notes = "새로운 회원을 등록하는 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = MemberDTO.class),
            @ApiResponse(code = 400, message = "잘못된 요청입니다."),
            @ApiResponse(code = 409, message = "이미 존재하는 아이디 또는 이메일입니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @PostMapping("")
    public ResponseEntity<MemberDTO> join(
            @ApiParam(value = "회원가입 정보", required = true)
            MemberJoinDTO member) {
        return ResponseEntity.ok(service.join(member));
    }

    /**
     * 아바타 이미지 요청 처리 API
     * GET: http://localhost:8080/api/member/{username}/avatar
     * @param username 사용자 ID
     * @param response HTTP 응답 객체
     */
    @ApiOperation(value = "아바타 이미지 조회", notes = "사용자의 아바타 이미지를 조회하는 API (없을 경우 기본 이미지 반환)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "아바타 이미지 조회 성공"),
            @ApiResponse(code = 404, message = "사용자를 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("/{username}/avatar")
    public void getAvatar(
            @ApiParam(value = "사용자 ID", required = true, example = "testuser")
            @PathVariable String username, 
            HttpServletResponse response) {
        String avatarPath = "c:/upload/avatar/" + username + ".png";
        File file = new File(avatarPath);

        if(!file.exists()) {
            // 아바타가 없는 경우 기본 이미지 사용
            file = new File("C:/upload/avatar/unknown.png");
        }

        UploadFiles.downloadImage(response, file);
    }

    /**
     * 회원 정보 수정 API
     * PUT: http://localhost:8080/api/member/{username}
     * @param username 수정할 사용자 ID
     * @param member 수정할 회원 정보
     * @return ResponseEntity<MemberDTO>
     *         - 200 OK: 회원정보 수정 성공, 수정된 회원 정보 반환
     *         - 400 Bad Request: 잘못된 요청 데이터
     *         - 403 Forbidden: 다른 사용자의 정보 수정 시도
     *         - 404 Not Found: 존재하지 않는 사용자
     *         - 500 Internal Server Error: 서버 내부 오류
     */
    @ApiOperation(value = "회원정보 수정", notes = "기존 회원의 정보를 수정하는 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = MemberDTO.class),
            @ApiResponse(code = 400, message = "잘못된 요청입니다."),
            @ApiResponse(code = 403, message = "권한이 없습니다."),
            @ApiResponse(code = 404, message = "사용자를 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @PutMapping("/{username}") // PUT 메서드 : 기존 리소스의 완전한 업데이트를 의미
    public ResponseEntity<MemberDTO> changeProfile(
            @ApiParam(value = "사용자 ID", required = true, example = "testuser")
            @PathVariable String username,
            @ApiParam(value = "수정할 회원 정보", required = true)
            MemberUpdateDTO member) {
        return ResponseEntity.ok(service.update(member));
    }

    /**
     * 비밀번호 변경 API
     * PUT: http://localhost:8080/api/member/{username}/changepassword
     * @param username 사용자 ID
     * @param changePasswordDTO 비밀번호 변경 정보 (기존 비밀번호, 새 비밀번호)
     * @return ResponseEntity<?>
     *         - 200 OK: 비밀번호 변경 성공
     *         - 400 Bad Request: 잘못된 요청 데이터 (기존 비밀번호 불일치 등)
     *         - 403 Forbidden: 다른 사용자의 비밀번호 변경 시도
     *         - 404 Not Found: 존재하지 않는 사용자
     *         - 500 Internal Server Error: 서버 내부 오류
     */
    @ApiOperation(value = "비밀번호 변경", notes = "사용자의 비밀번호를 변경하는 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "비밀번호가 성공적으로 변경되었습니다."),
            @ApiResponse(code = 400, message = "잘못된 요청입니다. (기존 비밀번호 불일치 등)"),
            @ApiResponse(code = 403, message = "권한이 없습니다."),
            @ApiResponse(code = 404, message = "사용자를 찾을 수 없습니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @PutMapping("/{username}/changepassword")
    public ResponseEntity<?> changePassword(
            @ApiParam(value = "사용자 ID", required = true, example = "testuser")
            @PathVariable String username,
            @ApiParam(value = "비밀번호 변경 정보", required = true)
            @RequestBody ChangePasswordDTO changePasswordDTO) {
        service.changePassword(changePasswordDTO);
        return ResponseEntity.ok().build();
    }
}
