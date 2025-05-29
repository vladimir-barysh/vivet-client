package model;

import java.time.LocalDateTime;

public record Message(
        Long id,
        Long senderId,
        Long recipientId,
        String content,
        String timestamp
) {}