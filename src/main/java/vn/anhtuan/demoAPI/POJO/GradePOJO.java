package vn.anhtuan.demoAPI.POJO;

public class GradePOJO {
    private Integer id;
    private String name;
    private String code;

    public GradePOJO() {}

    public GradePOJO(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}