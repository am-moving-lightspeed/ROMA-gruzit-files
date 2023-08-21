package com.example.demo;

import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
@RestController
@RequestMapping("/files-management")
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @PostMapping(value = "/post", consumes = MediaType.APPLICATION_PDF_VALUE)
  public HttpEntity<Void> postPdfFile(@RequestBody byte[] file)
  throws IOException {

    Path path = Paths.get("file.pdf");
    if (!Files.exists(path)) {
      path = Files.createFile(path);
    }
    try (var outputStream = Files.newOutputStream(path, WRITE)) {
      outputStream.write(file);
    }
    catch (Exception e) {
      // poh
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/post-form", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public HttpEntity<Void> postPdfFileAsForm(@RequestPart MultipartFile file)
  throws IOException {

    Path path = Paths.get("file.pdf");
    if (!Files.exists(path)) {
      path = Files.createFile(path);
    }
    try (var outputStream = Files.newOutputStream(path, WRITE)) {
      outputStream.write(file.getBytes());
    }
    catch (Exception e) {
      // poh
    }
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/get-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> getPdfFile() {

    Path path = Paths.get("file.pdf");
    if (!Files.exists(path)) {
      return ResponseEntity.noContent().build();
    }

    try (var inputStream = new FileInputStream("file.pdf")) {
      byte[] content = inputStream.readAllBytes();
      return ResponseEntity.ok(content);
    }
    catch (Exception e) {
      // poh
    }
    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/get-any", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> getAnyFile() {

    Path path = Paths.get("file.pdf");
    if (!Files.exists(path)) {
      return ResponseEntity.noContent().build();
    }

    try (var inputStream = new FileInputStream("file.pdf")) {
      byte[] content = inputStream.readAllBytes();
      var headers = new MultiValueMapAdapter<>(
          Map.of("X-Content-Type-Options", List.of("nosniff"))
      );
      return ResponseEntity.ok()
                           .headers(new HttpHeaders(headers))
                           .body(content);
    }
    catch (Exception e) {
      // poh
    }
    return ResponseEntity.noContent().build();
  }
}
