package me.star.tools.shufa;

import lombok.Data;

/**
 * @author Star Chou
 * @create 2021/12/26 13:24
 */
@Data
public class ShufazidianRequestDto {

    /**
     * 需要转为书法字体单句子或者单字
     */
    private String words;
    /**
     * 字体类型
     */
    private String sort;
}
