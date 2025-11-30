package cm.bogne_stanley.money_flow.domain.service;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cm.bogne_stanley.money_flow.common.exception.BusinessException;
import cm.bogne_stanley.money_flow.common.exception.ErrorCode;

@Service
public class StorageService {
    @Value("${app.upload.dir:uploads}")
    private String UPLOAD_DIR;

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            if (file.toUri() == null) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }
            @SuppressWarnings("null")
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }


    private Path load(String filename) {
        return Paths.get(UPLOAD_DIR).resolve(filename);
    }

    @SuppressWarnings("null")
    public String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalName = file.getOriginalFilename();
            String extension = originalName != null ? originalName.substring(originalName.lastIndexOf(".")) : "";
            String newFileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(newFileName);
            file.transferTo(filePath);
            return filePath.toString().replace("\\", "/");
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_TRANSFER_ERROR, e.getMessage());
        }
    }
}
