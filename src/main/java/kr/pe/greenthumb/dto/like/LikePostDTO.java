package kr.pe.greenthumb.dto.like;

import kr.pe.greenthumb.domain.like.LikePost;
import kr.pe.greenthumb.domain.post.Post;
import kr.pe.greenthumb.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class LikePostDTO {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private Long postId;
        private Long userId;

        public LikePost toEntity(Post post, User user) {
            return LikePost.builder()
                    .post(post)
                    .user(user)
                    .build();
        }
    }

    @Getter
    public static class Delete {
        private Long postId;
        private Long userId;
    }

    @Getter
    public static class Get {
        private Long postId;
        private Long userId;

        public Get(LikePost entity) {
            this.postId = entity.getPost().getPostId();
            this.userId = entity.getUser().getUserId();
        }
    }

}