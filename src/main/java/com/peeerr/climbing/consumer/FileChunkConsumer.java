package com.peeerr.climbing.consumer;

import static com.peeerr.climbing.constant.Topic.FILE_CHUNK;

import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.service.FileChunkAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileChunkConsumer {

    private final FileChunkAssembler fileChunkAssembler;

    @KafkaListener(topics = FILE_CHUNK, groupId = "file-upload")
    public void consume(FileChunkMessage message) {
        fileChunkAssembler.assembleChunk(message);
    }

}
