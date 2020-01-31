package com.group.peach.thewalkingschoolbus.proxy;

import com.group.peach.thewalkingschoolbus.model.Group;
import com.group.peach.thewalkingschoolbus.model.Message;
import com.group.peach.thewalkingschoolbus.model.ObjectIDModel;
import com.group.peach.thewalkingschoolbus.model.PermissionRequest;
import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.model.GPSLocationModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * The ProxyBuilder class will handle the apiKey and token being injected as a header to all calls
 * This is a Retrofit interface.
 */
public interface WGServerProxy {
    @GET("getApiKey")
    Call<String> getApiKey(@Query("Peach") String groupName, @Query("jta113") String sfuId);

    @POST("/users/signup")
    Call<User> createNewUser(@Body User user);

    @POST("/login")
    Call<Void> login(@Body User userWithEmailAndPassword);

    @GET("/users")
    Call<List<User>> getUsers();

    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") Long userId);

    @GET("/users/byEmail")
    Call<User> getUserByEmail(@Query("email") String email);

    @POST("/users/{id}")
    Call<User> editUser(@Path("id") Long userId,@Body User user);


    /**
     * MORE GOES HERE:
     * - Monitoring
     * - Groups
     */

    /**
     * Group Callback
     */
    @GET("/groups")
    Call<List<Group>> getGroups();

    @Headers("permissions-enabled: true")
    @POST("/groups")
    Call<Group> createNewGroup(@Body Group g);

    @Headers("permissions-enabled: true")
    @POST("/groups/{id}")
    Call<Group> editGroup(@Path("id") Long id,@Body Group groups);

    @POST("groups/{id}/memberUsers")
    Call<List<User>> addNewGroupMember(@Path("id") Long id, @Body ObjectIDModel objectIDModel);

    @DELETE("groups/{groupId}/memberUsers/{userId}")
    Call<Void> removeGroupMember(@Path("groupId") Long id, @Path("userId") Long userId);

    /**
     * GPS callback
     */
    @GET("users/{id}/lastGpsLocation")
    Call<GPSLocationModel> getUserGps(@Path("id") Long id);

    @POST("/users/{id}/lastGpsLocation")
    Call<GPSLocationModel> setUserGPS(@Path("id") Long id, @Body GPSLocationModel gpsLocationModel);

    /**
     * Group Details
     */
    @GET("/groups/{id}")
    Call<Group> getGroupDetail(@Path("id") Long id);

    //get group members
    @GET("/groups/{id}/memberUsers")
    Call<List<User>> getGroupMembers(@Path("id") Long id);

    //monitor callbacks
    @GET("/users/{id}/monitorsUsers")
    Call<List<User>> getChildren(@Path("id") Long userId);

    @POST("/users/{id}/monitorsUsers")
    Call<Void> addChild(@Path("id")Long userId, @Body Map<String, Long> payload);

    @GET("/users/{id}/monitoredByUsers")
    Call<List<User>> getParents(@Path("id") Long userId);

    @POST("/users/{id}/monitoredByUsers")
    Call<Void> addParent(@Path("id")Long userId, @Body Map<String, Long> payload);

    //message call backs
    //use different query string to filter messages
    @GET("/messages")
    Call<List<Message>> getAllMsg(@Query("foruser") Long userId);


    @GET("/messages")
    Call<List<Message>> getUnread(@Query("foruser") Long userId, @Query("status") String ifRead);

    @GET("/messages")
    Call<List<Message>> getUnreadEmg(@Query("foruser") Long userId,
                                     @Query("status") String ifRead,
                                     @Query("is-emergency") String isEmg);


    @DELETE("/messages/{id}")
    Call<Void> deleteMessageById(@Path("id") Long messageId);

    @POST("/messages/{messageId}/readby/{userId}")
    Call<Void> markMessageRead(@Path("messageId") Long messageId, @Path("userId") Long userId, @Body boolean isRead);

    @POST("/messages/togroup/{groupId}")
    Call<Void> sendMsgToGroup(@Path("groupId") Long groupId, @Body Map<String, Object> payload);

    @POST("/messages/toparentsof/{userId}")
    Call<Void> sendMsgToParent(@Path("userId") Long userId, @Body Map<String, Object> payload);

    //PermissionRequest callbacks
    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissions();

    @GET("/permissions/{id}")
    Call<PermissionRequest> getPermissionById(@Path("id") long permissionId);

    @POST("/permissions/{id}")
    Call<PermissionRequest> approveOrDenyPermissionRequest(
            @Path("id") long permissionId,
            @Body PermissionStatus status
    );

    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissionByUserId(@Query("userId") long id);

    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissionByStatus(@Query("status") PermissionStatus status);

    enum PermissionStatus {
        PENDING,
        APPROVED,
        DENIED
    }


}

