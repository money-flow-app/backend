package cm.bogne_stanley.money_flow.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.exception.BusinessException;
import cm.bogne_stanley.money_flow.common.exception.ErrorCode;
import cm.bogne_stanley.money_flow.data.entity.Attachment;
import cm.bogne_stanley.money_flow.data.entity.Expense;
import cm.bogne_stanley.money_flow.data.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final StorageService storageService;
    private final AttachmentRepository attachmentRepository;

    public void deleteAttachmentsByExpense(Expense expense) {
        attachmentRepository.findByExpense(expense).forEach(attachment -> {
            storageService.deleteFile(attachment.getPath());
        });
        attachmentRepository.deleteByExpense(expense);
    }

    @SuppressWarnings("null")
    public Attachment createAttachment(Expense expense, MultipartFile file) {
        String filePath = storageService.saveFile(file);
        Attachment attachment = Attachment.builder()
            .path(filePath)
            .expense(expense)
            .name(getFileName(file))
            .extension(getFileExtension(file))
            .build();
        return attachmentRepository.save(attachment);
    }

    public List<Attachment> bulkCreateAttachments(Expense expense, List<MultipartFile> files) {
        return files.stream()
            .map(file -> createAttachment(expense, file))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @SuppressWarnings("null")
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
        storageService.deleteFile(attachment.getPath());
        attachmentRepository.delete(attachment);
    }

    public void deleteAttachmentsByIds(List<Long> ids) {
        ids.forEach(this::deleteAttachment);
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return null;
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }

    private String getFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return null;
        }
        return originalFilename.substring(0, originalFilename.lastIndexOf('.'));
    }
}
