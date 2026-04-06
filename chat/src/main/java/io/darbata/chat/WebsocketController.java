package io.darbata.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class WebsocketController {

    @MessageMapping("/hello")
    @SendTo("/topic")
    public String handle(String message) {
        String output = "[" + getTimestamp() + ": " + message + "]";
        System.out.println(output);
        return output;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date());
    }
}