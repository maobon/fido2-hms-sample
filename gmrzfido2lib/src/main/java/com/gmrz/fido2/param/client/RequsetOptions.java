package com.gmrz.fido2.param.client;

import java.util.Map;

public abstract class RequsetOptions {

    public static final String HW_OPTIONS_LINK = "asset_link";

    protected Map<String,Object> options;

    protected  int requestId;

    protected String origin;

    public String getOrigin(){
        return origin;
    }

    public void setOrigin(String origin){
        this.origin = origin;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Map<String, Object> getHwOptions() {
        return options;
    }

    public void setHwOptions(Map<String, Object> getHwOptions) {
        this.options = getHwOptions;
    }
}
