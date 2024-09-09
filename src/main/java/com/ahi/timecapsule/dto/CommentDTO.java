package com.ahi.timecapsule.dto;

import com.ahi.timecapsule.entity.Comment;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
  private Long id;
  private String content;
  private String userId;
  private String authorNickname;
  private int storyId;  // Integer에서 int로 변경
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Entity -> DTO
  public static CommentDTO fromComment(Comment comment) {
    return CommentDTO.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .userId(comment.getUser().getId())
            .authorNickname(comment.getUser().getNickname())
            .storyId(comment.getStory().getId())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .build();
  }

  // DTO -> Entity
  public Comment toEntity(User user, Story story) {
    return Comment.builder()
            .id(this.id)
            .content(this.content)
            .user(user)
            .story(story)
            .createdAt(this.createdAt)
            .updatedAt(this.updatedAt)
            .build();
  }
}