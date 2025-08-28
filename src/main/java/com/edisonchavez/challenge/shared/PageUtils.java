package com.edisonchavez.challenge.shared;

import java.util.Locale;

public final class PageUtils {
    private PageUtils(){

    }
    public static int normPage(Integer p){
        return (p==null||p<0)?0:p;
    }
    public static int normSize(Integer s){
        return (s==null||s<=0)?10:s;
    }

    public static <T> PageResult<T> toPage(java.util.List<T> all, Integer page, Integer size){
        int p = normPage(page), z = normSize(size);
        int total = all.size();
        int from = Math.min(p*z, total), to = Math.min(from+z, total);
        return new PageResult<>(p, z, total, (int) Math.ceil(total/(double)z), all.subList(from, to));
    }
    public static boolean matchesIdByUrl(String url, String id){
        return id != null && url != null && url.endsWith("/" + id);
    }
    public static boolean containsIgnoreCase(String s, String q){
        return s != null && q != null && s.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT));
    }
}