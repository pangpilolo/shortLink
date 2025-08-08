package com.pangpi.shortlink.project.service;

/**
 * url标题接口层
 */
public interface UrlTitleService {

    /**
     * 根据url获取对应网站的title
     * @param url 网站url
     * @return title
     */
    String getTitleByUrl(String url);
}
