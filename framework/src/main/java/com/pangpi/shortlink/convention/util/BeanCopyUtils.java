package com.pangpi.shortlink.convention.util;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pangpi
 */
public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    public static <V,O> List<V> copyBeanList(List<O> list,Class<V> clazz) {
        return list.stream().map(o -> copyBean(o,clazz)).collect(Collectors.toList());
    }


    public static <V> V copyBean(Object source,Class<V> clazz) {
        //创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            //实现属性拷贝
            BeanUtils.copyProperties(source,result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回目标对象
        return result;
    }

}
