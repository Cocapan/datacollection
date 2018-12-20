package cn.gz.rd.datacollection.controller.converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

/**
 * 将String转换成Date
 * @author Peng Xiaodong
 */
@Component
public class StringToDateConverter implements Converter<String, Date> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;

        } else if (StringUtils.equalsIgnoreCase(source, "null")) {
            return null;

        } else {
            try {
                return DateUtils.parseDate(source,
                        "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
            } catch (ParseException e) {
                logger.error("时间格式转换异常， 时间:" + source, e);
            }
            return null;
        }

    }
}
