package vn.anhtuan.demoAPI.REST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.anhtuan.demoAPI.Service.DataLoaderQuizService;
import vn.anhtuan.demoAPI.Service.DataLoaderService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/data")
public class DataLoaderController {

    @Autowired
    private DataLoaderQuizService dataLoaderQuizService;

    @PostMapping("/reload")
    public ResponseEntity<Map<String, Object>> reloadData() {
        try {
            dataLoaderQuizService.reloadAllData();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Data reloaded successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error reloading data: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}
