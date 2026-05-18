package com.internship.documentmanagement.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.InputStream;

public class FileHashUtil {
    public static String calculateSHA256(InputStream inputStream) throws Exception {
        return DigestUtils.sha256Hex(inputStream);
    }
}
