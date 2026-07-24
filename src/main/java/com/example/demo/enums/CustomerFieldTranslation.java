package com.example.demo.enums;

public enum CustomerFieldTranslation {
    CITY("city", "cidade"),
    NAME("name", "nome"),
    ADDRESS("address", "endereço"),
    PHONE("phone", "telefone");

    private final String original;
    private final String translated;

    CustomerFieldTranslation(String original, String translated) {
        this.original = original;
        this.translated = translated;
    }

    public String getOriginal() {
        return original;
    }

    public String getTranslated() {
        return translated;
    }

    public static String translateMessage(String message) {
        String result = message;
        for (CustomerFieldTranslation ft : CustomerFieldTranslation.values()) {
            result = result.replace(ft.getOriginal(), ft.getTranslated());
        }
        return result;
    }
}

