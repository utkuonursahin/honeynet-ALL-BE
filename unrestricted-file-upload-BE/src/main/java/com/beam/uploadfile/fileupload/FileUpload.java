package com.beam.uploadfile.fileupload;

import com.beam.uploadfile.base.Base;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("uploadFile")
@TypeAlias("uploadFile")
@Component
@ConfigurationProperties(prefix = "file")
@Accessors(chain = true)
public class FileUpload extends Base {
    private String fileName;
    private String fileNameOriginal;
    private String origin;

}