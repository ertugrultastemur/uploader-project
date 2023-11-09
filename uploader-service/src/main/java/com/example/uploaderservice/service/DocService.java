package com.example.uploaderservice.service;

import com.example.uploaderservice.dto.DocDto;
import com.example.uploaderservice.exception.DocNotFoundException;
import com.example.uploaderservice.exception.FileIOException;
import com.example.uploaderservice.model.Doc;
import com.example.uploaderservice.repository.DocRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DocService {

    private final DocRepository docRepository;

    public DocService(DocRepository docRepository){
        this.docRepository = docRepository;
    }

    public String saveFile(MultipartFile[] files) throws FileIOException {
        try {
            for (MultipartFile file : files) {
                String docName = file.getOriginalFilename();
                Doc doc = new Doc(docName, file.getContentType(), file.getBytes());
                docRepository.save(doc);
                /*WebClient webClient = WebClient.builder().build();
                WebClient.ResponseSpec responseSpec = webClient.post()
                        .uri("http://192.168.191.160:7878/pdfread")
                        .bodyValue(doc.getData())
                        .retrieve();

                // Get the response body
                String responseBody = responseSpec.bodyToMono(String.class).block();*/

                String url = "http://192.168.96.160:7878/pdfread";


                OkHttpClient client = new OkHttpClient();


                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "file.pdf",
                                RequestBody.create(MediaType.parse(file.getContentType()), file.getBytes()))
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                try (response) {
                    if (!response.isSuccessful()) {
                        System.err.println("Request failed: " + response);
                    } else {
                        assert response.body() != null;
                        ResponseBody responseBody = response.body();
                        String responseBodyString = responseBody.string();
                        responseBody.close();
                        System.out.println(responseBodyString);
                        return responseBodyString;
                    }
                }

            }
        } catch (IOException e) {
            throw new FileIOException("File processing error: " + e.getMessage());
        }

        return null;
    }
    public DocDto getFile(int fileId){
        return docRepository.findById(fileId).map(DocDto::convert).orElseThrow(()-> new DocNotFoundException("Doc could not be found by id: " + fileId));

    }

    public List<DocDto> getAllFiles(){
        return docRepository.findAll().stream().map(DocDto::convert).collect(Collectors.toList());
    }
}
