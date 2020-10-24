package com.geng.clicktest;

import android.view.View;

import androidx.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventType(listenerType = View.OnClickListener.class,listenerSetter = "setOnClickListener")
public @interface OnClick {
    @IdRes int[] value();
}
