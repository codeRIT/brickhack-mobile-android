package io.brickhack.mobile;

import android.net.Uri;

public class BrickHackSettings{

    private final String clientID = "b181105e8a2bcf3552eea153be952981ff6cc5d9746a5161e526302e97780cd0";
    private final String clientSCRT = "912ae062cbd507abb38191f23d4d8703d151fec09653001de4deedafa6e766b0";
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
