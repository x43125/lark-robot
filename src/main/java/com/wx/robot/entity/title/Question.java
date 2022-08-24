package com.wx.robot.entity.title;

/**
 * @author wangxiang
 * @date 2022/8/24 10:27
 * @description
 */
public class Question {
    private String questionFrontendId;
    private String questionTitleSlug;
    private String __typename;

    public String getQuestionFrontendId() {
        return questionFrontendId;
    }

    public void setQuestionFrontendId(String questionFrontendId) {
        this.questionFrontendId = questionFrontendId;
    }

    public String getQuestionTitleSlug() {
        return questionTitleSlug;
    }

    public void setQuestionTitleSlug(String questionTitleSlug) {
        this.questionTitleSlug = questionTitleSlug;
    }

    public String get__typename() {
        return __typename;
    }

    public void set__typename(String __typename) {
        this.__typename = __typename;
    }
}
