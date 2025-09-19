package vn.anhtuan.demoAPI.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.*;
import vn.anhtuan.demoAPI.POJO.ChoicePOJO;
import vn.anhtuan.demoAPI.POJO.QuestionPOJO;
import vn.anhtuan.demoAPI.POJO.QuizPOJO;
import vn.anhtuan.demoAPI.Repository.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DataLoaderQuizService {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderQuizService.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private QuizService quizService;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private QuizTypeRepository quizTypeRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        loadInitialData();
    }

    public void loadInitialData() {
        try {
            loadQuizzes();
            logger.info("Initial quiz data loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading initial quiz data: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void loadQuizzes() {
        String[] subjects = {"toan"};
        int[] grades = {6};

        for (String subject : subjects) {
            for (int grade : grades) {
                String fileName = "quiz_" + getFileName(subject, grade);
                Resource resource = resourceLoader.getResource("classpath:data/" + fileName);

                if (!resource.exists()) {
                    logger.warn("⚠️ File {} not found, skipping", fileName);
                    continue;
                }

                logger.info("🔄 Loading quizzes from file: {}", fileName);

                try (InputStream inputStream = resource.getInputStream()) {
                    List<QuizPOJO> quizPOJOS = objectMapper.readValue(
                            inputStream, new TypeReference<List<QuizPOJO>>() {});

                    for (QuizPOJO quizPOJO : quizPOJOS) {
                        try {
                            Quiz existingQuiz = quizService.getQuizByCode(quizPOJO.getCode());
                            if (existingQuiz != null) {
                                logger.info("Quiz with code {} already exists, skipping", quizPOJO.getCode());
                                continue;
                            }

                            // Get related entities
                            Grade gradeEntity = gradeRepository.findById(quizPOJO.getGradeId()).orElse(null);
                            Subject subjectEntity = subjectRepository.findById(quizPOJO.getSubjectId()).orElse(null);
                            Chapter chapterEntity = chapterRepository.findById(quizPOJO.getChapterId()).orElse(null);
                            QuizType quizType = quizTypeRepository.findById(quizPOJO.getQuizTypeId()).orElse(null);

                            if (gradeEntity == null || subjectEntity == null || quizType == null || chapterEntity == null) {
                                logger.warn("⚠️ Invalid grade/subject/chapter/quizType for quiz code {}", quizPOJO.getCode());
                                continue;
                            }

                            // Create quiz
                            Quiz quiz = new Quiz(gradeEntity, subjectEntity, chapterEntity, quizType, quizPOJO.getCode());
                            quiz = quizService.createQuiz(quiz);

                            // Create questions and choices
                            for (QuestionPOJO questionPOJO : quizPOJO.getQuestions()) {
                                Question question = new Question(
                                        quiz,
                                        questionPOJO.getContent(),
                                        questionPOJO.getExplanation()
                                );
                                question = questionRepository.save(question);

                                for (ChoicePOJO choiceDTO : questionPOJO.getChoices()) {
                                    Choice choice = new Choice(
                                            question,
                                            choiceDTO.getContent(),
                                            choiceDTO.getIsCorrect()
                                    );
                                    choiceRepository.save(choice);
                                }
                            }

                            logger.info("✅ Loaded quiz with code: {} ({} questions)",
                                    quizPOJO.getCode(), quizPOJO.getQuestions().size());

                        } catch (Exception e) {
                            logger.error("❌ Failed to load quiz {}: {}", quizPOJO.getCode(), e.getMessage(), e);
                        }
                    }

                    logger.info("🎉 Successfully loaded {} quizzes from {}", quizPOJOS.size(), fileName);

                } catch (IOException e) {
                    logger.error("❌ Error reading file {}: {}", fileName, e.getMessage(), e);
                } catch (Exception e) {
                    logger.error("❌ Unexpected error while processing {}: {}", fileName, e.getMessage(), e);
                }
            }
        }
    }

    private String getFileName(String subject, int grade) {
        switch (subject.toLowerCase()) {
            case "toán":
            case "toan":
                return "toan_" + grade + ".json";
            case "khoahoctunhien":
                return "khoahoctunhien_" + grade + ".json";
            case "văn":
            case "nguvan":
                return "nguvan_" + grade + ".json";
            case "anh":
            case "tienganh":
                return "tienganh_" + grade + ".json";
            default:
                throw new IllegalArgumentException("Môn học không hợp lệ: " + subject);
        }
    }

    @Transactional
    public void reloadAllData() throws IOException {
        logger.info("Reloading all quiz data...");

        choiceRepository.deleteAll();
        questionRepository.deleteAll();
        quizRepository.deleteAll();

        loadQuizzes();
    }
}
