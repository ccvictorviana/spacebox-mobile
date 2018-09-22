package br.com.spacebox.api.model.response;

import java.util.Date;
import java.util.List;

public class FileSummaryResponse {
    private Long id;
    private Long fileParentId;
    private String name;
    private String type;
    private Long size;
    private Date updated;
    private Date created;

    private List<FileSummaryResponse> files;

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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getFileParentId() {
        return fileParentId;
    }

    public void setFileParentId(Long fileParentId) {
        this.fileParentId = fileParentId;
    }

    public List<FileSummaryResponse> getFiles() {
        return files;
    }

    public void setFiles(List<FileSummaryResponse> files) {
        this.files = files;
    }
}