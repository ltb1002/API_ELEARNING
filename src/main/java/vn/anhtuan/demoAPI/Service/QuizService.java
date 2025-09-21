package vn.anhtuan.demoAPI.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.*;
import vn.anhtuan.demoAPI.POJO.ChoicePOJO;
import vn.anhtuan.demoAPI.POJO.QuestionPOJO;
import vn.anhtuan.demoAPI.POJO.QuizPOJO;
import vn.anhtuan.demoAPI.POJO.QuizResponsePOJO;
import vn.anhtuan.demoAPI.Repository.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private QuizTypeRepository quizTypeRepository;

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Integer id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        return quiz.orElse(null);
    }

    public Quiz getQuizByCode(String code) {
        return quizRepository.findByCode(code);
    }

    public List<Quiz> getQuizzesByGradeAndSubject(Integer gradeId, Integer subjectId) {
        return quizRepository.findByGradeIdAndSubjectId(gradeId, subjectId);
    }

    public List<Quiz> getQuizzesByGradeSubjectAndType(Integer gradeId, Integer subjectId, Integer quizTypeId) {
        return quizRepository.findByGradeIdAndSubjectIdAndQuizTypeId(gradeId, subjectId, quizTypeId);
    }

    @Transactional
    public Quiz createQuiz(Quiz quiz) {
        if (quiz.getCode() == null || quiz.getCode().isEmpty()) {
            String code = generateQuizCode(quiz);
            quiz.setCode(code);
        }

        Quiz existingQuiz = getQuizByCode(quiz.getCode());
        if (existingQuiz != null) {
            throw new IllegalArgumentException("Quiz with code " + quiz.getCode() + " already exists");
        }

        return quizRepository.save(quiz);
    }

    private String generateQuizCode(Quiz quiz) {
        String subjectCode = "";
        switch (quiz.getSubject().getId()) {
            case 1:
                subjectCode = "TOAN";
                break;
            case 2:
                subjectCode = "KHTN";
                break;
            case 3:
                subjectCode = "VAN";
                break;
            case 4:
                subjectCode = "TA";
                break;
            default:
                subjectCode = "SUB";
                break;
        }

        String typeCode = quiz.getQuizType().getId() == 1 ? "15" : "30";

        List<Quiz> existingQuizzes = quizRepository.findByGradeIdAndSubjectIdAndQuizTypeId(
                quiz.getGrade().getId(), quiz.getSubject().getId(), quiz.getQuizType().getId());

        int sequentialNumber = existingQuizzes.size() + 1;

        return String.format("%s%d_%s_%03d", subjectCode, quiz.getGrade().getId(), typeCode, sequentialNumber);
    }

    public List<Question> getQuizQuestions(Integer quizId) {
        return questionRepository.findQuestionsByQuizIdOrdered(quizId);
    }

    public List<Choice> getQuestionChoices(Integer questionId) {
        return choiceRepository.findByQuestionId(questionId);
    }

    public List<Choice> getCorrectChoicesForQuestion(Integer questionId) {
        // Sửa thành phương thức có sẵn trong repository
        return choiceRepository.findCorrectChoicesByQuestionId(questionId);
    }

    public Map<Integer, Set<Integer>> getCorrectChoiceIdsForQuestions(List<Integer> questionIds) {
        List<Choice> correctChoices = choiceRepository.findCorrectChoicesByQuestionIds(questionIds);
        return correctChoices.stream()
                .collect(Collectors.groupingBy(
                        choice -> choice.getQuestion().getId(),
                        Collectors.mapping(Choice::getId, Collectors.toSet())
                ));
    }

    public Grade getGradeById(Integer id) {
        Optional<Grade> grade = gradeRepository.findById(id);
        return grade.orElse(null);
    }

    public Subject getSubjectById(Integer id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        return subject.orElse(null);
    }

    public QuizType getQuizTypeById(Integer id) {
        Optional<QuizType> quizType = quizTypeRepository.findById(id);
        return quizType.orElse(null);
    }

    public String getChapterNameById(Long chapterId) {
        return chapterRepository.findById(chapterId)
                .map(Chapter::getTitle)
                .orElse("Unknown Chapter");
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<QuizType> getAllQuizTypes() {
        return quizTypeRepository.findAll();
    }

    public QuizPOJO convertToQuizPOJO(Quiz quiz) {
        QuizPOJO pojo = new QuizPOJO();
        pojo.setId(quiz.getId());
        pojo.setGradeId(quiz.getGrade().getId());
        pojo.setSubjectId(quiz.getSubject().getId());
        pojo.setChapterId(quiz.getChapter().getId());
        pojo.setChapterTitle(quiz.getChapter().getTitle());
        pojo.setQuizTypeId(quiz.getQuizType().getId());
        pojo.setCode(quiz.getCode());

        List<QuestionPOJO> questionPOJOs = questionRepository.findQuestionsByQuizIdOrdered(quiz.getId())
                .stream()
                .map(this::convertToQuestionPOJO)
                .collect(Collectors.toList());
        pojo.setQuestions(questionPOJOs);

        return pojo;
    }

    public QuestionPOJO convertToQuestionPOJO(Question question) {
        QuestionPOJO pojo = new QuestionPOJO();
        pojo.setId(question.getId()); // Thêm ID của câu hỏi
        pojo.setContent(question.getContent());
        pojo.setExplanation(question.getExplanation());

        List<ChoicePOJO> choicePOJOs = choiceRepository.findByQuestionId(question.getId())
                .stream()
                .map(this::convertToChoicePOJO)
                .collect(Collectors.toList());
        pojo.setChoices(choicePOJOs);

        return pojo;
    }

    public ChoicePOJO convertToChoicePOJO(Choice choice) {
        ChoicePOJO pojo = new ChoicePOJO();
        pojo.setId(choice.getId()); // Thêm ID của lựa chọn
        pojo.setContent(choice.getContent());
        pojo.setIsCorrect(choice.getIsCorrect());
        return pojo;
    }

    public QuizResponsePOJO convertToQuizResponsePOJO(Quiz quiz) {
        QuizResponsePOJO pojo = new QuizResponsePOJO();
        pojo.setId(quiz.getId());
        pojo.setCode(quiz.getCode());
        pojo.setGrade(quiz.getGrade().getName());
        pojo.setSubject(quiz.getSubject().getName());
        pojo.setQuizType(quiz.getQuizType().getName());
        pojo.setDuration(quiz.getQuizType().getDurationMinutes());

        List<QuestionPOJO> questionPOJOs = questionRepository.findQuestionsByQuizIdOrdered(quiz.getId())
                .stream()
                .map(this::convertToQuestionPOJO)
                .collect(Collectors.toList());
        pojo.setQuestions(questionPOJOs);

        return pojo;
    }

    public QuizPOJO convertinfoQuizPOJO(Quiz quiz) {
        QuizPOJO pojo = new QuizPOJO();
        pojo.setId(quiz.getId());
        pojo.setGradeId(quiz.getGrade().getId());
        pojo.setSubjectId(quiz.getSubject().getId());
        pojo.setChapterId(quiz.getChapter().getId());
        pojo.setChapterTitle(quiz.getChapter().getTitle());
        pojo.setQuizTypeId(quiz.getQuizType().getId());
        pojo.setCode(quiz.getCode());
        return pojo;
    }
}