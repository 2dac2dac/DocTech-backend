package dac2dac.doctect.user.repository;

import dac2dac.doctect.user.entity.User;
import io.github.classgraph.AnnotationInfoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    User findByUsername(String username);
    User findByEmail(String email);
}
