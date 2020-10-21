package com.ford.turbo.aposb.common.authsupport.localization;

public class Configs {

    public enum SupportedLanguages {
        EN("EN"),
        FR("FR"),
        ZH_HANS("ZH-HANS");

        private String text;

        SupportedLanguages(String text) { this.text = text.toUpperCase(); }

        public String getText() {
            return this.text.toUpperCase();
        }

        public static SupportedLanguages fromString(String text) {
            if (text != null) {
                for (SupportedLanguages language : SupportedLanguages.values()) {
                    if (text.equalsIgnoreCase(language.text)) {
                        return language;
                    }
                }
            }
            return null;
        }
    }

    public enum SupportedCountries {
        CAN("CAN"),
        USA("USA"),
        CHN("CHN");

        private String text;

        SupportedCountries(String text) {
            this.text = text.toUpperCase();
        }

        public String getText() { return this.text.toUpperCase(); }

        public static SupportedCountries fromString(String text) {
            if (text != null) {
                for (SupportedCountries language : SupportedCountries.values()) {
                    if (text.equalsIgnoreCase(language.text)) {
                        return language;
                    }
                }
            }
            return null;
        }
    }

    public enum SupportedRegions {
        CA("CA"),
        US("US");

        private String text;

        SupportedRegions(String text) {
            this.text = text.toUpperCase();
        }

        public String getText() { return this.text.toUpperCase(); }

        public static SupportedRegions fromString(String text) {
            if (text != null) {
                for (SupportedRegions regions : SupportedRegions.values()) {
                    if (text.equalsIgnoreCase(regions.text)) {
                        return regions;
                    }
                }
            }
            return null;
        }
    }
}
