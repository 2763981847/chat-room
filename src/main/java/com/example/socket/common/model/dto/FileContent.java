package com.example.socket.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Fu Qiujie
 * @since 2023/11/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileContent implements Serializable {
    private String fileName;
    private byte[] content;
}
