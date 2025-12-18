package com.sep.sep_backend.friendship.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class UserSummaryDTO {

    private UUID id;
    private String username;
    private String email;
    private String avatarUrl;

    public UserSummaryDTO(){}

    public UserSummaryDTO(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        Random random = new Random();
        this.avatarUrl = fillAvatarList().get(random.nextInt(0, 8));
    }

    public UserSummaryDTO(UUID id, String username, String email, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    private ArrayList<String> fillAvatarList(){

        ArrayList<String> avatarList = new ArrayList<>();
        String URL_1 = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop";
        String URL_2 = "https://images.unsplash.com/photo-1599566150163-29194dcaad36?w=100&h=100&fit=crop";
        String URL_3 = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop";
        String URL_4 = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop";
        String URL_5 = "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=100&h=100&fit=crop";
        String URL_6 = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop";
        String URL_7 = "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=100&h=100&fit=crop";
        String URL_8 = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop";

        avatarList.add(URL_1);
        avatarList.add(URL_2);
        avatarList.add(URL_3);
        avatarList.add(URL_4);
        avatarList.add(URL_5);
        avatarList.add(URL_6);
        avatarList.add(URL_7);
        avatarList.add(URL_8);

        return avatarList;
    }
}
