package cn.gz.rd.datacollection.controller.request;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Peng Xiaodong
 */
public class UpdateUsableRequest {

    private List<String> subjectCodes;

    private Boolean usable;

    public List<String> getSubjectCodes() {
        return subjectCodes;
    }

    public void setSubjectCodes(List<String> subjectCodes) {
        this.subjectCodes = subjectCodes;
    }

    public Boolean getUsable() {
        return usable;
    }

    public void setUsable(Boolean usable) {
        this.usable = usable;
    }

    @Override
    public String toString() {
        return "UpdateUsableRequest{" +
                "subjectCodes=" + StringUtils.join(subjectCodes) +
                ", isUsable=" + usable +
                '}';
    }
}
