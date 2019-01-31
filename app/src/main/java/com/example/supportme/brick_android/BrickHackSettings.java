package com.example.supportme.brick_android;

import android.net.Uri;

public class BrickHackSettings{

    private final String environment = "https://staging.brickhack.io";
    private final String clientID = "a46ad487beade18ee2868fb9b6a6de69950f3a5bd7b2d5eb3fb62e35f53c120e";
    private final String redirectURI = "brickhack://oauth/callback";

    String getAuthorizationRoute(){
        String authorizationRoute = "/oauth/authorize";
        return this.environment + authorizationRoute;
    }

    String getTokenRoute(){
        String tokenRoute = "/oauth/token";
        return this.environment + tokenRoute;
    }

    String getClientID(){
        return this.clientID;
    }

    Uri getRedirectURI(){
        return Uri.parse(this.redirectURI);
    }
}
