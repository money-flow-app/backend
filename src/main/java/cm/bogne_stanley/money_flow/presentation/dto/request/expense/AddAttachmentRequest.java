package cm.bogne_stanley.money_flow.presentation.dto.request.expense;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidFile;

public record AddAttachmentRequest(
    @ValidFile(message = "Attachments must be valid files", optional = false)
    List<MultipartFile> attachments
)  {

}
