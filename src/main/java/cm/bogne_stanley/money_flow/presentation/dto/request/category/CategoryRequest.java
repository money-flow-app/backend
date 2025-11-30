package cm.bogne_stanley.money_flow.presentation.dto.request.category;

import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidFile;
import cm.bogne_stanley.money_flow.common.custom_validator.annotations.ValidImageFile;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank(message = "Name is required")
    String name,
    @NotBlank(message = "Description is required")
    String description,
    @ValidFile(message = "Invalid file", optional = true)
    @ValidImageFile(message = "Invalid image file", optional = true)
    MultipartFile icon
) {

}
