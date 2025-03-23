package org.scriptysentry;

public enum Libraries {
    JQUERY("jquery"),
    SWEETALERT2("sweetalert2"),
    CKEDITOR("ckeditor"),
    SELECT2("select2"),
    SLICKGRID("slickgrid"),
    TOOLTIPSTER("tooltipster"),
    SELECTIZE("selectize"),
    KENDO("kendo"),
    AMCHARTS("amcharts"),
    RICHMARKER("richmarker");

    private final String name;

    Libraries(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
