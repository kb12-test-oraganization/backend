package org.scoula.controller;

import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.scoula.security.account.domain.CustomUser;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/security")
@RestController
@Api(
        tags = "보안 테스트",                   // 그룹 이름
        description = "권한별 접근 테스트 API", // 상세 설명
        value = "SecurityController"          // 컨트롤러 식별자
)
public class SecurityController {

    /**
     * 모든 사용자 접근 가능 API (인증 불필요)
     * GET: http://localhost:8080/api/security/all
     * @return ResponseEntity<String>
     *         - 200 OK: 접근 성공, 메시지 반환
     */
    @ApiOperation(value = "전체 접근 테스트", notes = "모든 사용자가 접근 가능한 API (인증 불필요)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = String.class),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("/all")
    public ResponseEntity<String> doAll() {
        log.info("do all can access everybody");
        return ResponseEntity.ok("All can access everybody");
    }

    /**
     * ROLE_MEMBER 권한 필요 API
     * GET: http://localhost:8080/api/security/member
     * @param authentication 인증 정보
     * @return ResponseEntity<String>
     *         - 200 OK: 접근 성공, 사용자명 반환
     *         - 401 Unauthorized: 인증되지 않은 사용자
     *         - 403 Forbidden: MEMBER 권한이 없는 사용자
     */
    @ApiOperation(value = "회원 권한 테스트", notes = "ROLE_MEMBER 권한이 필요한 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = String.class),
            @ApiResponse(code = 401, message = "인증이 필요합니다."),
            @ApiResponse(code = 403, message = "권한이 없습니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("/member")
    public ResponseEntity<String> doMember(
            @ApiParam(value = "인증 정보", hidden = true)
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("username = " + userDetails.getUsername());
        return ResponseEntity.ok(userDetails.getUsername());
    }

    /**
     * ROLE_ADMIN 권한 필요 API
     * GET: http://localhost:8080/api/security/admin
     * @param customUser 커스텀 사용자 인증 정보
     * @return ResponseEntity<MemberVO>
     *         - 200 OK: 접근 성공, 관리자 정보 반환
     *         - 401 Unauthorized: 인증되지 않은 사용자
     *         - 403 Forbidden: ADMIN 권한이 없는 사용자
     */
    @ApiOperation(value = "관리자 권한 테스트", notes = "ROLE_ADMIN 권한이 필요한 API")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = MemberVO.class),
            @ApiResponse(code = 401, message = "인증이 필요합니다."),
            @ApiResponse(code = 403, message = "관리자 권한이 없습니다."),
            @ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
    })
    @GetMapping("/admin")
    public ResponseEntity<MemberVO> doAdmin(
            @ApiParam(value = "커스텀 사용자 인증 정보", hidden = true)
            @AuthenticationPrincipal CustomUser customUser
    ) {
        MemberVO member = customUser.getMember();
        log.info("username = " + member);
        return ResponseEntity.ok(member);
    }
}
