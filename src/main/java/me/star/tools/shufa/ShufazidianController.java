package me.star.tools.shufa;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Star Chou
 * @create 2021/12/25 20:45
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tools")
public class ShufazidianController {

    private final ShufazidianService shufazidianService;

    @PostMapping(value = "/long/collect", consumes = "application/json", produces = "text/html;charset=utf-8")
    public String load(@RequestBody ShufazidianRequestDto requestDto) throws IOException {
        return shufazidianService.loadHtml(requestDto);
    }
}
