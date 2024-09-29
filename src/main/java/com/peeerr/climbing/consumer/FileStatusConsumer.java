package com.peeerr.climbing.consumer;

import com.peeerr.climbing.constant.Topic;
import com.peeerr.climbing.dto.FileStatusMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FileStatusConsumer {

    @KafkaListener(topics = Topic.FILE_STATUS, groupId = "file-status")
    public void consume(FileStatusMessage message) {
        System.out.println("fileId = " + message.getFileId());
        System.out.println("status = " + message.getStatus().name());
    }

}
