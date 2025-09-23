package vn.anhtuan.demoAPI.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.anhtuan.demoAPI.POJO.LessonPOJO;
import vn.anhtuan.demoAPI.Service.SearchService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * API tìm kiếm lessons với keyword, subjectId và grade (optional)
     *
     * @param keyword Từ khóa tìm kiếm (tìm trong title của lesson)
     * @param subjectId ID của subject để lọc (optional)
     * @param grade Lớp để lọc (optional)
     * @return Danh sách lessons phù hợp
     */
    @GetMapping("/lessons")
    public ResponseEntity<?> searchLessons(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer grade) {

        try {
            List<LessonPOJO> results = searchService.searchLessons(keyword, subjectId, grade);

            // Tạo response structure chuẩn
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("total", results.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi tìm kiếm lessons: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * API tìm kiếm nâng cao với phân trang (optional - cho phiên bản sau)
     */
    @GetMapping("/lessons/advanced")
    public ResponseEntity<?> searchLessonsAdvanced(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer grade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // Hiện tại chỉ trả về tất cả, có thể phát triển phân trang sau
            List<LessonPOJO> results = searchService.searchLessons(keyword, subjectId, grade);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("total", results.size());
            response.put("page", page);
            response.put("size", size);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi tìm kiếm lessons: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}