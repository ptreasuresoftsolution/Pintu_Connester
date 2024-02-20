package com.connester.job.RetrofitConnection;

import com.connester.job.RetrofitConnection.jsontogson.FeedsCommentListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.SignUpOtpResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserLoginResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by DELL on 5/21/2017.
 */
public interface ApiInterface {

    String OFFLINE_FOLDER = "/JobPortal";
    //    String OFFLINE_FOLDER = "";
    String PRE_FIX = OFFLINE_FOLDER + "/api/";

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getLoginUserRow")
    Call<UserRowResponse> GET_LOGIN_USER_ROW(@FieldMap Map<String, String> params );

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/Login")
    Call<UserLoginResponse> CALL_LOGIN(@FieldMap Map<String, String> params );

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/addSingUpDetails")
    Call<SignUpOtpResponse> SIGNUP_SUBMIT_FOR_OTP(@FieldMap Map<String, String> params );

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/otpEmailSendMail/signUp")
    Call<SignUpOtpResponse> SIGNUP_OTP_RESEND(@FieldMap Map<String, String> params );

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/isAuth/email")
    Call<UserLoginResponse> SIGNUP_SUBMIT_WITH_OTP(@FieldMap Map<String, String> params );

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/ForgotPassword")
    Call<NormalCommonResponse> CALL_FORGOT_PASS(@FieldMap Map<String, String> params );

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getUniqueUserName")
    Call<NormalCommonResponse> GET_UNIQUE_USERNAME(@FieldMap Map<String, String> params );

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/CheckUsername")
    Call<NormalCommonResponse> CHECK_USERNAME(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/StepOne")
    Call<NormalCommonResponse> STEP_1_SUBMIT(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedsLikeUnLink")
    Call<NormalCommonResponse> CALL_LIKE_UNLIKE(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedCommentsJson")
    Call<FeedsCommentListResponse> GET_FEED_COMMENT(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedOnComment")
    Call<NormalCommonResponse> SUBMIT_COMMENT_ON_FEEDS(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedsForward")
    Call<NormalCommonResponse> FEEDS_SHARE_FORWARD(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedClosed")
    Call<NormalCommonResponse> FEEDS_OPTION_CLOSE(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedsSaveUnsave")
    Call<NormalCommonResponse> FEEDS_OPTION_SAVE_UNSAVE(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/unFollowProfile")
    Call<NormalCommonResponse> FEEDS_OPTION_UNFOLLOW_PROFILE(@FieldMap Map<String, String> params );
    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/applyJob")
    Call<NormalCommonResponse> JOB_APPLY(@FieldMap Map<String, String> params );


}
