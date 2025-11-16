package cm.bogne_stanley.money_flow.common.wrappers;

public record APIResponse<T>(
    boolean success,
    String message,
    T data
) {
    
}
