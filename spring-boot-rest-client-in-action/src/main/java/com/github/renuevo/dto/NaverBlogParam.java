package com.github.renuevo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverBlogParam {
    String query;
    int display;
    int start;
    String sort;
}
