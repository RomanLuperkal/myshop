package org.ivanov.myshop.curt;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.curt.dto.CreateCurtDto;
import org.ivanov.myshop.curt.service.CurtService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/curts")
@RequiredArgsConstructor
public class CurtController {
    private final CurtService curtService;

    @PostMapping("/add")
    void addToCurt(@RequestBody CreateCurtDto dto) {
        curtService.addToCurt(dto);
    }
}
