package io.brickhack.mobile;

import android.net.Uri;

public class Settings {
    private final String clientID = "b0a484dfaf474fdfd43ad7867d3c70fe8d76195ee565f36a29677fdbd8a168d3";
    private final String clientSCRT = "1837394c4b13cf3954488a31afc2ec9482a33b2b7e68568280ae74edb954d756";
    private final String redirectURI = "brickhack://oauth/callback";
    public static final String LOG_TAG = "BrickHack";

    String getClientID(){
        return this.clientID;
    }

    Uri getRedirectURI(){
        return Uri.parse(this.redirectURI);
    }

    String getClientSCRT() { return this.clientSCRT; }
}
