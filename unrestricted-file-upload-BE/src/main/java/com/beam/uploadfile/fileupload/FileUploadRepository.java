package com.beam.uploadfile.fileupload;

import com.beam.uploadfile.fileupload.FileUpload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends MongoRepository<FileUpload,String> {

}