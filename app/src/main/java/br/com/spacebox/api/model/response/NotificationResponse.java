package br.com.spacebox.api.model.response;


import java.util.Date;

public class NotificationResponse {

    private Long id;

    private int type;

    private String nFileName;

    private Long nFileId;

    private Long userOwnerId;

    private Long userActionId;

    private Date created;

    private FileSummaryResponse file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getnFileName() {
        return nFileName;
    }

    public void setnFileName(String nFileName) {
        this.nFileName = nFileName;
    }

    public Long getnFileId() {
        return nFileId;
    }

    public void setnFileId(Long nFileId) {
        this.nFileId = nFileId;
    }

    public Long getUserOwnerId() {
        return userOwnerId;
    }

    public void setUserOwnerId(Long userOwnerId) {
        this.userOwnerId = userOwnerId;
    }

    public Long getUserActionId() {
        return userActionId;
    }

    public void setUserActionId(Long userActionId) {
        this.userActionId = userActionId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public FileSummaryResponse getFile() {
        return file;
    }

    public void setFile(FileSummaryResponse file) {
        this.file = file;
    }
}