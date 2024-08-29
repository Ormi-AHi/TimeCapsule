package com.ahi.timecapsule.service;

import com.ahi.timecapsule.dto.CommentDTO;
import com.ahi.timecapsule.entity.Comment;
import com.ahi.timecapsule.entity.Story;
import com.ahi.timecapsule.repository.CommentRepository;
import com.ahi.timecapsule.repository.StoryRepository;
import com.ahi.timecapsule.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, StoryRepository storyRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByStoryId(Long storyId) {
        List<Comment> comments = commentRepository.findByStoryId(storyId);
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());

        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        comment.setUser(user);

        Story story = storyRepository.findById(commentDTO.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid story ID"));
        comment.setStory(story);

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }


    @Transactional
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this comment");
        }

        comment.setContent(commentDTO.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return convertToDTO(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentDTO convertToDTO(Comment comment) {
        return CommentDTO.fromComment(comment);
    }
}