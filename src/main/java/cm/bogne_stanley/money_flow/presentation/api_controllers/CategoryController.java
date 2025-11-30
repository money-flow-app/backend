package cm.bogne_stanley.money_flow.presentation.api_controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.bogne_stanley.money_flow.common.wrappers.APIResponse;
import cm.bogne_stanley.money_flow.common.wrappers.PaginatedData;
import cm.bogne_stanley.money_flow.domain.service.CategoryService;
import cm.bogne_stanley.money_flow.presentation.dto.request.category.CategoryRequest;
import cm.bogne_stanley.money_flow.presentation.dto.response.category.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<CategoryResponse>> createCategory(@Valid @ModelAttribute CategoryRequest categoryRequest) {
        return ResponseEntity.ok(new APIResponse<>(true, "Category created", categoryService.create(categoryRequest)));
    }

    @GetMapping
    public ResponseEntity<APIResponse<PaginatedData<CategoryResponse>>> getAllCategories(@PageableDefault(size = 3, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new APIResponse<>(true, "Categories fetched", categoryService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(new APIResponse<>(true, "Category fetched", categoryService.getById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(new APIResponse<>(true, "Category deleted", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @ModelAttribute CategoryRequest categoryRequest) {
        return ResponseEntity.ok(new APIResponse<>(true, "Category updated", categoryService.update(id, categoryRequest)));
    }
}
