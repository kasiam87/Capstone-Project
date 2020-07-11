package com.udacity.android.makeupapp.api.products;

public enum Type {

    BLUSH("blush"),
    BRONZER("bronzer"),
    EYEBROW("eyebrow"),
    EYELINER("eyeliner"),
    EYESHADOW("eyeshadow"),
    FOUNDATION("foundation"),
    LIP_LINER("lip liner"),
    LIPSTICK("lipstick"),
    MASCARA("mascara"),
    NAIL_POLISH("nail polish");

    public String type;

    Type(String type){
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static Type fromString(String str) {
        for (Type type : values()) {
            if (type.toString().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }
}
