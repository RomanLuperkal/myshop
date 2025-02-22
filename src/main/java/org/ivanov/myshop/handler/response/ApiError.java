package org.ivanov.myshop.handler.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String message;
    private String status;
}
