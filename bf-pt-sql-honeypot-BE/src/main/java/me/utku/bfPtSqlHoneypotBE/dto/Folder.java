package me.utku.bfPtSqlHoneypotBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Folder {
    private Folder[] subFolders;
    private Document[] subDocuments;
    private String name;
}
