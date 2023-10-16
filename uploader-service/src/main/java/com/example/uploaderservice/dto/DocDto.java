package com.example.uploaderservice.dto;

import com.example.uploaderservice.model.Doc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocDto {

    private Integer id;

    private String docType;

    private String docName;

    private byte[] data;

    public static DocDto convert(Doc doc){
        return new DocDto(
                doc.getId(),
                doc.getDocType(),
                doc.getDocName(),
                doc.getData()
        );
    }
}
