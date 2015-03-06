package com.github.lemniscate.hipstack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by dave on 2/27/15.
 */
@Slf4j
@ControllerAdvice
public class ControllerConfig {

    @Inject
    private ObjectMapper objectMapper;

    @InitBinder
    public void initBinder(WebDataBinder binder, final WebRequest request){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // TODO add error handling here...

    @ExceptionHandler(HttpMessageNotReadableException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Object handleBadRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageNotReadableException e) throws IOException {
        return handle("An invalid request was sent", e, request, response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody Object handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, RuntimeException e) throws Exception {
        return handle("Authentication Error: " + e.getMessage(), e, request, response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody Object handleCatchAll(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        return handle("An unexpected error has occurred: " + e.getMessage(), e, request, response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Object handle(String message, Exception rootCause, HttpServletRequest request, HttpServletResponse response, HttpStatus status) throws IOException {
        TrackableException e = new TrackableException(message, rootCause);
        String uri = request.getRequestURI();
        log.warn("TrackableException[" + e.errorToken + "] occurred at " + uri, rootCause);

        Map<String, Object> map = ImmutableMap.<String, Object>builder()
                .put("message", message)
                .put("referenceId", e.errorToken)
                .put("serverTime", new Date())
                .build();
        return new ResponseEntity<Object>(map, status);
    }

    public class TrackableException extends RuntimeException{
        private final String errorToken = UUID.randomUUID().toString();

        public TrackableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
