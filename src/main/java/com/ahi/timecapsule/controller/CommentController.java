package com.ahi.timecapsule.controller;

import com.ahi.timecapsule.dto.CommentDTO;
import com.ahi.timecapsule.dto.UserDTO;
import com.ahi.timecapsule.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByStoryId(@PathVariable Long storyId) {
        return ResponseEntity.ok(commentService.getCommentsByStoryId(storyId));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @RequestBody CommentDTO commentDto,
            Authentication authentication) {
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        commentDto.setUserId(userDTO.getId());
        CommentDTO createdComment = commentService.createComment(commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDTO commentDto,
            Authentication authentication) {
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDto, userDTO.getId());
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        commentService.deleteComment(commentId, userDTO.getId());
        return ResponseEntity.noContent().build();
    }
}