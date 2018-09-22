package br.com.spacebox.api.model.request;

import java.util.Date;

public class FileFilterRequest {
    private Date beginUpdateDate;
    private Long fileParentId;

    public Date getBeginUpdateDate() {
        return beginUpdateDate;
    }

    public void setBeginUpdateDate(Date beginUpdateDate) {
        this.beginUpdateDate = beginUpdateDate;
    }

    public Long getFileParentId() {
        return fileParentId;
    }

    public void setFileParentId(Long fileParentId) {
        this.fileParentId = fileParentId;
    }
}
