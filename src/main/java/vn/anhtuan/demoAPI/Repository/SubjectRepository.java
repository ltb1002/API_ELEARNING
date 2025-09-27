package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.anhtuan.demoAPI.Entity.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    List<Subject> findByCodeAndGrade(String code, int grade);

    // Thêm method mới
    List<Subject> findByGrade(int grade);

}
