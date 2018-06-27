package volunteer.sk.greate43.com.gotcha;

public class FriendList {
    private String pushId;
    private String userId;
    private String friendId;
    private boolean isFriendRequestAccepted;
    private boolean isFriendRequestRejected;

    private boolean isRequestAlreadySent;
    public FriendList() {
    }

    public boolean isFriendRequestRejected() {
        return isFriendRequestRejected;
    }

    public void setFriendRequestRejected(boolean friendRequestRejected) {
        isFriendRequestRejected = friendRequestRejected;
    }

    public boolean isRequestAlreadySent() {
        return isRequestAlreadySent;
    }

    public void setRequestAlreadySent(boolean requestAlreadySent) {
        isRequestAlreadySent = requestAlreadySent;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public boolean isFriendRequestAccepted() {
        return isFriendRequestAccepted;
    }

    public void setFriendRequestAccepted(boolean friendRequestAccepted) {
        isFriendRequestAccepted = friendRequestAccepted;
    }
}
