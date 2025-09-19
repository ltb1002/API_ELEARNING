package vn.anhtuan.demoAPI.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.anhtuan.demoAPI.Entity.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
}
