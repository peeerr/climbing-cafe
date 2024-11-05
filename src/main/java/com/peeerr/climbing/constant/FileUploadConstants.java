package com.peeerr.climbing.constant;

import java.util.concurrent.TimeUnit;

public final class FileUploadConstants {

    public static final String UPLOAD_STATUS_PREFIX = "upload:status:";
    public static final String UPLOAD_PARTS_PREFIX = "upload:parts:";
    public static final String FAILED_FILES_KEY = "upload:failed:files";

    public static final long UPLOAD_EXPIRY = TimeUnit.HOURS.toSeconds(3); // 3 hours
    public static final long FAILED_FILE_EXPIRY = TimeUnit.HOURS.toSeconds(1);
    public static final int CHUNK_SIZE = 5 * 1024 * 1024; // 5MB

}
