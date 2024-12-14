package com.pp.service.image;

import com.pp.domain.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageCommandService {
    List<Image> uploadProductImages(Long productId, List<MultipartFile> images) throws IOException;
    void deleteProductImages(Long productId);
}
