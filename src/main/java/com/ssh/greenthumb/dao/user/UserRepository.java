package com.ssh.greenthumb.dao.user;

import com.ssh.greenthumb.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserNameAndIsDeleted(String userName, String isDeleted);

    List<User> findAllByIsDeleted(String isDeleted);

    Boolean existsByUserName(String userName);

}