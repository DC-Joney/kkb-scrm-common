/*
 * Copyright
 */
package com.kkb.common.send.ding.meta;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类描述
 *
 * @author sy
 * @date 2021-11-22 07:22
 * @since
 */
public class DingTalkMsgBuilder {

    public static ActionCardFactory actionCard() {
        return Message.actionCard();
    }

    public static MarkdownFactory markdown() {
        return Message.markdown();
    }

    public static MarkdownFactory markdown(String title) {
        MarkdownFactory markdown = Message.markdown();
        markdown.title(title);
        return markdown;
    }

    public static MarkdownFactory markdown(String title, String text, Long... userIds) {
        MarkdownFactory markdown = markdown();
        markdown.title(title);
        markdown.text(text);
        markdown.withUsers(userIds);
        return markdown;
    }

    public static TextFactory text() {
        return Message.text();
    }

    @Data
    public static class Message {

        public static ActionCardFactory actionCard() {
            DingTalkMsg.Msg message = new DingTalkMsg.Msg();
            message.setMsgtype(DingTalkMsg.ACTION_CARD);
            return new ActionCardFactory(message);
        }

        public static MarkdownFactory markdown() {
            DingTalkMsg.Msg message = new DingTalkMsg.Msg();
            message.setMsgtype(DingTalkMsg.MARKDOWN);
            return new MarkdownFactory(message);
        }

        public static TextFactory text() {
            DingTalkMsg.Msg message = new DingTalkMsg.Msg();
            message.setMsgtype(DingTalkMsg.TEXT);
            return new TextFactory(message);
        }

    }

    private static class MessageFactory<T extends MessageFactory<T>> {
        protected final DingTalkMsg.Msg message;
        protected DingTalkMsg.ActionCard actionCard;
        protected DingTalkMsg.Markdown markdown;
        protected DingTalkMsg.Text text;
        protected List<Long> deptIdList;
        protected List<Long> useridList;

        protected MessageFactory(DingTalkMsg.Msg message) {
            this.message = message;
        }

        @SuppressWarnings("unchecked")
        public final T withUsers(Long... userIds) {
            if (ArrayUtils.isNotEmpty(userIds)) {
                if (null == useridList) {
                    useridList = new ArrayList<>();
                }
                useridList.addAll(Arrays.asList(userIds));
            }
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public final T withDepts(Long... deptIds) {
            if (ArrayUtils.isNotEmpty(deptIds)) {
                if (null == deptIdList) {
                    deptIdList = new ArrayList<>();
                }
                deptIdList.addAll(Arrays.asList(deptIds));
            }
            return (T) this;
        }

        private Long[] list2Array(List<Long> objs) {
            if (CollectionUtils.isEmpty(objs)) {
                return null;
            }
            return objs.toArray(new Long[0]);
        }

        public final DingTalkMsg get() {
            DingTalkMsg msg = new DingTalkMsg();
            message.setActionCard(actionCard);
            message.setMarkdown(markdown);
            message.setText(text);
            msg.setMsg(message);
            msg.setDeptIdList(list2Array(deptIdList));
            msg.setUseridList(list2Array(useridList));
            return msg;
        }
    }

    public static class MarkdownFactory extends MessageFactory<MarkdownFactory> {

        private MarkdownFactory(DingTalkMsg.Msg message) {
            super(message);
            this.markdown = new DingTalkMsg.Markdown();
        }

        public MarkdownFactory title(String title) {
            markdown.setTitle(title);
            return this;
        }

        public MarkdownFactory text(String text) {
            markdown.setText(text);
            return this;
        }
    }
        public static class TextFactory extends MessageFactory<TextFactory> {

            private TextFactory(DingTalkMsg.Msg message) {
                super(message);
                this.text = new DingTalkMsg.Text();
            }

            public TextFactory content(String content) {
                text.setContent(content);
                return this;
            }
    }

    public static class ActionCardFactory extends MessageFactory<ActionCardFactory> {

        ActionCardFactory(DingTalkMsg.Msg message) {
            super(message);
            this.actionCard = new DingTalkMsg.ActionCard();
        }

        public ActionCardFactory title(String title) {
            actionCard.setTitle(title);
            return this;
        }

        public ActionCardFactory markdown(String markdown) {
            actionCard.setMarkdown(markdown);
            return this;
        }

        public ActionCardFactory singleTitle(String singleTitle) {
            actionCard.setSingleTitle(singleTitle);
            return this;
        }

        public ActionCardFactory singleUrl(String singleUrl) {
            actionCard.setSingleUrl(singleUrl);
            return this;
        }

    }

}
