package com.aaryan.coronavirustracker.models.weatherApiModel;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class zipcode {

    @Pattern(regexp = "[0-9]{6}",message = "zipcode is a 6 digit number,make sure the zipcode entered is correct")
    private Integer zip;
}
