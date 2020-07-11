package com.udacity.android.makeupapp.api.products;

public enum Tag {

    CANADIAN("Canadian"),
    CERTCLEAN("CertClean"),
    CHEMICAL_FREE("Chemical Free"),
    DAIRY_FREE("Dairy Free"),
    EWG_VERIFIED("EWG Verified"),
    ECOCERT("EcoCert"),
    FAIR_TRADE("Fair Trade"),
    GLUTEN_FREE("Gluten Free"),
    HYPOALLERGENIC("Hypoallergenic"),
    NATURAL("Natural"),
    NO_TALC("No Talc"),
    NON_GMO("Non-GMO"),
    ORGANIC("Organic"),
    PEANUT_FREE_PRODUCT("Peanut Free Product"),
    SUGAR_FREE("Sugar Free"),
    USDA_ORGANIC("USDA Organic"),
    VEGAN("Vegan"),
    ALCOHOL_FREE("alcohol free"),
    CRUELTY_FREE("cruelty free"),
    OIL_FREE("oil free"),
    PURPICKS("purpicks"),
    SILICONE_FREE("silicone free"),
    WATER_FREE("water free");

    public String tag;

    Tag(String tag){
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }

    public static Tag fromString(String str) {
        for (Tag tag : values()) {
            if (tag.toString().equalsIgnoreCase(str)) {
                return tag;
            }
        }
        return null;
    }
}
