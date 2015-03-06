package com.github.lemniscate.hipstack.web.views;

import com.github.lemniscate.spring.jsonviews.client.BaseView;

/**
 * Created by dave on 2/27/15.
 */
public interface JsonViews {

    public interface Summary extends BaseView {}
    public interface Detailed extends Summary {}
    public interface SystemLevel extends Detailed {}
    
}
