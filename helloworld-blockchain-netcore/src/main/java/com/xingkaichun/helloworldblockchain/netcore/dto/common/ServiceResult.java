package com.xingkaichun.helloworldblockchain.netcore.dto.common;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ServiceResult<T> {

    private ServiceCode serviceCode;
    private String message;
    private T result;

    public ServiceResult(ServiceCode serviceCode, String message, T result) {
        this.serviceCode = serviceCode;
        this.message = message;
        this.result = result;
    }

    public static<T> ServiceResult<T> createSuccessServiceResult(String message, T result){
        return new ServiceResult(ServiceCode.SUCCESS,message,result);
    }
    public static<T> ServiceResult<T> createSuccessServiceResult(String message){
        return new ServiceResult(ServiceCode.SUCCESS,message,null);
    }
    public static<T> ServiceResult createFailServiceResult(String message){
        return new ServiceResult(ServiceCode.FAIL,message,null);
    }
    public static boolean isSuccess(ServiceResult serviceResult){
        return serviceResult!=null && serviceResult.getServiceCode() != null && serviceResult.getServiceCode() == ServiceCode.SUCCESS;
    }




    //region get set

    public ServiceCode getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(ServiceCode serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    //endregion
}
