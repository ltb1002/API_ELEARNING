package vn.anhtuan.demoAPI.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.Lesson;
import vn.anhtuan.demoAPI.POJO.LessonPOJO;
import vn.anhtuan.demoAPI.Repository.LessonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private LessonRepository lessonRepository;

    public List<LessonPOJO> searchLessons(String keyword, Long subjectId, Integer grade) {
        // Xử lý keyword cho tiếng Việt
        String processedKeyword = processVietnameseKeyword(keyword);

        // Sử dụng phương thức JPQL cho tìm kiếm tiếng Việt
        List<Lesson> lessons = lessonRepository.searchLessons(processedKeyword, subjectId, grade);

        // Chuyển đổi Entity sang POJO
        return lessons.stream()
                .map(this::convertToLessonPOJO)
                .collect(Collectors.toList());
    }

    private String processVietnameseKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        // Chuẩn hóa keyword: trim và thay thế nhiều khoảng trắng thành một
        return keyword.trim().replaceAll("\\s+", " ");
    }

    private LessonPOJO convertToLessonPOJO(Lesson lesson) {
        LessonPOJO pojo = new LessonPOJO();
        pojo.setId(lesson.getId());
        pojo.setTitle(lesson.getTitle());
        pojo.setVideoUrl(lesson.getVideoUrl());

        // Lấy thông tin chapter và subject từ quan hệ
        if (lesson.getChapter() != null) {
            pojo.setChapterName(lesson.getChapter().getTitle());

            if (lesson.getChapter().getSubject() != null) {
                pojo.setSubjectId(lesson.getChapter().getSubject().getId());
                pojo.setSubjectName(lesson.getChapter().getSubject().getName());
                pojo.setGrade(lesson.getChapter().getSubject().getGrade()); // Thêm grade vào POJO
            }
        }

        // Note: Không set contents để tránh dữ liệu quá nặng khi search
        // Chỉ load contents khi vào trang chi tiết lesson

        return pojo;
    }
}