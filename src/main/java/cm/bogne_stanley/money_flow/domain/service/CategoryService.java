package cm.bogne_stanley.money_flow.domain.service;

import java.io.File;
import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.exception.BusinessException;
import cm.bogne_stanley.money_flow.common.exception.ErrorCode;
import cm.bogne_stanley.money_flow.data.entity.Category;
import cm.bogne_stanley.money_flow.data.repository.CategoryRepository;
import cm.bogne_stanley.money_flow.presentation.dto.request.category.CategoryRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category save(@NonNull CategoryRequest categoryRequest, @NonNull MultipartFile iconFile) {
        Category category = categoryRequest.toCategory();
        if (iconFile != null && !iconFile.isEmpty()) {
            category.setIcon(iconFile.getOriginalFilename());
            String filePath = "uploads/" + iconFile.getOriginalFilename();
            try {
                iconFile.transferTo(new File(filePath));
            } catch (IllegalStateException e) {
                throw new BusinessException(ErrorCode.FILE_TRANSFER_ERROR, e.getMessage());
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.FILE_TRANSFER_ERROR, e.getMessage());
            }
            category.setIcon(filePath);
        }
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        return categoryRepository.save(category);
    }
}
