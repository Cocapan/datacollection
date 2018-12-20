package cn.gz.rd.datacollection.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author panxuan
 * 结果集合工具类
 */
public class ResultMapUtils {

    /**
     * 设置分页结果集
     * @param objectList 对象集合
     * @param pageNum 页码
     * @param pageSize 每页展示数
     * @return 结果集
     */
    public static Map<String, Object> getPageResultMap(List<?> objectList, int pageNum, int pageSize){
        Map<String, Object> resultMap = new HashMap<>();
        //部门总数量, 用于分页
        resultMap.put("count", objectList.size());
        //增加判断, 防止由于最后一页数目小于展示数导致集合越界
        if ((objectList.size()-(pageNum-1)*pageSize) < pageSize){
            resultMap.put("dataList", objectList.subList((pageNum-1)*pageSize, objectList.size()));
        }else {
            resultMap.put("dataList",objectList.subList(pageSize * (pageNum - 1), pageSize * (pageNum - 1) + pageSize));
        }
        return resultMap;
    }

    /**
     * 设置空结果集
     * @return 结果集
     */
    public static Map<String, Object> getEmptyResultMap(){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("dataList", new ArrayList<>());
        resultMap.put("count", 0);
        return resultMap;
    }

    /**
     * 设置结果集
     * @param objectList 对象集合
     * @return 结果集
     */
    public static Map<String, Object> getResultMap(List<?> objectList){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("dataList", objectList);
        return resultMap;
    }

    /**
     * 设置结果集
     * @param objectList 对象集合
     * @param count 集合总数
     * @return 结果集
     */
    public static Map<String, Object> getResultMap(List<?> objectList, int count){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("dataList", objectList);
        resultMap.put("count", count);
        return resultMap;
    }

    /**
     * 设置结果集
     * @param identifier 唯一标识
     * @param value 值
     * @return 结果集
     */
    public static Map<String, Object> getResultMap(String identifier, Object value){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(identifier, value);
        return resultMap;
    }

}
