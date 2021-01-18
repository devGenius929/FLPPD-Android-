package com.antigravitystudios.flppd.networking;

import com.antigravitystudios.flppd.models.Connection;
import com.antigravitystudios.flppd.models.Message;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //----------------------------------AUTHENTICATION-------------------------------------

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("authenticate")
    Call<User> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("facebook_auth")
    Call<User> facebook_auth(
            @Field("access_token") String facebookToken
    );

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("recover")
    Call<Message> recover(
            @Field("phone_number") String phone_number
    );

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("signup")
    Call<User> signup(
            @Field("user[email]") String email,
            @Field("user[password]") String password,
            @Field("user[first_name]") String first_name,
            @Field("user[last_name]") String last_name,
            @Field("user[phone_number]") String phone_number
    );

    @Headers("Accept: application/json")
    @DELETE("logout")
    Call<Message> logout(@Header("Authorization") String token);

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("apns")
    Call<Message> setpush(@Header("Authorization") String token,
                          @Field("device_id") String device_id,
                          @Field("device_token") String device_token
    );

    ///----------------------------------PROPERTIES----------------------------------

    @Headers("Accept: application/json")
    @GET("properties")
    Call<List<Property>> getProperties(
            @Header("Authorization") String token,
            @Query("user_id") Integer user_id,
            @Query("limit") Integer limit,
            @Query("type") String type,
            @Query("city") String city,
            @Query("state") String state,
            @Query("zipcode") String zipcode,
            @Query("price_min") String price_min,
            @Query("price_max") String price_max
    );

    @Headers("Accept: application/json")
    @GET("favorites")
    Call<List<Property>> getFavoriteProperties(@Header("Authorization") String token);

    @Headers("Accept: application/json")
    @POST("favorites/{id}")
    Call<Message> addFavoriteProperty(@Header("Authorization") String token, @Path("id") int itemId);

    @Headers("Accept: application/json")
    @DELETE("favorites/{id}")
    Call<Message> removeFavoriteProperty(@Header("Authorization") String token, @Path("id") int itemId);

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("properties")
    Call<Property> addProperty(
            @Header("Authorization") String token,
            @Field("property[price]") String price,
            @Field("property[arv]") String arv,
            @Field("property[street]") String street,
            @Field("property[city]") String city,
            @Field("property[state]") String state,
            @Field("property[zip_code]") String zip_code,
            @Field("property[nbeds]") String nbeds,
            @Field("property[nbath]") String nbath,
            @Field("property[description]") String description,
            @Field("property[sqft]") String sqft,
            @Field("property[property_category]") String property_category,
            @Field("property[year_built]") String year_built,
            @Field("property[parking]") String parking,
            @Field("property[lot_size]") String lot_size,
            @Field("property[zoning]") String zoning,
            @Field("property[PropertyType_id]") Integer property_type_id,
            @Field("property[PropertyListing_id]") Integer property_listing_id,
            @Field("property[rehab_cost]") String rehab_cost,
            @Field("property[defaultimage]") String default_img,
            @Field("property[photo_data]") String photo_data
    );

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @PUT("properties/{id}")
    Call<Property> updateProperty(
            @Header("Authorization") String token,
            @Path("id") int itemId,
            @Field("property[price]") String price,
            @Field("property[arv]") String arv,
            @Field("property[street]") String street,
            @Field("property[city]") String city,
            @Field("property[state]") String state,
            @Field("property[zip_code]") String zip_code,
            @Field("property[nbeds]") String nbeds,
            @Field("property[nbath]") String nbath,
            @Field("property[description]") String description,
            @Field("property[sqft]") String sqft,
            @Field("property[property_category]") String property_category,
            @Field("property[year_built]") String year_built,
            @Field("property[parking]") String parking,
            @Field("property[lot_size]") String lot_size,
            @Field("property[zoning]") String zoning,
            @Field("property[PropertyType_id]") Integer property_type_id,
            @Field("property[PropertyListing_id]") Integer property_listing_id,
            @Field("property[rehab_cost]") String rehab_cost,
            @Field("property[defaultimage]") String default_img,
            @Field("property[photo_data]") String photo_data
    );

    @Headers("Accept: application/json")
    @GET("properties/{id}")
    Call<Property> getProperty(@Header("Authorization") String token, @Path("id") int itemId);

    @Headers("Accept: application/json")
    @DELETE("properties/{id}")
    Call<Property> deleteProperty(@Header("Authorization") String token, @Path("id") int itemId);

    //----------------------------------USERS----------------------------------

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @PUT("users/{id}/profile")
    Call<User> updateUserProfile(
            @Header("Authorization") String token,
            @Path("id") Integer userId,
            @Field("user[first_name]") String first_name,
            @Field("user[last_name]") String last_name,
            @Field("user[phone_number]") String phone_number,
            @Field("user[about]") String about,
            @Field("user[areas]") String areas,
            @Field("user[role]") String role,
            @Field("user[city]") String city,
            @Field("user[avatar]") String avatar
    );

    @Headers("Accept: application/json")
    @GET("users/{id}/profile")
    Call<User> getUserProfile(
            @Header("Authorization") String token,
            @Path("id") Integer userId
    );

    @GET("search/users")
    @Headers("Accept: application/json")
    Call<List<User>> searchUsers(@Header("Authorization") String token, @Query("search") String name);

    //----------------------------------NETWORK----------------------------------

    @GET("network/{id}")
    @Headers("Accept: application/json")
    Call<List<Connection>> getUserConnections(@Header("Authorization") String token, @Path("id") Integer user_id);

    @GET("pendingconnections")
    @Headers("Accept: application/json")
    Call<List<Connection>> getPendingConnections(@Header("Authorization") String token);

    @GET("waitingconnections")
    @Headers("Accept: application/json")
    Call<List<Connection>> getWaitingConnections(@Header("Authorization") String token);

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("network")
    Call<Message> addConnection(@Header("Authorization") String token, @Field("user_id") Integer friend_id);

    @Headers("Accept: application/json")
    @DELETE("network/{id}")
    Call<Message> deleteConnection(@Header("Authorization") String token, @Path("id") Integer friend_id);

    @Headers("Accept: application/json")
    @PUT("network/{id}/accept")
    Call<Message> acceptConnection(@Header("Authorization") String token, @Path("id") Integer id);

    @Headers("Accept: application/json")
    @DELETE("network/{id}/delete/")
    Call<Message> rejectConnection(@Header("Authorization") String token, @Path("id") Integer itemId);

}
