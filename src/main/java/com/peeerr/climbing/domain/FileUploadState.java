package com.peeerr.climbing.domain;

public enum FileUploadState {
    INITIATED,
    CHUNKS_SENT,
    ASSEMBLED,
    UPLOADED,
    FAILED
}
