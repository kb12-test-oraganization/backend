package org.scoula.board.mapper;

import org.scoula.board.domain.BoardAttachmentVO;
import org.scoula.board.domain.BoardVO;
import org.scoula.common.pagination.PageRequest;

import java.util.List;

public interface BoardMapper {

    // === 게시글 CRUD ===

    //@Select("select * from tbl_board order by no desc")
    public List<BoardVO> getList();         // 게시글 목록 조회
    public BoardVO get(Long no);            // 단일 게시글 조회
    public void create(BoardVO board);      // 게시글 등록
    public int update(BoardVO board);       // 게시글 수정
    public int delete(Long no);             // 게시글 삭제

    // === 첨부파일 관리 ===
    public void createAttachment(BoardAttachmentVO attach);         // 첨부파일 등록
    public List<BoardAttachmentVO> getAttachmentList(Long bno);     // 특정 게시글의 첨부 파일 목록 조회
    public BoardAttachmentVO getAttachment(Long no);                // 특정 첨부 파일 1개 조회
    public int deleteAttachment(Long no);                           // 특정 첨부 파일 1개 삭제

    // === Pagination ===
    // 전체 게시글 수 조회
    int getTotalCount();

    // 페이징된 게시글 목록 조회
    List<BoardVO> getPage(PageRequest pageRequest);

    // === Search ===
    // 검색 조건을 포함한 전체 게시글 수 조회
    int getTotalCountWithSearch(PageRequest pageRequest);

    // 검색 조건을 포함한 페이징된 게시글 목록 조회
    List<BoardVO> getPageWithSearch(PageRequest pageRequest);
}
