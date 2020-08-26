package com.abbsolute.ma_livu.Community.CommunityComment;

public interface CommuCommentOnItemClick {
        void commentLike(int position);
        void commentDislike(int position);
        void deleteItem(int position);
        void reportItem(int position);
        void goCommunityCommentComment(int position);
}
