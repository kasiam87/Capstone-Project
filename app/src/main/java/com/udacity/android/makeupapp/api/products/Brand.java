package com.udacity.android.makeupapp.api.products;

public enum Brand {
    ALMA("almay"),
    ALVA("alva"),
    ANNA_SUI("anna sui"),
    ANNABELLE("annabelle"),
    BENEFIT("benefit"),
    BOOSH("boosh"),
    BURTS_BEES("burt's bees"),
    BUTTER_LONDON("butter london"),
    CEST_MOI("c'est moi"),
    CARGO_COSMETICS("cargo cosmetics"),
    CHINA_GLAZE("china glaze"),
    CLINIQUE("clinique"),
    COASTAL_CLASSIC_CREATION("coastal classic creation"),
    COLOURPOP("colourpop"),
    COVERGIRL("covergirl"),
    DALISH("dalish"),
    DECIEM("deciem"),
    DIOR("dior"),
    DR_HAUSCHKA("dr. hauschka"),
    ELF("e.l.f."),
    ESSIE("essie"),
    FENTY("fenty"),
    GLOSSIER("glossier"),
    GREEN_PEOPLE("green people"),
    IMAN("iman"),
    LOREAL("l'oreal"),
    LOTUS_COSMETICS_USA("lotus cosmetics usa"),
    MAIAS_MINERAL_GALAXY("maia's mineral galaxy"),
    MARCELLE("marcelle"),
    MARIENATIE("marienatie"),
    MAYBELLINE("maybelline"),
    MILANI("milani"),
    MINERAL_FUSION("mineral fusion"),
    MISA("misa"),
    MISTURA("mistura"),
    MOOV("moov"),
    NUDUS("nudus"),
    NYX("nyx"),
    ORLY("orly"),
    PACIFICA("pacifica"),
    PENNY_LANE_ORGANICS("penny lane organics"),
    PHYSICIANS_FORMULA("physicians formula"),
    PIGGY_PAINT("piggy paint"),
    PURE_ANADA("pure anada"),
    REJUVA_MINERALS("rejuva minerals"),
    REVLON("revlon"),
    SALLY_BS_SKIN_YUMMIES("sally b's skin yummies"),
    SALON_PERFECT("salon perfect"),
    SANTE("sante"),
    SINFUL_COLOURS("sinful colours"),
    SMASHBOX("smashbox"),
    STILA("stila"),
    SUNCOAT("suncoat"),
    W3LLPEOPLE("w3llpeople"),
    WET_N_WILD("wet n wild"),
    ZORAH("zorah"),
    ZORAH_BIOCOSMETIQUES("zorah biocosmetiques");

    public final String brand;

    Brand(String brand){
        this.brand = brand;
    }

    @Override
    public String toString(){
        return brand;
    }

    public static Brand fromString(String str) {
        for (Brand brand : values()) {
            if (brand.toString().equalsIgnoreCase(str)) {
                return brand;
            }
        }
        return null;
    }
}
