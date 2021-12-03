package kr.pe.greenthumb.service.post;

import kr.pe.greenthumb.dao.post.PostRepository;
import kr.pe.greenthumb.dao.user.UserRepository;
import kr.pe.greenthumb.domain.post.Post;
import kr.pe.greenthumb.domain.user.User;
import kr.pe.greenthumb.dto.post.PostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    @Autowired
    PostRepository postDao;
    @Autowired
    UserRepository userDao;

    public Post add(PostDTO.Create dto) {
        User user = userDao.findById(dto.getUserIdx()).
                orElseThrow(() -> new NullPointerException("This (number" + dto.getUserIdx() + ") user is not exist"));

        return postDao.save(postDao.save(dto.toEntity(user)));
    }

    public List<Post> getAll(String category) {
        return postDao.findPostByPostCategory(category);
    }

    public void update(Post post) {
        postDao.save(post);
    }

    public void delete(Long postIdx) {
        Post post = postDao.findById(postIdx).get();
        postDao.delete(post);
    }
}