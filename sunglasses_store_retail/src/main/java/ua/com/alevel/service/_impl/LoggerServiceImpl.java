package ua.com.alevel.service._impl;

import org.springframework.stereotype.Service;
import ua.com.alevel.service.LoggerService;

@Service("loggerService")
public class LoggerServiceImpl implements LoggerService {
    @Override
    public void log() {
        System.out.println("LoggerServiceImpl.log");
    }
}
