package com.antigravitystudios.flppd.networking;

import android.support.annotation.NonNull;

import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.events.ConnectionAcceptedEvent;
import com.antigravitystudios.flppd.events.ConnectionAddedEvent;
import com.antigravitystudios.flppd.events.ConnectionDeletedEvent;
import com.antigravitystudios.flppd.events.ConnectionRejectedEvent;
import com.antigravitystudios.flppd.events.ConnectionsListReceivedEvent;
import com.antigravitystudios.flppd.events.ConnectionsPendingListReceivedEvent;
import com.antigravitystudios.flppd.events.ConnectionsWaitingListReceivedEvent;
import com.antigravitystudios.flppd.events.LoginEvent;
import com.antigravitystudios.flppd.events.LoginFacebookEvent;
import com.antigravitystudios.flppd.events.ProfileReceivedEvent;
import com.antigravitystudios.flppd.events.ProfileUpdateEvent;
import com.antigravitystudios.flppd.events.PropertiesListReceivedEvent;
import com.antigravitystudios.flppd.events.PropertyAddEvent;
import com.antigravitystudios.flppd.events.PropertyDeletedEvent;
import com.antigravitystudios.flppd.events.PropertyReceivedEvent;
import com.antigravitystudios.flppd.events.PropertyUpdateEvent;
import com.antigravitystudios.flppd.events.RecoverEvent;
import com.antigravitystudios.flppd.events.SearchUserResultEvent;
import com.antigravitystudios.flppd.events.SignUpEvent;
import com.antigravitystudios.flppd.models.Connection;
import com.antigravitystudios.flppd.models.Message;
import com.antigravitystudios.flppd.models.PropertiesFilter;
import com.antigravitystudios.flppd.models.Property;
import com.antigravitystudios.flppd.models.User;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Interactor implements ApiService {

    private final ApiService apiService;
    private final EventBus bus = EventBus.getDefault();

    public Interactor(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Call<User> login(String email, String password) {
        Call<User> call = apiService.login(email, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                bus.post(new LoginEvent(response));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                bus.post(new LoginEvent(t));
            }
        });
        return call;
    }

    @Override
    public Call<User> facebook_auth(String facebookToken) {
        Call<User> call = apiService.facebook_auth(facebookToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                bus.post(new LoginFacebookEvent(response));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                bus.post(new LoginFacebookEvent(t));
            }
        });
        return call;
    }

    @Override
    public Call<Message> recover(String phone_number) {
        Call<Message> call = apiService.recover("+1 " + phone_number);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                bus.post(new RecoverEvent(response));
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                bus.post(new RecoverEvent(t));
            }
        });
        return call;
    }

    @Override
    public Call<User> signup(String email, String password, String first_name, String last_name, String phone_number) {
        Call<User> call = apiService.signup(email, password, first_name, last_name, "+1 " + phone_number);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                bus.post(new SignUpEvent(response));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                bus.post(new SignUpEvent(t));
            }
        });
        return call;
    }

    @Override
    public Call<Message> logout(String token) {
        Call<Message> call = apiService.logout(token);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
        return call;
    }

    @Override
    public Call<Message> setpush(String token, String device_id, String device_token) {
        Call<Message> call = apiService.setpush(token, device_id, device_token);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
        return call;
    }

    @Override
    public Call<List<Property>> getProperties(String token, Integer userId, Integer limit, String type, String city, String state, String zipcode, String price_min, String price_max) {
        final PropertiesListReceivedEvent event = new PropertiesListReceivedEvent(userId == null ? "all" : null, userId);

        Call<List<Property>> call = apiService.getProperties(token, userId, limit, type, city, state, zipcode, price_min, price_max);
        call.enqueue(new Callback<List<Property>>() {
            @Override
            public void onResponse(@NonNull Call<List<Property>> call, @NonNull Response<List<Property>> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<List<Property>> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    public Call<List<Property>> getProperties(Integer userId, Integer limit, PropertiesFilter filter) {
        if (filter == null) {
            filter = new PropertiesFilter();
        }
        return getProperties(SPreferences.getToken(), userId, limit, filter.getType(), filter.getCity(), filter.getState(), filter.getZipcode(), filter.getPrice_min(), filter.getPrice_max());
    }

    @Override
    public Call<List<Property>> getFavoriteProperties(String token) {
        final PropertiesListReceivedEvent event = new PropertiesListReceivedEvent("starred", null);

        Call<List<Property>> call = apiService.getFavoriteProperties(token);
        call.enqueue(new Callback<List<Property>>() {
            @Override
            public void onResponse(@NonNull Call<List<Property>> call, @NonNull Response<List<Property>> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<List<Property>> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<Message> addFavoriteProperty(String token, int itemId) {
        token = SPreferences.getToken();
        Call<Message> call = apiService.addFavoriteProperty(token, itemId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
            }
        });
        return call;
    }

    @Override
    public Call<Message> removeFavoriteProperty(String token, int itemId) {
        token = SPreferences.getToken();
        Call<Message> call = apiService.removeFavoriteProperty(token, itemId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
            }
        });
        return call;
    }

    @Override
    public Call<Property> addProperty(String token, String price, String arv, String street, String city, String state, String zip_code, String nbeds, String nbath, String description, String sqft, String property_category, String year_built, String parking, String lot_size, String zoning, Integer property_type_id, Integer property_listing_id, String rehab_cost, String default_img, String photo_data) {
        final PropertyAddEvent event = new PropertyAddEvent();

        Call<Property> call = apiService.addProperty(token, price, arv, street, city, state, zip_code, nbeds, nbath, description, sqft, property_category, year_built, parking, lot_size, zoning, property_type_id, property_listing_id, rehab_cost, default_img, photo_data);
        call.enqueue(new Callback<Property>() {
            @Override
            public void onResponse(@NonNull Call<Property> call, @NonNull Response<Property> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<Property> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    public Call<Property> addProperty(Property property) {
        String token = SPreferences.getToken();
        return addProperty(
                token,
                property.getPrice(),
                property.getArv(),
                property.getStreet(),
                property.getCity(),
                property.getState(),
                property.getZip_code(),
                property.getNbeds(),
                property.getNbath(),
                property.getDescription(),
                property.getSqft(),
                property.getProperty_category(),
                property.getYear_built(),
                property.getParking(),
                property.getLot_size(),
                property.getZoning(),
                property.getProperty_type_id(),
                property.getProperty_listing_id(),
                property.getRehab_cost(),
                property.getDefault_img(),
                property.getPhoto_data()
        );
    }

    @Override
    public Call<Property> updateProperty(String token, int itemId, String price, String arv, String street, String city, String state, String zip_code, String nbeds, String nbath, String description, String sqft, String property_category, String year_built, String parking, String lot_size, String zoning, Integer property_type_id, Integer property_listing_id, String rehab_cost, String default_img, String photo_data) {
        final PropertyUpdateEvent event = new PropertyUpdateEvent();

        Call<Property> call = apiService.updateProperty(token, itemId, price, arv, street, city, state, zip_code, nbeds, nbath, description, sqft, property_category, year_built, parking, lot_size, zoning, property_type_id, property_listing_id, rehab_cost, default_img, photo_data);
        call.enqueue(new Callback<Property>() {
            @Override
            public void onResponse(@NonNull Call<Property> call, @NonNull Response<Property> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<Property> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    public Call<Property> updateProperty(Property property) {
        String token = SPreferences.getToken();
        return updateProperty(
                token,
                property.getId(),
                property.getPrice(),
                property.getArv(),
                property.getStreet(),
                property.getCity(),
                property.getState(),
                property.getZip_code(),
                property.getNbeds(),
                property.getNbath(),
                property.getDescription(),
                property.getSqft(),
                property.getProperty_category(),
                property.getYear_built(),
                property.getParking(),
                property.getLot_size(),
                property.getZoning(),
                property.getProperty_type_id(),
                property.getProperty_listing_id(),
                property.getRehab_cost(),
                property.getDefault_img(),
                property.getPhoto_data()
        );
    }

    @Override
    public Call<Property> getProperty(String token, int itemId) {
        final PropertyReceivedEvent event = new PropertyReceivedEvent(itemId);

        Call<Property> call = apiService.getProperty(token, itemId);
        call.enqueue(new Callback<Property>() {
            @Override
            public void onResponse(@NonNull Call<Property> call, @NonNull Response<Property> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<Property> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    public Call<Property> getProperty(Property property) {
        String token = SPreferences.getToken();
        return getProperty(
                token,
                property.getId()
        );
    }

    @Override
    public Call<Property> deleteProperty(String token, int itemId) {
        final PropertyDeletedEvent event = new PropertyDeletedEvent(itemId);

        Call<Property> call = apiService.deleteProperty(token, itemId);
        call.enqueue(new Callback<Property>() {
            @Override
            public void onResponse(@NonNull Call<Property> call, @NonNull Response<Property> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<Property> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    public Call<Property> deleteProperty(Property property) {
        String token = SPreferences.getToken();
        return deleteProperty(
                token,
                property.getId()
        );
    }

    @Override
    public Call<User> updateUserProfile(String token, Integer userId, String first_name, String last_name, String phone_number, String about, String areas, String role, String city, String avatar) {
        Call<User> call = apiService.updateUserProfile(token, userId, first_name, last_name, phone_number, about, areas, role, city, avatar);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                bus.post(new ProfileUpdateEvent(response));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                bus.post(new ProfileUpdateEvent(t));
            }
        });
        return call;
    }

    public Call<User> updateUserProfile(User user) {
        String token = (!SPreferences.getToken().isEmpty()) ? SPreferences.getToken() : user.getAuthToken();
        return updateUserProfile(
                token,
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                "+1 " + user.getPhoneNumber(),
                user.getAbout(),
                user.getAreas(),
                user.getRole(),
                user.getCity(),
                user.getAvatar()
        );
    }

    @Override
    public Call<User> getUserProfile(String token, Integer userId) {
        final ProfileReceivedEvent event = new ProfileReceivedEvent(userId);

        Call<User> call = apiService.getUserProfile(token, userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<List<User>> searchUsers(String token, String name) {
        final SearchUserResultEvent event = new SearchUserResultEvent(name);

        Call<List<User>> call = apiService.searchUsers(token, name);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<List<Connection>> getUserConnections(String token, final Integer user_id) {
        final ConnectionsListReceivedEvent event = new ConnectionsListReceivedEvent(user_id);

        Call<List<Connection>> call = apiService.getUserConnections(token, user_id);
        call.enqueue(new Callback<List<Connection>>() {
            @Override
            public void onResponse(@NonNull Call<List<Connection>> call, @NonNull Response<List<Connection>> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<List<Connection>> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<List<Connection>> getPendingConnections(String token) {
        final ConnectionsPendingListReceivedEvent event = new ConnectionsPendingListReceivedEvent();

        Call<List<Connection>> call = apiService.getPendingConnections(token);
        call.enqueue(new Callback<List<Connection>>() {
            @Override
            public void onResponse(@NonNull Call<List<Connection>> call, @NonNull Response<List<Connection>> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<List<Connection>> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<List<Connection>> getWaitingConnections(String token) {
        final ConnectionsWaitingListReceivedEvent event = new ConnectionsWaitingListReceivedEvent();

        Call<List<Connection>> call = apiService.getWaitingConnections(token);
        call.enqueue(new Callback<List<Connection>>() {
            @Override
            public void onResponse(@NonNull Call<List<Connection>> call, @NonNull Response<List<Connection>> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<List<Connection>> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<Message> addConnection(String token, Integer friend_id) {
        final ConnectionAddedEvent event = new ConnectionAddedEvent(friend_id);

        Call<Message> call = apiService.addConnection(token, friend_id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<Message> deleteConnection(String token, Integer friend_id) {
        final ConnectionDeletedEvent event = new ConnectionDeletedEvent(friend_id);

        Call<Message> call = apiService.deleteConnection(token, friend_id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<Message> acceptConnection(String token, Integer id) {
        final ConnectionAcceptedEvent event = new ConnectionAcceptedEvent(id);

        Call<Message> call = apiService.acceptConnection(token, id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

    @Override
    public Call<Message> rejectConnection(String token, Integer itemId) {
        final ConnectionRejectedEvent event = new ConnectionRejectedEvent(itemId);

        Call<Message> call = apiService.rejectConnection(token, itemId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                event.setResponse(response);
                bus.post(event);
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                event.setT(t);
                bus.post(event);
            }
        });
        return call;
    }

}
