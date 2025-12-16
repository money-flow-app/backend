package cm.bogne_stanley.money_flow.common.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cm.bogne_stanley.money_flow.data.entity.Attachment;
import cm.bogne_stanley.money_flow.presentation.dto.response.expense.AttachmentResponse;

@Component
public class AttachmentMapper {
    @Value("${app.url}")
    private String appUrl;

    public AttachmentResponse toResponse(Attachment attachment) {
        String path = attachment.getPath() != null ? (attachment.getPath().startsWith("http") ? attachment.getPath() : appUrl + "/" + attachment.getPath()) : null;
        return new AttachmentResponse(
            attachment.getId(),
            path,
            attachment.getExtension()
        );
    }
    
}
