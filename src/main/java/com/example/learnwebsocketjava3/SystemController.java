package com.example.learnwebsocketjava3;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/socket")
public class SystemController {
    @GetMapping("/index/{userId}")
    public ModelAndView socket(@PathVariable String userId) {
        ModelAndView modelAndView = new ModelAndView("/socket1");
        modelAndView.addObject("userId", userId);
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping("/socket/path/{cid}")
    public Map pushToWeb(@PathVariable String cid, String message) {
        Map<String, Object> result = new HashMap<>();
        try {
            WebSocketServer.sendInfo(message, cid);
            result.put("code", cid);
            result.put("msg", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
