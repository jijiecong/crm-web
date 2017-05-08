package com.meiren.common.context;

import com.meiren.acl.service.entity.AclUserEntity;
import com.meiren.web.login.BaseController;

/**
 * Created by admin on 2017/1/17.
 */
public class CurrentContext {

    private static ThreadLocal<AclUserEntity> currentUser = new ThreadLocal<AclUserEntity>();

    private static ThreadLocal<Boolean> modelAndView = new ThreadLocal<Boolean>();

    public static void remove() {
        currentUser.remove();
    }

    public static void set(AclUserEntity user) {
        currentUser.set(user);
    }

    public static AclUserEntity get() {
        return currentUser.get();
    }

    public static void removeModelAndView() {
        modelAndView.remove();
    }

    public static void setModelAndView(Boolean equalModelAndView) {
        modelAndView.set(equalModelAndView);
    }

    public static Boolean getModelAndView() {
        return modelAndView.get();
    }
}
