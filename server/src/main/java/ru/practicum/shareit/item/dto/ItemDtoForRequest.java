package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoForRequest {
    private long id;
    private String name;
    private long ownerId;
    private String description;
    private Boolean available;
    private Long requestId;
}
