package com.abbsolute.ma_livu.Community.CommunityComment.CommunityCommentComment;

public interface CommuCommentCommentOnItemClick {
    void commentLike(int position);
    void commentDislike(int position);
    void deleteItem(int position);
    void reportItem(int position);
    boolean checkUser(int position);
}
