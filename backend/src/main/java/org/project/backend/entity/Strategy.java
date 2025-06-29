package org.project.backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Strategy {
    private Long id;
    private String name;
    private String type;        // ASSET_ALLOCATION/FOF/INDEX/TACTICAL_TIMING
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private String status;      // ACTIVE/INACTIVE
}