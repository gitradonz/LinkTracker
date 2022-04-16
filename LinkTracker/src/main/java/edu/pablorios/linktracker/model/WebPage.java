package edu.pablorios.linktracker.model;

import java.util.List;

public class WebPage {

    // Atributos de nuestra clase WebPage
    List<String> enlaces;
    String web, url;

    // Constructor
    public WebPage(String web, String Url){
        this.web =web;
        this.url =Url;
    }

    // Getters y settes
    public String getWeb() {
        return web;
    }
    public String getUrl() {
        return url;
    }

    public void setEnlaces(List<String> enlaces) {
        this.enlaces = enlaces;
    }
    public List<String> getEnlaces() {
        return enlaces;
    }
}
