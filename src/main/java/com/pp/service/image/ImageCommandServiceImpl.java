package com.pp.service.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.pp.domain.Image;
import com.pp.domain.Member;
import com.pp.domain.Product;
import com.pp.repository.image.ImageRepository;
import com.pp.repository.member.MemberRepository;
import com.pp.repository.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCommandServiceImpl implements ImageCommandService {


    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final AmazonImageUploadService amazonImageUploadService;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    //productId와 이미지들 기반으로 만듬
    public List<Image> uploadProductImages(Long productId, List<MultipartFile> multipartFiles) throws IOException {

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));


        List<Image> savedImages = new ArrayList<>();

        //각 이미지마다 실행됨
        for (MultipartFile file : multipartFiles) {
            LocalDate givenDate;

            //이미지 판별 서비스
            try {
                Metadata originalMetadata = ImageMetadataReader.readMetadata(file.getInputStream());
                Map<?, ?> processedMetadata = extractUserMetadata(originalMetadata);

                Object capturedDate = processedMetadata.get("DateTimeOriginal"); //찍은 날짜 추출
                Object maker = processedMetadata.get("Make");  //제조사 추출
                log.info("찍은 날짜   {}", capturedDate);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                LocalDateTime givenDateTime = LocalDateTime.parse((CharSequence) capturedDate, formatter);
                givenDate = givenDateTime.toLocalDate();
                LocalDate today = LocalDate.now();
                log.info("givenDate: {},  today: {}", givenDate, today);


                if (givenDate.isEqual(today)) {
                    log.info("찍은 날짜 같음");
                } else {

                    throw new RuntimeException("오늘 찍은 사진이 아닙니다");
                }


            } catch (ImageProcessingException e) {
                // Handle ImageProcessingException
                log.error("Error processing image metadata 다음으로 넘어감: " + file.getOriginalFilename());
                e.printStackTrace(); // Log the error details
                continue;

            } catch (IOException e) {
                // Handle IOException (e.g., if there's an issue reading the file)
                log.error("파일 읽기 실패 다음으로 넘어감: " + file.getOriginalFilename());
                e.printStackTrace();
                continue;
            }


            InputStream processedInputStream = null;
            long contentLength = 0;

            Optional<Member> member = memberRepository.findById(product.getMember().getId());
            String userName = member.get().getName();
            String email = member.get().getEmail();

            String watermarkText = userName + ":" + email + ":" + givenDate.toString();
            BufferedImage watermarkImage = createTextWatermark(watermarkText);
            //워터마크
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //워터마크 적용된 파일 객체
                Thumbnails.of(file.getInputStream())
                        .scale(1.0)
                        .watermark(Positions.CENTER, watermarkImage, 0.9f) // 워터마크 위치 및 투명도
                        .toOutputStream(outputStream);
                contentLength = outputStream.size();
                processedInputStream = new ByteArrayInputStream(outputStream.toByteArray());


                log.info("워터마크 넣음");
            } catch (Exception e) {
                e.printStackTrace();
            }


            // S3에 이미지 업로드
            String imageUrl = amazonImageUploadService.uploadImage(file, processedInputStream, contentLength, "images");


            // 이미지 엔티티 생성 및 저장
            Image image = Image.builder()
                    .product(product)
                    .originFileName(file.getOriginalFilename())
                    .storedFileName(imageUrl)
                    .size((int) file.getSize())
                    .build();
            savedImages.add(imageRepository.save(image));
            log.info("이미지 db에 저장 완료 {}", image);
        }

        return savedImages;

    }


    @Override
    public void deleteProductImages(Long productId) {
        // 해당 상품의 모든 이미지 삭제
        List<Image> images = imageRepository.findByProductId(productId);

        for (Image image : images) {
            // S3에서 이미지 삭제
            amazonImageUploadService.deleteImage(image.getStoredFileName());

            // DB에서 이미지 삭제
            imageRepository.delete(image);
        }
    }


    private Map<String, String> extractUserMetadata(Metadata metadata) {
        Map<String, String> userMetadata = new HashMap<>();
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                // Customize metadata extraction as needed
                String tagName = tag.getTagName().replaceAll("[^a-zA-Z0-9]", ""); // Remove special characters
                String tagValue = tag.getDescription();

                userMetadata.put(tagName, tagValue);
                //log.info("태그 이름: {},    태그 값: {}", tagName, tagValue);
            }
        }
        //log.info("userMetadata {}", userMetadata);
        return userMetadata;
    }


    private BufferedImage createTextWatermark(String text) {
        int width = 2000;
        int height = 600;

        //1. 워터마크 이미지를 담을 BufferedImage 생성 (투명 배경)
        BufferedImage watermarkImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 2. Graphics2D 객체 생성
        Graphics2D g2d = watermarkImage.createGraphics();

        // 3. 안티앨리어싱 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 5. 텍스트 색상 및 투명도 설정
        g2d.setColor(new Color(0, 0, 0, 255)); // 흰색 반투명


        // 4. 폰트 설정 (한글 지원 폰트로 변경)
        g2d.setFont(new Font("Malgun Gothic", Font.PLAIN, 70));

        // 텍스트 위치 계산
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int x = (width - fontMetrics.stringWidth(text)) / 2;
        int y = (height - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();

        // 텍스트 그리기
        g2d.drawString(text, x, y);

        g2d.dispose();

        return watermarkImage;
    }
}