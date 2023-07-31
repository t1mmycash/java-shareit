package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemInItemRequestDto {
    private long id;
    private String name;
    private String description;
    private long ownerId;
    private boolean available;
    private long requestId;

    public ItemInItemRequestDto(long id, String name, String description, long ownerId, boolean available, long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.available = available;
        this.requestId = requestId;
    }
}
