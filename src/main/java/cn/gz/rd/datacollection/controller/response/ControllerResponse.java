package cn.gz.rd.datacollection.controller.response;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Objects;

/**
 * Controller返回给客户端的结果类
 */
public class ControllerResponse {

    /**
     * 请求是否成功
     */
    boolean result;

    /**
     * 返回的数据
     */
    Object data;

    /**
     * 用户消息，用户可以明白的消息内容。
     */
    String message;

    /**
     * 内部异常消息，给开发人员看，而不给用户看的内容。
     * 可以将异常堆栈信息放到这里面。
     */
    String errMsg;

    /**
     * 成功标示值
     */
    public static final boolean SUCCESS_TRUE = true;

    /**
     * 错误标示值
     */
    public static final boolean ERROR_FALSE = false;

    public ControllerResponse() {

    }

    public ControllerResponse(boolean result, Object data, String message){
        this.result = result;
        this.data = data;
        this.message = message;
    }

    /**
     * 设置成成功状态
     */
    public void success() {
        this.result = SUCCESS_TRUE;
    }

    public void success(String msg) {
        this.result = SUCCESS_TRUE;
        this.message = msg;
    }

    /**
     * 设置成失败状态
     */
    public void error() {
        this.result = ERROR_FALSE;
    }

    public void error(String message) {
        this.result = ERROR_FALSE;
        this.message = message;
    }

    public void error(String message, Exception e) {
        this.result = ERROR_FALSE;
        this.message = message;
        errMsg = ExceptionUtils.getRootCauseMessage(e);
    }

    public void error(Exception e) {
        this.result = ERROR_FALSE;
        this.message = "非常抱歉！内部执行出现问题，请联系管理员！";
        errMsg = ExceptionUtils.getRootCauseMessage(e);
    }


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControllerResponse response = (ControllerResponse) o;
        return result == response.result &&
                Objects.equals(data, response.data) &&
                Objects.equals(message, response.message) &&
                Objects.equals(errMsg, response.errMsg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, data, message, errMsg);
    }

    @Override
    public String toString() {
        return "ControllerResponse{" +
                "result=" + result +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
