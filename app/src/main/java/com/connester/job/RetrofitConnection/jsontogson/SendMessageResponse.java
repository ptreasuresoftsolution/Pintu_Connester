package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendMessageResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("chat_master_id")
    @Expose
    public Integer chatMasterId;
    @SerializedName("pushJson")
    @Expose
    public PushJson pushJson;
    //"pushJson": {
    //        "type": "SEND",
    //        "sendUser": {
    //            "user_master_id": "3",
    //            "name": "Pintu Dholariya",
    //            "user_name": "pintu033dholariya",
    //            "profile_link": "pintu033dholariya",
    //            "profile_pic": "202403100104172743.png"
    //        },
    //        "recUser": {
    //            "user_master_id": "4",
    //            "name": "pintu",
    //            "user_name": "pintu033dholariya656",
    //            "profile_link": "pintu033dholariya656",
    //            "profile_pic": "202401061046091705.jpg"
    //        },
    //        "chatData": {
    //            "send_user_master_id": "3",
    //            "rec_user_master_id": "4",
    //            "msg_type": "TEXT",
    //            "msg_file": "",
    //            "file_type": "",
    //            "msg_send_time": "2024-03-27 11:03:05",
    //            "msg_status": "SEND",
    //            "msg": "hi",
    //            "chat_master_id": 12
    //        }
    //    }
    public class PushJson {

        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("sendUser")
        @Expose
        public SendUser sendUser;

        public class SendUser {

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("user_name")
            @Expose
            public String userName;
            @SerializedName("profile_link")
            @Expose
            public String profileLink;
            @SerializedName("profile_pic")
            @Expose
            public String profilePic;

        }

        @SerializedName("recUser")
        @Expose
        public RecUser recUser;

        public class RecUser {

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("user_name")
            @Expose
            public String userName;
            @SerializedName("profile_link")
            @Expose
            public String profileLink;
            @SerializedName("profile_pic")
            @Expose
            public String profilePic;

        }

        @SerializedName("chatData")
        @Expose
        public ChatData chatData;

        public class ChatData {

            @SerializedName("send_user_master_id")
            @Expose
            public String sendUserMasterId;
            @SerializedName("rec_user_master_id")
            @Expose
            public String recUserMasterId;
            @SerializedName("msg_file")
            @Expose
            public String msgFile;
            @SerializedName("msg_type")
            @Expose
            public String msgType;
            @SerializedName("msg_send_time")
            @Expose
            public String msgSendTime;
            @SerializedName("msg_status")
            @Expose
            public String msgStatus;
            @SerializedName("msg")
            @Expose
            public String msg;
            @SerializedName("chat_master_id")
            @Expose
            public String chatMasterId;

        }
    }

    @SerializedName("fcmResponse")
    @Expose
    public String fcmResponse;
    @SerializedName("msg")
    @Expose
    public String msg;

}
