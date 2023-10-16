package com.example.uploaderservice.controller;


import com.example.uploaderservice.dto.DocDto;
import com.example.uploaderservice.service.DocService;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/v1/doc/")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class DocController {
    private final DocService docService;

    public DocController(DocService docService){
        this.docService = docService;
    }

    /*@GetMapping("/")
    public String get(Model model){
        List<DocDto> docs = docService.getAllFiles();
        model.addAttribute("docs", docs);
        return "doc";
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<DocDto> getById(@PathVariable int id){
        return ResponseEntity.ok(docService.getFile(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<DocDto>> getAll(){
        return ResponseEntity.ok(docService.getAllFiles());
    }

    @PostMapping("/uploadFiles")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") MultipartFile[] files){
        return ResponseEntity.ok(docService.saveFile(files));
    }

    @GetMapping("/downloadFile/{id}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable int id){
        DocDto docDto = docService.getFile(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(docDto.getDocType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + docDto.getDocName() + "\"")
                .body(new ByteArrayResource(docDto.getData()));
    }
}
