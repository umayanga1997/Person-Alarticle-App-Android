package com.openlab.personalarticle.Models;

import java.io.Serializable;
import java.util.Date;

public class ArticleModel implements Serializable {
    private String title;
    private String article;
    private String id;
    private boolean isPrivateArticle;
    private Date createDate;
    private Date lastUpdateDate;
    private String thumbnailUrl;
    private String writer;
    private String userID;

    public ArticleModel() {
    }

    public ArticleModel(String id,String userID, String title,String writer, String article, boolean isPrivateArticle, Date createDate, Date lastUpdateDate, String thumbnailUrl) {
        this.title = title;
        this.article = article;
        this.isPrivateArticle = isPrivateArticle;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.thumbnailUrl = thumbnailUrl;
        this.id = id;
        this.userID = userID;
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public boolean isPrivateArticle() {
        return isPrivateArticle;
    }

    public void setPrivateArticle(boolean privateArticle) {
        isPrivateArticle = privateArticle;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
