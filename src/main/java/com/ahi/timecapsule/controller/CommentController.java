package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.CommentDTO;
import com.ahi.timecapsule.dto.UserDTO;
import com.ahi.timecapsule.service.CommentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
public class CommentController {
  private final CommentService commentService;

  // CommentService를 주입받는 생성자
  @Autowired
  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  // 특정 스토리의 댓글 목록을 조회
  @GetMapping
  public String getComments(@RequestParam int storyId, Model model) {
    List<CommentDTO> comments = commentService.getCommentsByStoryId(storyId);
    model.addAttribute("comments", comments);
    model.addAttribute("storyId", storyId);
    return "comment-list";
  }

  // 새로운 댓글을 생성하는 메서드
  @PostMapping
  public String createComment(
      @ModelAttribute CommentDTO commentDto,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {
    if (authentication == null || !(authentication.getPrincipal() instanceof UserDTO)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
    }
    UserDTO userDTO = (UserDTO) authentication.getPrincipal();
    commentDto.setUserId(userDTO.getUserId());
    CommentDTO createdComment = commentService.createComment(commentDto);
    redirectAttributes.addFlashAttribute("message", "댓글이 성공적으로 작성되었습니다.");
    return "redirect:/stories/" + commentDto.getStoryId();
  }

  // 댓글 수정 매서드
  @PostMapping("/{commentId}")
  public String updateComment(
      @PathVariable Long commentId,
      @ModelAttribute CommentDTO commentDto,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {
    if (authentication == null || !(authentication.getPrincipal() instanceof UserDTO)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
    }
    UserDTO userDTO = (UserDTO) authentication.getPrincipal();
    CommentDTO updatedComment =
        commentService.updateComment(commentId, commentDto, userDTO.getUserId());
    redirectAttributes.addFlashAttribute("message", "댓글이 성공적으로 수정되었습니다.");
    return "redirect:/stories/" + updatedComment.getStoryId();
  }

  // 댓글 삭제 매서드
  @PostMapping("/{commentId}/delete")
  public String deleteComment(
      @PathVariable Long commentId,
      @RequestParam int storyId,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {
    if (authentication == null || !(authentication.getPrincipal() instanceof UserDTO)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
    }
    UserDTO userDTO = (UserDTO) authentication.getPrincipal();
    commentService.deleteComment(commentId, userDTO.getUserId());
    redirectAttributes.addFlashAttribute("message", "댓글이 성공적으로 삭제되었습니다.");
    return "redirect:/stories/" + storyId;
  }
}
