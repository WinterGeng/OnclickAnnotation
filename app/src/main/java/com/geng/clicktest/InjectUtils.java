package com.geng.clicktest;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectUtils {
    public static void getView(Activity activity) {
        Class<? extends Activity> aClass = activity.getClass();
        //获取此类的所有成员
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field filed : declaredFields) {
            //判断属性是否被注解声明
            if (filed.isAnnotationPresent(Id.class)) {
                Id annotation = filed.getAnnotation(Id.class);
                int value = annotation.value();
                View viewById = activity.findViewById(value);
                filed.setAccessible(true);//设置访问权限，运行操作previte属性
                try {
                    //为反射赋值
                    filed.set(activity, viewById);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void onClick(Activity activity) {
        Class<? extends Activity> aClass = activity.getClass();
        //获取此类的所有方法
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            //获取方法上的所有注解
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                //获取注解类型
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType.isAnnotationPresent(EventType.class)) {
                    EventType eventType = annotationType.getAnnotation(EventType.class);
                    //onClickListener.class
                    Class listenerType = eventType.listenerType();
                    //setOnClickListener
                    String listenerSetter = eventType.listenerSetter();
                    try {
                        //第一个参数是接受者，也就是这里的注解对象,也就是调用annotation的value方法
                        Method valueMethod = annotationType.getDeclaredMethod("value");
                        int[] viewIds = (int[]) valueMethod.invoke(annotation);
                        method.setAccessible(true);
                        ListenerInvocationHandler handler = new ListenerInvocationHandler(activity, method);
                        Object listenerProxy = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);
                        for (int viewId : viewIds) {
                            // 获得当前activity的view（赋值）
                            View view = activity.findViewById(viewId);
                            Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                            setter.invoke(view,listenerProxy);
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class ListenerInvocationHandler<T> implements InvocationHandler {

        private Method method;
        private T target;

        public ListenerInvocationHandler(T target, Method method) {
            this.target = target;
            this.method = method;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return this.method.invoke(target, args);
        }
    }
}
