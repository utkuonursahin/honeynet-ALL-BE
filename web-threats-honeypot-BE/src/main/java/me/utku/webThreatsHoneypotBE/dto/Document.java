package me.utku.webThreatsHoneypotBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Document {
    public String name;
    public String text;

}
