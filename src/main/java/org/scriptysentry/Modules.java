package org.scriptysentry;

public enum Modules {
    
    CORE("core"),
    FRAMEWORK("framework"),
    LOGIC("logic"),
    MODULO("modulo"),
    WEB("web"),
    ANDROID("android"),
    SYNC("sync"),
    PEDIDOENGINE("pedidoengine"),
    PCSISTEMASGRADE("pcsistemasgrade"),
    CLIENTE("CLIENTE");

    private final String module;

    Modules(String module) {
        this.module = module;
    }

    public String getModule(){
        return this.module;
    }
}
