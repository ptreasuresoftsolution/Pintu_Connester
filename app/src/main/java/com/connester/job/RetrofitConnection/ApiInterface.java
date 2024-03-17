package com.connester.job.RetrofitConnection;

import com.connester.job.RetrofitConnection.jsontogson.BusinessPageRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.EducationListResponse;
import com.connester.job.RetrofitConnection.jsontogson.FeedsCommentListResponse;
import com.connester.job.RetrofitConnection.jsontogson.FeedsMasterResponse;
import com.connester.job.RetrofitConnection.jsontogson.GetLinkMetaDataResponse;
import com.connester.job.RetrofitConnection.jsontogson.GroupMembersListResponse;
import com.connester.job.RetrofitConnection.jsontogson.GroupRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.JobsEventMasterResponse;
import com.connester.job.RetrofitConnection.jsontogson.MembersListResponse;
import com.connester.job.RetrofitConnection.jsontogson.MyGroupListResponse;
import com.connester.job.RetrofitConnection.jsontogson.MyPagesListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NetworkMenuListCounter;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSuggestedListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.OurEventPostRow;
import com.connester.job.RetrofitConnection.jsontogson.OurJobPostRow;
import com.connester.job.RetrofitConnection.jsontogson.PageAnalyticsResponse;
import com.connester.job.RetrofitConnection.jsontogson.PageJobApplicationResponse;
import com.connester.job.RetrofitConnection.jsontogson.PageMembersResponse;
import com.connester.job.RetrofitConnection.jsontogson.ProjectItemResponse;
import com.connester.job.RetrofitConnection.jsontogson.ProjectListResponse;
import com.connester.job.RetrofitConnection.jsontogson.RequestedGroupListResponse;
import com.connester.job.RetrofitConnection.jsontogson.SignUpOtpResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserLoginResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.WorkExperienceItemResponse;
import com.connester.job.RetrofitConnection.jsontogson.WorkExperienceListResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
    Call<UserRowResponse> GET_LOGIN_USER_ROW(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/Login")
    Call<UserLoginResponse> CALL_LOGIN(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/addSingUpDetails")
    Call<SignUpOtpResponse> SIGNUP_SUBMIT_FOR_OTP(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/otpEmailSendMail/signUp")
    Call<SignUpOtpResponse> SIGNUP_OTP_RESEND(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/isAuth/email")
    Call<UserLoginResponse> SIGNUP_SUBMIT_WITH_OTP(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/ForgotPassword")
    Call<NormalCommonResponse> CALL_FORGOT_PASS(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getUniqueUserName")
    Call<NormalCommonResponse> GET_UNIQUE_USERNAME(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/CheckUsername")
    Call<NormalCommonResponse> CHECK_USERNAME(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/StepOne")
    Call<NormalCommonResponse> STEP_1_SUBMIT(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedsLikeUnLink")
    Call<NormalCommonResponse> CALL_LIKE_UNLIKE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedCommentsJson")
    Call<FeedsCommentListResponse> GET_FEED_COMMENT(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedOnComment")
    Call<NormalCommonResponse> SUBMIT_COMMENT_ON_FEEDS(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedsForward")
    Call<NormalCommonResponse> FEEDS_SHARE_FORWARD(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedClosed")
    Call<NormalCommonResponse> FEEDS_OPTION_CLOSE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedsSaveUnsave")
    Call<NormalCommonResponse> FEEDS_OPTION_SAVE_UNSAVE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/unFollowProfile")
    Call<NormalCommonResponse> FEEDS_OPTION_UNFOLLOW_PROFILE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/homeFeedsJson")
    Call<FeedsMasterResponse> HOME_FEEDS_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/feedMasterJson")
    Call<FeedsMasterResponse> FEED_MASTER_JSON_LIST(@FieldMap Map<String, String> params);
    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/singleFeedJsonHTML")
    Call<FeedsMasterResponse> SINGLE_FEED_JSON_CALL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Network/networkDefaultData")
    Call<NetworkSuggestedListResponse> NETWORK_SUGGESTED_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Network/networkSideCounter")
    Call<NetworkMenuListCounter> NETWORK_SIDE_COUNTER(@FieldMap Map<String, String> params);

    //InvReqAccept / InvReqDecline / SendInvReq / RemoveConnection / RemoveFollower / UnFollowFollowing / ReqFollow / FollowReqAccept / FollowReqReject
    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/mangeUserNetwork")
    Call<NormalCommonResponse> NETWORK_ACTION_MANGE(@FieldMap Map<String, String> params);

    //connectReqUsersMaster / connectUsers / followReqUsers / followerUsers / followingUsers / userCommunitys / userBusinessPages / userEvents / suggestedCityUser / suggestedIndustryUser / suggestedGroup / suggestedBusPages
    @FormUrlEncoded
    @POST(PRE_FIX + "Network/seeAll")
    Call<Object> NETWORK_SEE_ALL_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/userCommunityJoinRequest")
    Call<NormalCommonResponse> GROUP_JOIN_REQUEST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/userCommunityExit")
    Call<NormalCommonResponse> GROUP_EXIT_CALL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/followBusinessPage")
    Call<NormalCommonResponse> PAGE_FOLLOW_REQUEST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/unFollowBusinessPage")
    Call<NormalCommonResponse> PAGE_UNFOLLOW_CALL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/getMetaData")
    Call<GetLinkMetaDataResponse> GET_LINK_META_DATA_CALL(@FieldMap Map<String, String> params);

    @POST(PRE_FIX + "Feeds/addFeedPhotos")
    Call<NormalCommonResponse> FEED_ADD_PHOTOS_SUBMIT(@Body RequestBody body);

    @POST(PRE_FIX + "Feeds/addFeedVideo")
    Call<NormalCommonResponse> FEED_ADD_VIDEO_SUBMIT(@Body RequestBody body);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/addFeedTextLink")
    Call<NormalCommonResponse> FEED_ADD_TEXT_LINK_SUBMIT(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/applyJob")
    Call<NormalCommonResponse> JOB_APPLY(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/suggestedJobs")
    Call<JobsEventMasterResponse> JOB_SUGGESTED_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Feeds/suggestedEvents")
    Call<JobsEventMasterResponse> EVENT_SUGGESTED_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/accountClosed")
    Call<NormalCommonResponse> ACCOUNT_CLOSED_CALL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/reActivateAccount")
    Call<NormalCommonResponse> ACCOUNT_REACTIVATE_CALL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/changePassword")
    Call<NormalCommonResponse> CHANGE_PASSWORD_CALL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/unBlockedUser")
    Call<NormalCommonResponse> UN_BLOCKED_USER(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/loadBlockedUserList")
    Call<MembersListResponse> BLOCKED_USER_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/logOutAllDevices")
    Call<NormalCommonResponse> LOG_OUT_ALL_DEVICES_CALL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getUserClmData")
    Call<UserRowResponse> GET_CLM_DATA_USER_ROW(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getProjects")
    Call<ProjectListResponse> PROJECT_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getProjectsItem")
    Call<ProjectItemResponse> GET_PROJECT_ITEM(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/manageProjects")
    Call<NormalCommonResponse> PROJECT_ITEM_ADD_EDIT(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/removeProjectsItem")
    Call<NormalCommonResponse> PROJECT_ITEM_REMOVE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getExperience")
    Call<WorkExperienceListResponse> WORK_EXPERIENCE_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getExperienceItem")
    Call<WorkExperienceItemResponse> GET_WORK_EXPERIENCE_ITEM(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/manageWorkExperience")
    Call<NormalCommonResponse> WORK_EXPERIENCE_ITEM_ADD_EDIT(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/removeExperienceItem")
    Call<NormalCommonResponse> WORK_EXPERIENCE_ITEM_REMOVE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getEducation")
    Call<EducationListResponse> EDUCATION_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getEducationItem")
    Call<ProjectItemResponse> GET_EDUCATION_ITEM(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/manageEducation")
    Call<NormalCommonResponse> EDUCATION_ITEM_ADD_EDIT(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/removeEducationItem")
    Call<NormalCommonResponse> EDUCATION_ITEM_REMOVE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getSkillTbl")
    Call<NormalCommonResponse> GET_SKILL_TBL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/getLangaugeTbl")
    Call<NormalCommonResponse> GET_LANGUAGE_TBL(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "UserMange/editProfile")
    Call<NormalCommonResponse> EDIT_PROFILE_INFO_OR_CLM_ITEM(@FieldMap Map<String, String> params);

    @POST(PRE_FIX + "UserMange/changeProfilePic")
    Call<NormalCommonResponse> EDIT_PROFILE_CHANGE_PROFILE_PIC_BANNER(@Body RequestBody body);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/getMyPages")
    Call<MyPagesListResponse> GET_MY_PAGES_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/getPageDetailsRow")
    Call<BusinessPageRowResponse> BUSINESS_PAGE_ROW(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/getPageAnalytics")
    Call<PageAnalyticsResponse> BUSINESS_PAGE_ANALYTICS(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/jobApplicationTab")
    Call<PageJobApplicationResponse> BUSINESS_PAGE_JOB_APPLICATION(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/changeJobApplicationStatus")
    Call<NormalCommonResponse> PAGES_JOB_APPLICATION_CHANGE_STATUS(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/getPageMembers")
    Call<PageMembersResponse> PAGES_MEMBERS_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/unFollowBusinessPage")
    Call<NormalCommonResponse> PAGES_MEMBERS_REMOVE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/closeMyBusinessPageByPageAdminUser")
    Call<NormalCommonResponse> BUSINESS_PAGE_CLOSED(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/reActivateMyBusinessPage")
    Call<NormalCommonResponse> BUSINESS_PAGE_REACTIVATE_CALL(@FieldMap Map<String, String> params);

    @POST(PRE_FIX + "Business/mangeMyBusinessPage")
    Call<NormalCommonResponse> PAGE_CREATE_MANAGE_CALL(@Body RequestBody body);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/getOurJobPostRow")
    Call<OurJobPostRow> PAGES_OUR_JOB_POST_ROW(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/mangeOurPostedJob")
    Call<NormalCommonResponse> PAGES_MANAGE_OUR_JOB_POST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/removeOurJobPost")
    Call<NormalCommonResponse> PAGES_REMOVE_OUR_JOB_POST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/getOurJobPostEvent")
    Call<OurEventPostRow> PAGES_OUR_EVENT_POST_ROW(@FieldMap Map<String, String> params);

    @POST(PRE_FIX + "Business/mangeOurPostedEvent")
    Call<NormalCommonResponse> PAGES_MANAGE_OUR_EVENT_POST(@Body RequestBody body);

    @FormUrlEncoded
    @POST(PRE_FIX + "Business/removeOurJobEvent")
    Call<NormalCommonResponse> PAGES_REMOVE_OUR_EVENT_POST(@FieldMap Map<String, String> params);


    @POST(PRE_FIX + "Community/mangeMyCreatedGroup")
    Call<NormalCommonResponse> GROUP_CREATE_MANAGE_CALL(@Body RequestBody body);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/getMyCommunitys")
    Call<MyGroupListResponse> GET_MY_GROUP_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/getRequestedCommunity")
    Call<RequestedGroupListResponse> GET_MY_REQUESTED_GROUP_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/getCommunityDetailsRow")
    Call<GroupRowResponse> GROUP_ROW(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/closeMyCreatedCommunity")
    Call<NormalCommonResponse> GROUP_CLOSED(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/reActivateMyCreatedCommunity")
    Call<NormalCommonResponse> GROUP_RE_ACTIVE(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/loadBlockedCommunityMembers")
    Call<GroupMembersListResponse> GROUP_BLOCKED_MEMBERS_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/unBlockedCommunityMember")
    Call<NormalCommonResponse> UN_BLOCKED_GROUP_MEMBERS_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/getCommunityMembers")
    Call<GroupMembersListResponse> GROUP_MEMBERS_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/blockedCommunityMember")
    Call<NormalCommonResponse> BLOCK_GROUP_MEMBERS(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/userCommunityExit")
    Call<NormalCommonResponse> REMOVE_GROUP_MEMBERS(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/getCommunityMemberRequest")
    Call<GroupMembersListResponse> GROUP_MEMBER_REQUEST_LIST(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/userCommunityJoinRequestReject")
    Call<NormalCommonResponse> GROUP_MEMBER_REQUEST_REJECT(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(PRE_FIX + "Community/userCommunityJoinRequestAccept")
    Call<NormalCommonResponse> GROUP_MEMBER_REQUEST_ACCEPT(@FieldMap Map<String, String> params);
}
