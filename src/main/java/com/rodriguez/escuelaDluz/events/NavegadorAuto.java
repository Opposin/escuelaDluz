package com.rodriguez.escuelaDluz.events;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NavegadorAuto {

    @EventListener(ApplicationReadyEvent.class)
    public void abrirNavegador() {
    	 String url = "http://localhost:8080";
    	    abrirNavegador(url);
    }
    
    public static void abrirNavegador(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime runtime = Runtime.getRuntime();

            if (os.contains("win")) {
                runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                runtime.exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                runtime.exec("xdg-open " + url);
            } else {
                System.err.println("Sistema operativo no soportado.");
            }
        } catch (Exception e) {
            System.err.println("Error al abrir el navegador: " + e.getMessage());
        }
    }
}