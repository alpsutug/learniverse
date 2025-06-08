package com.springboot.work.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;


import java.util.Locale;

@Component
@RequiredArgsConstructor
public class TranslatorService {
    private final MessageSource messageSource;

    public String toLocale(String code, String... args) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(code, args, locale);
        return StringEscapeUtils.unescapeJava(message);

    }
}
