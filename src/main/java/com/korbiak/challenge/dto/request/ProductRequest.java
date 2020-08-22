package com.korbiak.challenge.dto.request;

import com.korbiak.challenge.dto.response.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank
    @Size(max = 32)
    private String name;

    @NotBlank
    @Size(max = 225)
    private String description;

    @NotNull
    @Positive
    private double price;

    @NotNull
    private Date releaseDate;

    @NotNull
    private CategoryResponse category;
}
