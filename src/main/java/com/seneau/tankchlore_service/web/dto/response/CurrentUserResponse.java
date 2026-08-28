package com.seneau.tankchlore_service.web.dto.response;

import java.util.List;

public record CurrentUserResponse(String email, List<String> privileges) {
}
