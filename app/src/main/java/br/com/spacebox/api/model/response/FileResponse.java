package br.com.spacebox.api.model.response;

public class FileResponse {
    private Long id;
    private Long fileParentId;
    private String name;
    private String type;
    private Long size;
    private String content;
    private String updated;
    private String created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Long getFileParentId() {
        return fileParentId;
    }

    public void setFileParentId(Long fileParentId) {
        this.fileParentId = fileParentId;
    }
}
