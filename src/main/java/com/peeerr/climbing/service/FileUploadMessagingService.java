package com.peeerr.climbing.service;

import static com.peeerr.climbing.constant.FileUploadConstants.UPLOAD_EXPIRY;
import static com.peeerr.climbing.constant.FileUploadConstants.UPLOAD_PARTS_PREFIX;
import static com.peeerr.climbing.constant.FileUploadConstants.UPLOAD_STATUS_PREFIX;

import com.amazonaws.services.s3.model.PartETag;
import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.constant.Topic;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.dto.FileStatusMessage;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileUploadMessagingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public void saveUploadIdToRedis(String fileId, String uploadId) {
        redisTemplate.opsForValue().set(UPLOAD_STATUS_PREFIX + fileId, uploadId, UPLOAD_EXPIRY, TimeUnit.SECONDS);
    }

    public Optional<String> getUploadIdFromRedis(String fileId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(UPLOAD_STATUS_PREFIX + fileId));
    }

    public void sendFileChunkToKafka(FileChunkMessage message) {
        kafkaTemplate.send(Topic.FILE_CHUNK, message.getFileId(), message);
    }

    public void sendFileStatus(String fileId, FileUploadState state) {
        String destination = "/topic/file-status/" + fileId;
        messagingTemplate.convertAndSend(destination, new FileStatusMessage(fileId, state));
    }

    public void cleanupRedisKeys(String fileId) {
        redisTemplate.delete(UPLOAD_STATUS_PREFIX + fileId);
        redisTemplate.delete(UPLOAD_PARTS_PREFIX + fileId);
    }

    public void savePartETagToRedis(String fileId, int partNumber, PartETag partETag) {
        String key = UPLOAD_PARTS_PREFIX + fileId;
        redisTemplate.opsForHash().put(key, String.valueOf(partNumber), partETag.getETag());
        redisTemplate.expire(key, UPLOAD_EXPIRY, TimeUnit.SECONDS);
    }

    public List<PartETag> getPartETagsFromRedis(String fileId, int totalParts) {
        String key = UPLOAD_PARTS_PREFIX + fileId;
        return IntStream.rangeClosed(1, totalParts)
                .mapToObj(i -> {
                    String eTag = (String) redisTemplate.opsForHash().get(key, String.valueOf(i));
                    return eTag != null ? new PartETag(i, eTag) : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
