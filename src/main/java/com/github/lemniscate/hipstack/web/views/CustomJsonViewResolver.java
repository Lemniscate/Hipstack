package com.github.lemniscate.hipstack.web.views;

import com.github.lemniscate.spring.crud.view.JsonViewResolver;
import com.github.lemniscate.spring.crud.web.ControllerMethod;
import com.github.lemniscate.spring.jsonviews.client.BaseView;
import org.springframework.stereotype.Component;

/**
 * Created by dave on 2/27/15.
 */
@Component
public class CustomJsonViewResolver extends JsonViewResolver {
    @Override
    public Class<? extends BaseView> resolve(ControllerMethod method, Class<?> type) {
        switch(method) {

            case GET_ALL: return JsonViews.Summary.class;

            case GET_ONE: return JsonViews.Detailed.class;

            default: return null;
        }
    }
}

