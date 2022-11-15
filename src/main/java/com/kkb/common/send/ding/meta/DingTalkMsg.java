/*
 * Copyright
 */
package com.kkb.common.send.ding.meta;

import lombok.Data;

import java.util.List;

/**
 * 发送钉钉消息体，具体详见 {@linkplain https://yapi.kaikeba.com/project/2565/interface/api/37385}
 *
 * @author sy
 * @date 2021-12-02 12:20
 */
@Data
public class DingTalkMsg {


    public final static String ACTION_CARD = "action_card";
    public final static String MARKDOWN = "markdown";
    public final static String TEXT = "text";

    /**
     * 接收者的部门id列表 (用户id和部门id可同时传, 但不能同时为空)
     */
    private Long[] deptIdList;
    /**
     * 接收者的用户userid列表(corgi uid)
     */
    private Long[] useridList;
    /**
     * msg
     */
    private Msg msg;

    @Data
    public static class Msg {

        /**
         * 发送消息类型 ，消息类型：action_card，file，image，link，markdown，oa，text，voice
         */
        private String msgtype;


        private ActionCard actionCard;

        /**
         * 文件类型
         */
        private File file;

        /**
         * 发送图片
         */
        private Image image;

        /**
         * 发送链接
         */
        private Link link;

        /**
         * 发送markdown类型
         */
        private Markdown markdown;
        private Oa oa;

        /**
         * 文本类型
         */
        private Text text;

        /**
         * 音频类型
         */
        private Voice voice;
    }


    @Data
    public static class ActionCard {
        private List<BtnJsonList> btnJsonList;
        private String btnOrientation;
        private String markdown;
        private String singleTitle;
        private String singleUrl;
        private String title;
    }

    @Data
    public static class BtnJsonList {
        private String actionUrl;
        private String title;
    }

    @Data
    public static class File {

        /**
         * 媒体文件id。引用的媒体文件最大10MB
         */
        private String mediaId;
    }

    @Data
    public static class Image {

        /**
         * 图片消息
         */
        private String mediaId;
    }

    @Data
    public static class Link {

        /**
         * 图片地址
         */
        private String messageUrl;

        /**
         * 消息点击链接地址，当发送消息为小程序时支持小程序跳转链接
         */
        private String picUrl;

        /**
         * 消息标题，建议100字符以内
         */
        private String text;

        /**
         * 消息描述，建议500字符以内
         */
        private String title;
    }

    @Data
    public static class Markdown {

        /**
         * markdown格式的消息，建议500字符以内
         */
        private String text;

        /**
         * 首屏会话透出的展示内容
         */
        private String title;
    }

    @Data
    public static class Oa {
        private Body body;
        private Head head;
        private String messageUrl;
        private String pcMessageUrl;
    }

    @Data
    public static class Body {
        private String author;
        private String content;
        private String fileContent;
        private List<Form> formList;
        private String image;
        private Rich rich;
        private String title;
    }

    @Data
    public static class Form {
        private String image;
    }

    @Data
    public static class Rich {
        private String num;
        private String unit;
    }

    @Data
    public static class Head {
        private String bgcolor;
        private String title;
    }

    @Data
    public static class Text {
        private String content;
    }

    @Data
    public static class Voice {
        private String duration;
        private String mediaId;
    }


}
