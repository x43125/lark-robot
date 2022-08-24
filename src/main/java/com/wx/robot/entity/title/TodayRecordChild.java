package com.wx.robot.entity.title;

import java.util.Date;

/**
 * @author wangxiang
 * @date 2022/8/24 10:24
 * @description
 */
public class TodayRecordChild {
    private Question question;
    private String lastSubmission;
    private Date date;
    private String userStatus;
    private String __typename;

    public String getLastSubmission() {
        return lastSubmission;
    }

    public void setLastSubmission(String lastSubmission) {
        this.lastSubmission = lastSubmission;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String get__typename() {
        return __typename;
    }

    public void set__typename(String __typename) {
        this.__typename = __typename;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
