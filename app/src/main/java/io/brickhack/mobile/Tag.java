package io.brickhack.mobile;

import android.support.annotation.NonNull;

public class Tag {

    Integer id;
    String name;

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
