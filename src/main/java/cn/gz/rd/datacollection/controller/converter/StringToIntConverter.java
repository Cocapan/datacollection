package cn.gz.rd.datacollection.controller.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 将String转换成Integer
 * @author Peng Xiaodong
 */
@Component
public class StringToIntConverter implements Converter<String, Integer> {

    @Override
    public Integer convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;

        } else if (StringUtils.equalsIgnoreCase(source, "null")) {
            return null;

        } else if (StringUtils.isNumeric(source)) {
            return Integer.valueOf(source);

        }
        return null;
    }

}
