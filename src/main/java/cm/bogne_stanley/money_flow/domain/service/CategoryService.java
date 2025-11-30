package cm.bogne_stanley.money_flow.domain.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import cm.bogne_stanley.money_flow.common.exception.BusinessException;
import cm.bogne_stanley.money_flow.common.exception.ErrorCode;
import cm.bogne_stanley.money_flow.common.mappers.CategoryMapper;
import cm.bogne_stanley.money_flow.common.mappers.PaginationMapper;
import cm.bogne_stanley.money_flow.common.wrappers.PaginatedData;
import cm.bogne_stanley.money_flow.data.entity.Category;
import cm.bogne_stanley.money_flow.data.entity.User;
import cm.bogne_stanley.money_flow.data.repository.CategoryRepository;
import cm.bogne_stanley.money_flow.presentation.dto.request.category.CategoryRequest;
import cm.bogne_stanley.money_flow.presentation.dto.response.category.CategoryResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    @Value("${app.upload.dir:uploads}")
    private String UPLOAD_DIR;
    private final CategoryMapper categoryMapper;
    private final PaginationMapper paginationMapper;


    @SuppressWarnings("null")
    public CategoryResponse create(CategoryRequest categoryRequest) {

        if (categoryRepository.findByNameAndUser(categoryRequest.name(), getCurrentUser()).isPresent()) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        
        Category category = Category.builder()
            .name(categoryRequest.name())
            .description(categoryRequest.description())
            .icon(categoryRequest.icon() != null ? storageService.saveFile(categoryRequest.icon()) : null)
            .user(getCurrentUser())
            .build();
        
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public PaginatedData<CategoryResponse> getAll(Pageable pageable) {
        PaginatedData<Category> paginatedData = paginationMapper.toPaginatedData(categoryRepository.findByUser(getCurrentUser(), pageable));
        return new PaginatedData<>(
            paginatedData.content().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList()),
            paginatedData.metadata()
        );
    }

    public CategoryResponse getById(Long id) {
        return categoryMapper.toResponse(categoryRepository.findByIdAndUser(id, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND)));
    }

    public CategoryResponse update(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findByIdAndUser(id, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            
        if (categoryRepository.findByNameAndUser(categoryRequest.name(), getCurrentUser()).isPresent() && !categoryRepository.findByNameAndUser(categoryRequest.name(), getCurrentUser()).get().getId().equals(id)) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        category.setName(categoryRequest.name());
        category.setDescription(categoryRequest.description());
        category.setIcon(categoryRequest.icon() != null ? storageService.saveFile(categoryRequest.icon()) : category.getIcon());
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @SuppressWarnings("null")
    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndUser(id, getCurrentUser())
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
